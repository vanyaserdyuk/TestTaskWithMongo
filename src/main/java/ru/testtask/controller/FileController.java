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
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/find/regexp/{regexp}")
    public ResponseEntity<List<FileDTO>> searchFileWithRegex(@PathVariable String regexp){
        List<FileData> fileDatas = fileService.searchByRegex(regexp);
        List<FileDTO> fileDTOS = fileDatas.stream().map(fileData -> modelMapper.map(fileData, FileDTO.class)).collect(Collectors.toList());
        return new ResponseEntity<>(fileDTOS, HttpStatus.OK);
    }

    @GetMapping("/find/dir/{directory}")
    public ResponseEntity<List<FileDTO>> getAllFiles(@PathVariable String directory){
        List<FileData> fileList = fileService.getFileListFromDirectory(directory);
        List<FileDTO> fileDTOS = fileList.stream().map(user -> modelMapper.map(fileList, FileDTO.class)).collect(Collectors.toList());
        return new ResponseEntity<>(fileDTOS, HttpStatus.OK);
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
        catch (FileNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<FileData> optionalFileData = fileService.findFileById(id);

        if (optionalFileData.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(modelMapper.map(optionalFileData.get(), FileDTO.class)
                    , HttpStatus.OK);
        }
    }

    @PostMapping("/{id}/copy/{directory}")
    public ResponseEntity<String> copyFile(@PathVariable String id,
                                           @PathVariable String directory) throws IOException {

        fileService.copyFile(id, directory);
        return new ResponseEntity<>(String.format("File was copied to %s", directory), HttpStatus.OK);
    }

    @GetMapping("/{id}/download")
    public void getFileContent(@PathVariable("id") String id,
            HttpServletResponse response) {
            fileService.getFileContent(id, response);
    }
}
