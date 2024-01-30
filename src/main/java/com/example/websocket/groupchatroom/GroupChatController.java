package com.example.websocket.groupchatroom;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Import(WebSocketConfig.class)
public class GroupChatController {

  private final SimpMessageSendingOperations messagingTemplate;
  private final GroupChatMessageService service;
  private final GroupChatService groupChatService;

  @MessageMapping("/groupChat")
  public void processMessage(@Payload GroupChatMessage chatMessage) {
    GroupChatMessage savedMsg = service.save(chatMessage, chatMessage.getChatId());
    GroupChatRoom chatRoom = groupChatService.getGroupChatRoom(chatMessage.getChatId());

    for (var usersId : chatRoom
      .getChatUsersId()) {
      messagingTemplate.convertAndSendToUser(
        usersId,"/queue/groupMessages",
        GroupChatNotification.builder()
          .id(savedMsg.getId())
          .groupChatId(savedMsg.getChatId())
          .senderId(chatMessage.getSenderId())
          .content(chatMessage.getContent())
          .build()
      );
    }
  }

  @GetMapping("/groupMessages/{chatId}")
  public ResponseEntity<List<GroupChatMessage>> getChatMessage(
    @PathVariable String chatId
  ) {
    return ResponseEntity.ok(service.findChatMessages(chatId));
  }

  @GetMapping("/groups/{userId}")
  public ResponseEntity<List<GroupChatRoom>> getGroupRooms(@PathVariable String userId) {
    return ResponseEntity.ok(groupChatService.getGroupChatRooms(userId));
  }

  @PostMapping("/groups/create/{userId}")
  public ResponseEntity<String> createGroupChatRoom(
    @PathVariable String userId,
    @RequestBody RequestGroupChat payloadGroupRoom
  ) {
    return ResponseEntity.ok(groupChatService.createGroupChat
      (
        userId,
        payloadGroupRoom.getChatName(),
        payloadGroupRoom.getGroupUsersId()
      ));
  }
}
