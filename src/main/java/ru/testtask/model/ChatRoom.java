package ru.testtask.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "chatRooms")
@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {
    private String id;

    @NonNull
    private String roomId;

    @DBRef(lazy = true)
    private List<ChatMessage> chatMessages;
}
