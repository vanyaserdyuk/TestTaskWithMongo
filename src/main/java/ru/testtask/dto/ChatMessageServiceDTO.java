package ru.testtask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.testtask.model.MessageType;


@Data
@Builder
@AllArgsConstructor
public class ChatMessageServiceDTO {
    private MessageType type;
    private String username;
}
