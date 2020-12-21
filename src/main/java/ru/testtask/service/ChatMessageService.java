package ru.testtask.service;

import org.springframework.data.domain.Page;
import ru.testtask.model.ChatMessage;

public interface ChatMessageService {
    void sendUserMessage(String roomId, String username, String content);
    void sendUserJoinToRoom(String roomId, String username);
    void sendUserLeaveFromRoom(String roomId, String username);
    Page<ChatMessage> findAllByRoom(String roomId);
    Page<ChatMessage> findAllByRoom(String roomId, int page, int pageSize);
}
