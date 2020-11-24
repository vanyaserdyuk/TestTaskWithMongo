package ru.testtask.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.testtask.model.FileData;
import ru.testtask.service.FileService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("api/files")
public class FileController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private FileService fileService;

    @PostMapping
    public void uploadFile(@RequestParam("file") String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        String fileName = FilenameUtils.getName(url.getPath());
        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        Path path = Paths.get(uploadDir + File.separator + fileName);
        FileUtils.copyURLToFile(url, path.toFile());
        fileService.addFile(path);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<?> removeFile(@PathVariable String filename) throws IOException {
         if (fileService.findByFilename(filename) != null)
         fileService.removeFile(filename);
         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/find/{regex}")
    public ResponseEntity<List<FileData>> searchFileWithRegex(@PathVariable String regex){
        return new ResponseEntity<>(fileService.searchByRegex(regex), HttpStatus.OK);
    }

    @GetMapping("/**")
    public ResponseEntity<List<Path>> getAllFiles(HttpServletRequest request) throws IOException {
        String directory = request.getRequestURI()
                .split(request.getContextPath() + "api/files/")[1];
        Path path = Paths.get(directory);
        List<Path> fileList = fileService.getFileListFromDirectory(path);
        return new ResponseEntity<>(fileList, HttpStatus.OK);
    }

    @PutMapping("/{filename}")
    public ResponseEntity<FileData> moveFile(@PathVariable String filename, @RequestParam("dir") String directory)
            throws IOException {

        Path destination = Path.of(uploadPath + File.separator + directory);

        fileService.moveFile(filename, destination);
        return new ResponseEntity<FileData>(fileService.findByFilename(filename), HttpStatus.OK);
    }

    @PostMapping("/copy/{filename}")
    public ResponseEntity<String> copyFile(@PathVariable String filename, @RequestParam("dir") String directory) throws IOException {

        Path destination = Path.of(uploadPath + File.separator + directory);

        fileService.copyFile(filename, destination);

        return new ResponseEntity<>(String.format("File was copied to %s", destination), HttpStatus.OK);
    }
}
