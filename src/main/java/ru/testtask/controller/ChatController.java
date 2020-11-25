package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.testtask.model.ChatMessage;
import ru.testtask.service.MessageService;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/chat/{roomId}/sendMessage")
    public ResponseEntity<?> getMessageList(@PathVariable String roomId,
                                            @PageableDefault(size = 15, direction = Sort.Direction.DESC)
                                                    Pageable pageable){
        Page<ChatMessage> chatMessages = messageService.findForRoom(pageable, roomId);
        return new ResponseEntity<Page<ChatMessage>>(chatMessages, HttpStatus.OK);
    }

}
