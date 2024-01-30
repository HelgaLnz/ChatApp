package com.example.websocket.groupchatroom;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestGroupChat {

  private String chatName;
  private List<String> groupUsersId;
}
