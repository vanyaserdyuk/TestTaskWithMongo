package ru.testtask.dto;

import lombok.Data;


@Data
public class ChatMessageUserDTO {
    private String content;
    private String sender;
}
