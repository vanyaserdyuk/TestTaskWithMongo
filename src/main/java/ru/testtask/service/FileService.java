package ru.testtask.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Service;
import ru.testtask.dto.UploadFileDTO;
import ru.testtask.exception.FileIsToLargeException;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.model.FileData;
import ru.testtask.repo.FileDataRepo;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
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

    @Value("${storage.maxSize}")
    private long storageMaxSize;

    private final FileDataRepo fileDataRepo;

    private final MongoTemplate mongoTemplate;

    public FileService(FileDataRepo fileDataRepo, MongoTemplate mongoTemplate) {
        this.fileDataRepo = fileDataRepo;
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void init(){
        mongoTemplate.indexOps("files").ensureIndex(new Index("filename", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps("files").ensureIndex(new Index("originalFilename", Sort.Direction.ASC).unique());
        if (Files.notExists(Paths.get(storageRoot))) {
            try {
                Files.createDirectory(Paths.get(storageRoot));
            } catch (IOException e) {
                log.info("Exception");
            }
        }
    }

    public FileData addFile(UploadFileDTO uploadFileDTO) {
        try {
            URL url = new URL(uploadFileDTO.getFileUrl());

            if (getFileSize(url) > storageMaxSize)
                throw new FileIsToLargeException("This file is too large!");

            Path temporaryPath = Files.createTempDirectory("temporaryPath").resolve(uploadFileDTO.getFileName());
            String filename = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(url.toString());
            Path path = Paths.get(storageRoot + File.separator + filename);
            FileUtils.copyURLToFile(url, temporaryPath.toFile());


            FileData fileData = FileData.builder().originalFilename(uploadFileDTO.getFileName())
                    .filename(filename)
                    .size(Files.size(temporaryPath))
                    .type(Files.probeContentType(path))
                    .directory(storageRoot)
                    .build();

            fileDataRepo.insert(fileData);
            Files.move(temporaryPath, path);
            temporaryPath.toFile().deleteOnExit();
            return fileData;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<FileData> getFileListFromDirectory(String directory){
        String regex = File.separator + directory + File.separator;
        return fileDataRepo.findFileDataByRegexpDirectory(regex);
    }

    public void removeFile(String id){
        Optional<FileData> fileData = fileDataRepo.findById(id);
        try {
            Files.deleteIfExists(Paths.get(fileData.get().getDirectory() + File.separator + fileData.get().getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileDataRepo.deleteById(id);
    }

    public FileData findByFilename(String filename){
        return fileDataRepo.findByFilename(filename);
    }

    public List<FileData> searchByRegex(String regex){
        return fileDataRepo.findFileDataByRegexpFilename(regex);
    }

    public FileData moveFile(String id, String directory) {
        Path destDirectory = Paths.get(storageRoot + File.separator + directory);
        Optional<FileData> optionalFileData = fileDataRepo.findById(id);

        FileData fileData = optionalFileData.get();

        Path destination = Paths.get(destDirectory + File.separator + fileData.getFilename());

        try {
            if (!Files.exists(destDirectory)) {
                Files.createDirectory(destDirectory);
            }

            if (Files.exists(destination)) {
                throw new NameAlreadyExistsException("Already exists");
            }

            Files.move(Paths.get(fileData.getDirectory() + File.separator + fileData.getFilename()),
                    destination);
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileData.setDirectory(destDirectory.toString());
        return fileDataRepo.save(fileData);
    }

    public void copyFile(String id, String directory) throws IOException {
        Path destDirectory = Paths.get(storageRoot + File.separator + directory);
        Optional<FileData> optionalFileData = fileDataRepo.findById(id);

        FileData fileData = optionalFileData.get();

        Path destination = destDirectory.resolve(fileData.getFilename());

        try {
            if (!Files.exists(destDirectory)) {
                Files.createDirectory(destDirectory);
            }

            Files.copy(Paths.get(fileData.getDirectory() + File.separator + fileData.getFilename()),
                    destination);
        } catch (IOException e) {
            e.printStackTrace();
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

    public long getFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            return conn.getContentLengthLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void deleteAllFiles(){
        fileDataRepo.deleteAll();
    }
}
