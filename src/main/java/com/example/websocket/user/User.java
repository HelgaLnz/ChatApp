package com.example.websocket.user;

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
public class User {

  @Id
  private String nickName;
  private String fullName;
  private Status status;
  private String password;
  private List<String> userFriends;
}
