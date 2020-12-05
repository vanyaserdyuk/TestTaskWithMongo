package ru.testtask.dto;

import lombok.Data;
import ru.testtask.model.MessageType;


@Data
public class ChatMessageDTO {
    private String content;
    private String sender;
    private MessageType type;
    private String roomId;
}
