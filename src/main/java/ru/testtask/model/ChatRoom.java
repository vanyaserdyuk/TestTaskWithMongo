package ru.testtask.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
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
    private String roomName;

    @DBRef(lazy = true)
    @Field("chatMessages")
    private List<ChatMessage> chatMessages = new ArrayList<>();


    public void addMessage(ChatMessage chatMessage){
        chatMessages.add(chatMessage);
    }
}
