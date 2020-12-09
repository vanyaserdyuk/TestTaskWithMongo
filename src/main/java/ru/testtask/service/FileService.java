package ru.testtask.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.stereotype.Service;
import ru.testtask.dto.UploadFileDTO;
import ru.testtask.exception.FileIsToLargeException;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.model.FileData;
import ru.testtask.repo.FileDataRepo;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    @Value("${storage.root}")
    private String storageRoot;

    private Path storageRootPath;

    private Path temporaryDir;

    @Value("${storage.file.max-size.mb}")
    private long fileMaxSizeMb;

    private final FileDataRepo fileDataRepo;

    public FileService(FileDataRepo fileDataRepo, MongoTemplate mongoTemplate) {
        this.fileDataRepo = fileDataRepo;
    }

    @PostConstruct
    public void init(){
        storageRootPath = Paths.get(storageRoot);
        if (Files.notExists(storageRootPath)){
            try {
                Files.createDirectories(storageRootPath);
            } catch (IOException e) {
                log.error(String.format("It is impossible to create storage root directory : %s", storageRoot), e);
                System.exit(-1);
            }
        }
    }

    public FileData uploadFile(UploadFileDTO uploadFileDTO) throws FileIsToLargeException{
        try {
            URL url = new URL(uploadFileDTO.getFileUrl());
            String filename = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(url.toString());
            temporaryDir = Files.createTempDirectory(storageRootPath, "temp");
            Path path = storageRootPath.resolve(filename);
            File tempFile = uploadFileFromUrl(url);

            FileData fileData = FileData.builder().originalFilename(uploadFileDTO.getFileName())
                    .filename(filename)
                    .size(Files.size(tempFile.toPath()))
                    .type(Files.probeContentType(path))
                    .directory("/")
                    .build();

            fileDataRepo.insert(fileData);
            Files.move(tempFile.toPath(), path);
            Files.deleteIfExists(tempFile.toPath());
            Files.deleteIfExists(temporaryDir);
            return fileData;
        } catch (IOException e) {
            log.error("An error occurred during uploading file");
            return null;
        }
    }

    public List<FileData> getFileListFromDirectory(String directory){
        String regex = "^" + directory;
        return fileDataRepo.findFileDataByRegexpDirectory(regex);
    }

    public void removeFile(String id) throws FileNotFoundException {
        Optional<FileData> optionalFileData = fileDataRepo.findById(id);
            if (optionalFileData.isPresent()){
                try {
                    FileData fileData = optionalFileData.get();
                    Files.deleteIfExists(Paths.get(fileData.getDirectory() + File.separator + fileData.getFilename()));
                    fileDataRepo.deleteById(id);
                } catch (IOException e){
                    log.error("Impossible to remove file", e);
                }
            }
            else {
                throw new FileNotFoundException(String.format("File with id %s does not found", id));
            }
        }


    public List<FileData> searchByRegex(String regex){
        return fileDataRepo.findFileDataByRegexpFilename(regex);
    }

    public FileData moveFile(String id, String directory) throws FileNotFoundException {
        Optional<FileData> optionalFileData = fileDataRepo.findById(id);
        if (optionalFileData.isEmpty()){
            throw new FileNotFoundException(String.format("File with id %s does not found", id));
        }

            FileData fileData = optionalFileData.get();
            Path destDirectory = storageRootPath.resolve(directory);
            Path destination = getDestination(fileData.getFilename(), destDirectory);

        try {
            if (!Files.exists(destDirectory)) {
                Files.createDirectories(destDirectory);
            }

            if (Files.exists(destination)) {
                throw new NameAlreadyExistsException("Already exists");
            }

            Files.move(getFileAbsolutePath(fileData), destination);
        } catch (IOException e) {
            log.error(String.format("An error occurred during moving the file with id %s", id));
        }

        fileData.setDirectory(directory);
        return fileDataRepo.save(fileData);
    }

    public void copyFile(String id, String directory) throws IOException {
        Optional<FileData> optionalFileData = fileDataRepo.findById(id);
        if (optionalFileData.isEmpty()){
            throw new FileNotFoundException(String.format("File with ID %s does not found", id));
        }

        Path destDirectory = Paths.get(storageRoot + File.separator + directory);

        Path destination = getDestination(id, destDirectory);
        FileData fileData = optionalFileData.get();

        try {
            if (!Files.exists(destDirectory)) {
                Files.createDirectories(destDirectory);
            }

            Files.copy(getFileAbsolutePath(fileData), destination);
        } catch (IOException e) {
            log.error(String.format("An error occurred during copying the file with id %s", id));
        }

        String newFileName = fileData.getOriginalFilename() + " (copied) " + Math.random() * 100;
        fileData.setFilename(UUID.randomUUID().toString());
        fileData.setOriginalFilename(newFileName);
        fileData.setId(null);
        fileDataRepo.insert(fileData);
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

    public Path getDestination(String fileName, Path destDirectory){
        return destDirectory.resolve(fileName);
    }

    public void getFileContent(String id, HttpServletResponse response){
        FileData fileData;

        try {
            Optional<FileData> optionalFileData = findFileById(id);
            if (optionalFileData.isPresent()){
                fileData = optionalFileData.get();
                InputStream is = new FileInputStream(fileData.getDirectory() + File.separator + fileData.getFilename());
                IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
            }

        } catch (IOException ex) {
            log.error("Error writing file to output stream. Filename was '{}'", id, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    public Path getFileAbsolutePath(FileData fileData){
        return Paths.get(storageRoot + File.separator + fileData.getDirectory() + File.separator + fileData.getFilename());
    }

    public Path getStorageRootPath(){
        return storageRootPath;
    }

}
