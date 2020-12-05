package ru.testtask.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.testtask.model.ChatMessage;
import ru.testtask.service.ChatMessageService;
import ru.testtask.service.ChatRoomService;


@RestController
public class ChatController {

    private final ChatMessageService chatMessageService;

    public ChatController(ChatMessageService chatMessageService, ChatRoomService chatRoomService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("/chat/{roomId}/getMessages/{page}")
    public ResponseEntity<?> getMessageList(@PathVariable String roomId, @PathVariable int page,
                                            @RequestParam (required = false) int pageSize){

        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "date"));
        Page<ChatMessage> chatMessages = chatMessageService.findAllByRoom(pageable, roomId);
        return new ResponseEntity<>(chatMessages, HttpStatus.OK);
    }

}
