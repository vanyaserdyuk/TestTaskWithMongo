package ru.testtask.dto;

import lombok.Data;

@Data
public class FileDTO {
    private String id;
    private long size;
    private String type;
    private String originalFilename;
    private String directory;
}
