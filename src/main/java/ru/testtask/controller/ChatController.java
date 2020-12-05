package ru.testtask.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.testtask.model.ChatMessage;
import ru.testtask.service.ChatMessageService;
import ru.testtask.service.ChatRoomService;


@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatMessageService chatMessageService;

    public ChatController(ChatMessageService chatMessageService, ChatRoomService chatRoomService) {
        this.chatMessageService = chatMessageService;
    }


    @GetMapping("/chat/room/{roomId}/messages")
    public ResponseEntity<?> getMessageList(@PathVariable String roomId,
                                            @RequestParam (required = false) Integer page,
                                            @RequestParam (required = false) Integer pageSize){
        if (page == null && pageSize == null){
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "date"));
            return new ResponseEntity<>(chatMessageService.findAllByRoom(pageable, roomId), HttpStatus.OK);
        }
        else {
            if (page == null || pageSize == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "date"));
            Page<ChatMessage> chatMessages = chatMessageService.findAllByRoom(pageable, roomId);
            return new ResponseEntity<>(chatMessages, HttpStatus.OK);
        }
    }

}
