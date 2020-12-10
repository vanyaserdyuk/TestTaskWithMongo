package ru.testtask.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.testtask.dto.ChatMessageUserDTO;
import ru.testtask.model.ChatMessage;
import ru.testtask.service.ChatMessageServiceImpl;
import ru.testtask.service.ChatRoomService;


@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatMessageServiceImpl chatMessageServiceImpl;

    private final ModelMapper modelMapper;

    public ChatController(ChatMessageServiceImpl chatMessageServiceImpl, ModelMapper modelMapper) {
        this.chatMessageServiceImpl = chatMessageServiceImpl;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/chat/room/{roomId}/messages")
    public ResponseEntity<?> getMessageList(@PathVariable String roomId,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer pageSize) {
        if (page == null && pageSize == null) {
            Page<ChatMessage> chatMessagePage = chatMessageServiceImpl.findAllByRoom(roomId);
            Page<ChatMessageUserDTO> chatMessageUserDTOPage = chatMessagePage.map(chatMessage ->
                    modelMapper.map(chatMessage, ChatMessageUserDTO.class));
            return new ResponseEntity<>(chatMessageUserDTOPage, HttpStatus.OK);
        } else {
            if (page == null || pageSize == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Page<ChatMessage> chatMessagePage = chatMessageServiceImpl.findAllByRoom(roomId, page, pageSize);
            Page<ChatMessageUserDTO> chatMessageUserDTOPage = chatMessagePage.map(chatMessage ->
                    modelMapper.map(chatMessage, ChatMessageUserDTO.class));
            return new ResponseEntity<>(chatMessageUserDTOPage, HttpStatus.OK);
        }
    }
}
