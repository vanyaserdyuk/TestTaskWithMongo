package ru.testtask.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalTime;

@Document(collection = "chatMessages")
@Data
public class ChatMessage {
    @Id
    private String id;
    private MessageType type;
    private String content;
    private String sender;
    private LocalTime date;

    @DBRef(lazy = true)
    @JsonAlias("roomName")
    private ChatRoom chatRoom;
}
