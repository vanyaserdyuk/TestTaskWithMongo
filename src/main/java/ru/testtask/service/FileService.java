package ru.testtask.service;

import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.testtask.dto.UploadFileDTO;
import ru.testtask.exception.FileIsToLargeException;
import ru.testtask.model.FileData;
import ru.testtask.repo.FileDataRepo;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileService {

    @Value("${storage.root}")
    private String storageRoot;

    private Path storageRootPath;

    private Path temporaryDir;

    @Value("${storage.file.max-size.mb:500}")
    private long fileMaxSizeMb;

    private final FileDataRepo fileDataRepo;

    public FileService(FileDataRepo fileDataRepo) {
        this.fileDataRepo = fileDataRepo;
    }

    @PostConstruct
    public void init(){
        storageRootPath = Paths.get(storageRoot);
        temporaryDir = storageRootPath.resolve("temp");
        if (Files.notExists(storageRootPath)){
            try {
                Files.createDirectories(temporaryDir);
            } catch (IOException e) {
                log.error(String.format("It is impossible to create storage root directory : %s", storageRoot), e);
                System.exit(-1);
            }
        }
    }

    public FileData uploadFile(UploadFileDTO uploadFileDTO) throws FileIsToLargeException{
        FileData fileData = null;
        File tempFile = null;
        boolean success = false;
        try {
            URL url = new URL(uploadFileDTO.getFileUrl());
            String filename = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(url.toString());
            Path path = storageRootPath.resolve(filename);
            tempFile = uploadFileFromUrl(url);

            fileData = FileData.builder().originalFilename(uploadFileDTO.getFileName())
                    .filename(filename)
                    .size(Files.size(tempFile.toPath()))
                    .type(Files.probeContentType(path))
                    .directory("")
                    .build();

            fileDataRepo.insert(fileData);
            Files.move(tempFile.toPath(), path);

            success = true;
            return fileData;
        } catch (IOException e) {
            log.error("An error occurred during uploading file", e);
            return null;
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                }
                catch (IOException e){
                    log.error("Impossible to delete tempFile", e);
                }
            }
            if (!success && fileData != null){
                fileDataRepo.deleteById(fileData.getId());
            }
        }
    }

    public List<FileData> getFileListFromDirectory(String directory) throws InvalidPathException{
        String path = normalizeDirectory(directory);
        return fileDataRepo.findFileDataByRegexpDirectory(path);
    }

    public void removeFile(String id) throws FileNotFoundException {
        FileData fileData = getFileById(id);
                try {
                    Files.deleteIfExists(getFileAbsolutePath(fileData));
                    fileDataRepo.deleteById(id);
                } catch (IOException e){
                    log.error("Impossible to remove file", e);
                }
        }

    public List<FileData> searchByRegex(String regex){
        return fileDataRepo.findFileDataByRegexpFilename(regex);
    }

    public FileData moveFile(String id, String directory) throws FileNotFoundException {
        String path = normalizeDirectory(directory);

        boolean success = false;

        FileData fileData = getFileById(id);
        Path destDirectory = storageRootPath.resolve(path);
        Path destination = destDirectory.resolve(fileData.getFilename());
        String sourceDirectory = fileData.getDirectory();
        Path sourcePath = getFileAbsolutePath(fileData);

        try {
            if (!Files.exists(destDirectory)) {
                Files.createDirectories(destDirectory);
            }

            fileData.setDirectory(path);
            fileDataRepo.save(fileData);
            Files.move(sourcePath, destination);
            success = true;

        } catch (IOException e) {
            log.error(String.format("An error occurred during moving the file with id %s", id), e);
        } catch (MongoWriteException e) {
            log.error("File with the same directory and filename already exists!", e);
        }finally {
          if (!success) {
              fileData.setDirectory(sourceDirectory);
              fileDataRepo.save(fileData);
            }
        }
        return fileData;
    }

    public FileData copyFile(String id, String directory) throws FileNotFoundException, InvalidPathException {
        String path = normalizeDirectory(directory);
        String newOriginalFileName;
        FileData copiedFileData = null;
        int i = 0;
        FileData fileData = getFileById(id);
        boolean success = false;

        Path destDirectory = Paths.get(storageRoot).resolve(path);
        String oldOriginalFilename = fileData.getOriginalFilename();
        String oldId = fileData.getId();
        String sourceDirectory = fileData.getDirectory();
        String newFilename = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(fileData.getFilename());
        Path sourcePath = getFileAbsolutePath(fileData);
        fileData.setFilename(newFilename);
        Path destination = destDirectory.resolve(fileData.getFilename());


        try {
            if (!Files.exists(destDirectory)) {
                Files.createDirectories(destDirectory);
            }

            Files.copy(sourcePath, destination);

            List<FileData> fileDataList = fileDataRepo.findFileDataByRegexpFilename(FilenameUtils
                    .removeExtension(fileData.getOriginalFilename()));
            List<String> fileDataNamesList = fileDataList.stream().map(FileData::getOriginalFilename).collect(Collectors.toList());
            String oldName = fileData.getOriginalFilename();

            do {
                newOriginalFileName = buildOriginalFilenameWhileCopy(oldName, i);
                i++;
            }
            while (fileDataNamesList.contains(newOriginalFileName));

            for (int j = 0; j < 15; j++) {
                try {
                    fileData.setOriginalFilename(newOriginalFileName);
                    fileData.setId(null);
                    fileData.setDirectory(path);
                    copiedFileData = fileDataRepo.insert(fileData);
                    break;
                } catch (Exception e) {
                    i++;
                    newOriginalFileName = buildOriginalFilenameWhileCopy(oldName, i);
                }
            }
            success = true;
        } catch (IOException e) {
            log.error(String.format("An error occurred during moving the file with id %s", id), e);
            throw new RuntimeException("");
        } finally {
            if (!success) {
                fileData.setDirectory(sourceDirectory);
                fileData.setOriginalFilename(oldOriginalFilename);
                fileData.setId(oldId);
                fileDataRepo.save(fileData);
            }
        }

        return copiedFileData;
    }

    private String buildOriginalFilenameWhileCopy(String filename, int index){
        return String.format("%s (%d).%s",
                FilenameUtils.removeExtension(filename), index, FilenameUtils.getExtension(filename));
    }

    public Optional<FileData> findFileById(String id){
        return fileDataRepo.findById(id);
    }

    public File uploadFileFromUrl(URL url) {
        long fileMaxSizeByte = fileMaxSizeMb * 1048576;

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");

            if (conn.getContentLengthLong() < fileMaxSizeByte) {

                File tempFile = File.createTempFile("tmp", null,
                        new File(temporaryDir.toString()));
                InputStream is = url.openStream();
                OutputStream fos = new FileOutputStream(tempFile);
                IOUtils.copy(is, fos);
                fos.close();
                return tempFile;
            }
            else {
                throw new FileIsToLargeException(String.format("The file with URL %s is too large!", url.toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void getFileContent(String id, HttpServletResponse response) throws FileNotFoundException {
        FileData fileData = getFileById(id);

        try {
            InputStream is = new FileInputStream(String.valueOf(getFileAbsolutePath(fileData)));
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            log.error("Error writing file to output stream. Filename was '{}'", id, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    public Path getFileAbsolutePath(FileData fileData){
        return Paths.get(storageRoot, fileData.getDirectory(), fileData.getFilename());
    }

    public Path getStorageRootPath(){
        return storageRootPath;
    }

    public FileData getFileById(String id) throws FileNotFoundException {
        Optional<FileData> optionalFileData = fileDataRepo.findById(id);
        if (optionalFileData.isPresent()) {
            return optionalFileData.get();
        } else {
            throw new FileNotFoundException(String.format("File with id %s does not found", id));
        }
    }


    private String normalizeDirectory(String directory) throws InvalidPathException {
        if (directory.isEmpty()) {
            return "";
        }

        Path path;
        String pathName = FilenameUtils.separatorsToWindows(directory);

            path = Paths.get(pathName).normalize();

        String newPathName = FilenameUtils.separatorsToUnix(path.toString());

        if (newPathName.startsWith("/")) {
            newPathName = newPathName.replaceFirst("/", "");
        }

        System.out.println(newPathName);
        return newPathName;
    }

}
