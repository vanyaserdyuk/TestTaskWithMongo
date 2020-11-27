package ru.testtask.model;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chatRooms")
@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {
    private String id;

    @NonNull
    @Indexed(unique = true)
    private String roomName;
}
