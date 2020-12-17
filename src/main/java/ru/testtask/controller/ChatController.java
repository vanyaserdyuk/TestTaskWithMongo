package ru.testtask.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.testtask.dto.ChatMessageUserDTO;
import ru.testtask.model.ChatMessage;
import ru.testtask.service.ChatMessageService;
import ru.testtask.service.ChatMessageServiceImpl;


@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatMessageService chatMessageService;

    private final ModelMapper modelMapper;

    public ChatController(ChatMessageServiceImpl chatMessageService, ModelMapper modelMapper) {
        this.chatMessageService = chatMessageService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/chat/room/{roomId}/messages")
    public ResponseEntity<?> getMessageList(@PathVariable String roomId,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer pageSize) {
        if (page == null && pageSize == null) {
            Page<ChatMessageUserDTO> chatMessageUserDTOPage = mapChatMessageToDto(chatMessageService
                    .findAllByRoom(roomId));
            return new ResponseEntity<>(chatMessageUserDTOPage, HttpStatus.OK);
        } else {
            if (page == null || pageSize == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Page<ChatMessageUserDTO> chatMessageUserDTOPage = mapChatMessageToDto(chatMessageService
                    .findAllByRoom(roomId, page, pageSize));
            return new ResponseEntity<>(chatMessageUserDTOPage, HttpStatus.OK);
        }
    }

    private Page<ChatMessageUserDTO> mapChatMessageToDto(Page<ChatMessage> chatMessagePage) {
        return chatMessagePage.map(chatMessage ->
                modelMapper.map(chatMessage, ChatMessageUserDTO.class));
    }
}
