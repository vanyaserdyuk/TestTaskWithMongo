package ru.testtask.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalTime;
import java.util.Date;

@Document(collection = "chatMessages")
@Data
public class ChatMessage {
    @Id
    private String id;
    private MessageType type;
    private String content;
    private String sender;
    private LocalTime date;


    private String roomName;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
