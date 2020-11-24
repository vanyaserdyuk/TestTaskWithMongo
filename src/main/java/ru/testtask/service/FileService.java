package ru.testtask.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Service;
import ru.testtask.model.FileData;
import ru.testtask.repo.FileDataRepo;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService extends SimpleFileVisitor<Path> {

    @Value("${upload.path}")
    private String uploadPath;

    private final FileDataRepo fileDataRepo;

    private final MongoTemplate mongoTemplate;

    public FileService(FileDataRepo fileDataRepo, MongoTemplate mongoTemplate) {
        this.fileDataRepo = fileDataRepo;
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void init(){
        mongoTemplate.indexOps("files").ensureIndex(new Index("filename", Sort.Direction.ASC).unique());
    }

    public void addFile(Path path){
        try {
            FileData fileData = FileData.builder().originalFilename(path.toString())
                    .filename(path.getFileName().toString())
                    .size(Files.size(path))
                    .type(Files.probeContentType(path))
                    .directory(uploadPath)
                    .build();

            fileDataRepo.insert(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Path> getFileListFromDirectory(Path path) throws IOException {

        List<Path> paths = new ArrayList<>();

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!Files.isDirectory(file))
                paths.add(file);
                return super.visitFile(path, attrs);
            }
        });

        return paths;
    }

    public void removeFile(String filename){
        fileDataRepo.deleteByFilename(filename);
    }

    public FileData findByFilename(String filename) throws IOException {
        Files.walkFileTree(Paths.get(uploadPath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().equals(filename))
                    Files.delete(file);
                return super.visitFile(file, attrs);
            }
        });
        return fileDataRepo.findByFilename(filename);
    }

    public List<FileData> searchByRegex(String regex){
        List<FileData> files = fileDataRepo.findAll();
        List<FileData> appropriateFiles = new ArrayList<>();

        for (FileData fileData : files){
            if (fileData.getFilename().matches("(.*)" + regex + "(.*)"))
                appropriateFiles.add(fileData);
        }

        return appropriateFiles;
    }

    public void moveFile(String filename, Path destination) throws IOException {

        if (!Files.exists(destination)){
            Files.createDirectory(destination);
        }

        Files.walkFileTree(Paths.get(uploadPath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().equals(filename)) {
                    Files.move(file, destination.resolve(file.getFileName()));
                }
                return super.visitFile(file, attrs);
            }
        });

        FileData fileData = fileDataRepo.findByFilename(filename);
        fileData.setDirectory(destination.toString());
        fileDataRepo.save(fileData);
    }

    public void copyFile(String filename, Path destination) throws IOException {
        String newFilename;
        int i = 0;

        if (!Files.exists(destination)){
            Files.createDirectory(destination);
        }

        FileData fileData = fileDataRepo.findByFilename(filename);

        do {
            newFilename = fileData.getFilename() + "(" + i + ")";
            i++;
        }
        while (fileDataRepo.findByFilename(newFilename) != null);

        fileData.setFilename(newFilename);
        fileDataRepo.insert(fileData);

        Files.walkFileTree(Paths.get(uploadPath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().equals(filename)) {
                    Files.copy(file, destination.resolve(file.getFileName()));
                }
                return super.visitFile(file, attrs);
            }
        });
    }
}
