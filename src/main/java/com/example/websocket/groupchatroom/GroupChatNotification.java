package com.example.websocket.groupchatroom;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupChatNotification {

  private String id;
  private String senderId;
  private String content;
  private String groupChatId;
}
