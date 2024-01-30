package com.example.websocket.unreadmessages;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document()
public class UnreadMessages {

  @Id
  private String id;
  private String chatId;
  private String recipientId;
  private Integer countUnreadMessage;
}
