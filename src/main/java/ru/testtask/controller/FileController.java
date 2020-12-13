package ru.testtask.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.testtask.dto.FileDTO;
import ru.testtask.dto.UploadFileDTO;
import ru.testtask.exception.FileIsToLargeException;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.model.FileData;
import ru.testtask.service.FileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

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
            FileData fileData = fileService.uploadFile(uploadFileDTO);
            return new ResponseEntity<>(fileData, HttpStatus.OK);
        }
        catch (FileIsToLargeException e){
            return new ResponseEntity<>("Impossible to upload this file", HttpStatus.PAYLOAD_TOO_LARGE);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFile(@PathVariable String id){
        try {
            fileService.removeFile(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (FileNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/find/regexp")
    public ResponseEntity<List<FileDTO>> searchFileWithRegex(@RequestParam String regexp){
        List<FileData> fileDatas = fileService.searchByRegex(regexp);
        List<FileDTO> fileDTOS = fileDatas.stream().map(fileData -> modelMapper.map(fileData, FileDTO.class)).collect(Collectors.toList());
        return new ResponseEntity<>(fileDTOS, HttpStatus.OK);
    }

    @GetMapping("/find/dir")
    public ResponseEntity<List<FileDTO>> getAllFiles(@RequestParam String directory){
        List<FileData> fileList = fileService.getFileListFromDirectory(directory);
        List<FileDTO> fileDTOS = fileList.stream().map(user -> modelMapper.map(fileList, FileDTO.class)).collect(Collectors.toList());
        return new ResponseEntity<>(fileDTOS, HttpStatus.OK);
    }

    @PutMapping("/{id}/move")
    public ResponseEntity<?> moveFile(@PathVariable String id, @RequestParam String directory) {
        FileData fileData;

        try {
            fileData = fileService.moveFile(id, directory);
        } catch (NameAlreadyExistsException e) {
            return new ResponseEntity<>(String.format("File with the same name is already exists in directory %s", directory),
                    HttpStatus.CONFLICT);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(modelMapper.map(fileData, FileDTO.class)
                , HttpStatus.OK);

    }

    @PostMapping("/{id}/copy")
    public ResponseEntity<String> copyFile(@PathVariable String id,
                                           @RequestParam String directory) throws IOException {

        try {
            fileService.copyFile(id, directory);
            return new ResponseEntity<>(String.format("File with id %s was copied to %s", id, directory)
                    , HttpStatus.OK);
        } catch (FileNotFoundException e){
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<String> getFileContent(@PathVariable("id") String id,
            HttpServletResponse response) {
            try {
                fileService.getFileContent(id, response);
                return new ResponseEntity<>("File was successfully downloaded", HttpStatus.OK);
            }
            catch (FileNotFoundException e){
                return new ResponseEntity<>(String.format("File with ID %s does not found", id),
                        HttpStatus.NOT_FOUND);
            }
    }


}
