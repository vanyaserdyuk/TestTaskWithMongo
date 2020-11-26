package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.testtask.model.ChatMessage;
import ru.testtask.service.MessageService;


@RestController
public class ChatController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/chat/{roomId}/getMessages/{page}")
    public ResponseEntity<?> getMessageList(@PathVariable String roomId, @PathVariable int page){

        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "date"));
        Page<ChatMessage> chatMessages = messageService.findForRoom(pageable, roomId);
        return new ResponseEntity<Page<ChatMessage>>(chatMessages, HttpStatus.OK);
    }

}
