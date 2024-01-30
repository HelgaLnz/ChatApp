package com.example.websocket.groupchatroom;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class GroupChatRoom {

  @Id
  private String id;
  private String groupChatId;
  private String moderId;
  private String groupChatName;
  private List<String> chatUsersId;
}
