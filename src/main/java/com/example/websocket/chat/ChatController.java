package com.example.websocket.chat;

import com.example.websocket.chatroom.ChatRoomService;
import config.WebSocketConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Import(WebSocketConfig.class)
public class ChatController {

  private final SimpMessageSendingOperations messagingTemplate;
  private final ChatMessageService chatMessageService;
  private final ChatRoomService chatRoomService;

  @MessageMapping("/chat")
  public void processMessage(@Payload ChatMessage chatMessage) {
    ChatMessage savedMsg = chatMessageService.save(chatMessage);

    messagingTemplate.convertAndSendToUser(
      chatMessage.getRecipientId(), "/queue/messages", ChatNotification.builder()
        .id(savedMsg.getId())
        .chatId(savedMsg.getChatId())
        .senderId(chatMessage.getSenderId())
        .recipientId(chatMessage.getRecipientId())
        .content(chatMessage.getContent())
        .build()
    );
  }

  @GetMapping("/messages/{senderId}/{recipientId}")
  public ResponseEntity<List<ChatMessage>> getChatMessages(
    @PathVariable String senderId,
    @PathVariable String recipientId
  ) {
    return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
  }

  @GetMapping("/chatRoom/{senderId}/{recipientId}")
  public ResponseEntity<String> getChatId(
    @PathVariable String senderId,
    @PathVariable String recipientId
  ) {
    var chatId = chatRoomService
      .getChatRoomId(senderId, recipientId, true)
      .orElse(null);
    return ResponseEntity.ok(String.valueOf(chatId));
  }
}
