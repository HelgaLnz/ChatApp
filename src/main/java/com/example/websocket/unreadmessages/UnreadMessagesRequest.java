package com.example.websocket.unreadmessages;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnreadMessagesRequest {

  private String chatId;
  private String recipientId;
}
