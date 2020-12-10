package ru.testtask.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.testtask.dto.ChatRoomDTO;
import ru.testtask.model.ChatRoom;
import ru.testtask.service.ChatRoomService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    private final ModelMapper modelMapper;

    public ChatRoomController(ChatRoomService chatRoomService, ModelMapper modelMapper) {
        this.chatRoomService = chatRoomService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/chat/rooms")
    public ResponseEntity<List<ChatRoomDTO>> getAllChatRooms(){
        List<ChatRoom> chatRooms = chatRoomService.getAllRooms();
        return new ResponseEntity<>(chatRooms.stream().map(chatRoom -> modelMapper.map(chatRoom, ChatRoomDTO.class))
                .collect(Collectors.toList())
                ,HttpStatus.OK);
    }

    @PostMapping("/chat/rooms")
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestParam String roomName){
        ChatRoom chatRoom = chatRoomService.addChatRoomIfNotExists(roomName);
        return new ResponseEntity<>(modelMapper.map(chatRoom, ChatRoomDTO.class), HttpStatus.OK);
    }
}
