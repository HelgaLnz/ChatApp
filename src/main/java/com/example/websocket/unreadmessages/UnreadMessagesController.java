package com.example.websocket.unreadmessages;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class UnreadMessagesController {

  private final UnreadMessagesService unReadMessagesService;

  @GetMapping("/unread/messages/count/{chatId}/{recipientId}")
  public ResponseEntity<Integer> getCountUnreadMessage(@PathVariable String chatId, @PathVariable String recipientId) {
    return ResponseEntity.ok(unReadMessagesService.getCountUnReadMessage(chatId, recipientId));
  }

  @PatchMapping("/unread/messages/set/read")
  public ResponseEntity<?> setMessagesAsRead(@RequestBody UnreadMessagesRequest request) {
    unReadMessagesService.setAsRead(request.getChatId(), request.getRecipientId());
    return ResponseEntity.ok(0);
  }
}
