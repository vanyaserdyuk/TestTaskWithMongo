package ru.testtask.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.testtask.dto.FileDTO;
import ru.testtask.dto.UploadFileDTO;
import ru.testtask.exception.FileIsToLargeException;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.model.FileData;
import ru.testtask.service.FileService;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("api/files")
public class FileController {

    private final FileService fileService;

    private final ModelMapper modelMapper;

    public FileController(FileService fileService, ModelMapper modelMapper) {
        this.fileService = fileService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestBody UploadFileDTO uploadFileDTO){
        try {
            FileData fileData = fileService.addFile(uploadFileDTO);
            return new ResponseEntity<>(modelMapper.map(fileData, FileDTO.class), HttpStatus.OK);
        }
        catch (FileIsToLargeException e){
            return new ResponseEntity<>("Impossible to upload this file", HttpStatus.PAYLOAD_TOO_LARGE);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFile(@PathVariable String id){
         if (fileService.findFileById(id).isPresent()) {
             fileService.removeFile(id);
         }
         else return new ResponseEntity<>(String.format("File with ID %s does not found", id),
                 HttpStatus.NOT_FOUND);
         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/find/{regex}")
    public ResponseEntity<List<FileData>> searchFileWithRegex(@PathVariable String regex){
        return new ResponseEntity<>(fileService.searchByRegex(regex), HttpStatus.OK);
    }

    @GetMapping("/find/dir/{directory}")
    public ResponseEntity<List<FileData>> getAllFiles(@PathVariable String directory){
        List<FileData> fileList = fileService.getFileListFromDirectory(directory);
        return new ResponseEntity<>(fileList, HttpStatus.OK);
    }

    @PutMapping("/{id}/move/{directory}")
    public ResponseEntity<?> moveFile(@PathVariable String id, @PathVariable String directory) {
        try {
            fileService.moveFile(id, directory);
        }
        catch (NameAlreadyExistsException e){
            return new ResponseEntity<>(String.format("File with the same name is already exists in directory %s", directory),
                    HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(modelMapper.map(modelMapper.map(fileService.findFileById(id), FileDTO.class), FileDTO.class)
                ,HttpStatus.OK);
    }

    @PostMapping("{id}/copy/{directory}")
    public ResponseEntity<String> copyFile(@PathVariable String id,
                                           @PathVariable String directory) throws IOException {

        fileService.copyFile(id, directory);

        return new ResponseEntity<>(String.format("File was copied to %s", directory), HttpStatus.OK);
    }

    @GetMapping("/download/{id}")
    public void getFile(@PathVariable("id") String id,
            HttpServletResponse response) {
            FileData fileData;

        try {
            Optional<FileData> optionalFileData = fileService.findFileById(id);
            if (optionalFileData.isPresent()){
                fileData = optionalFileData.get();
                InputStream is = new FileInputStream(fileData.getDirectory() + File.separator + fileData.getFilename());
                IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
            }

        } catch (IOException ex) {
            log.info("Error writing file to output stream. Filename was '{}'", id, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }
}
