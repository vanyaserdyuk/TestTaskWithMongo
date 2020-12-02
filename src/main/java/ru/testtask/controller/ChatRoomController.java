package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.testtask.model.ChatRoom;
import ru.testtask.service.ChatRoomService;

import java.util.List;

@RestController
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping("/chat/getRooms")
    public ResponseEntity<List<ChatRoom>> getAllChatRooms(){
        return new ResponseEntity<>(chatRoomService.getAllRooms(), HttpStatus.OK);
    }

}
