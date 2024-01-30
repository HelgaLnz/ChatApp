package com.example.websocket.service;

import com.example.websocket.ChatApplicationTests;
import com.example.websocket.user.Status;
import com.example.websocket.user.User;
import com.example.websocket.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static com.mongodb.assertions.Assertions.assertTrue;

@SpringBootTest
public class UserServiceTest extends ChatApplicationTests {

  @InjectMocks
  @Autowired
  private UserService userService;


  private List<User> getUsers() {
    return Stream.of(User.builder()
        .status(Status.OFFLINE)
        .fullName("Nick Asc Awe")
        .nickName("02ea8ced-1eb1-4d46-817f-c5016d0b989c")
        .build(),
      User.builder()
        .status(Status.ONLINE)
        .fullName("Astrai Qwera")
        .nickName("67900223-a1d2-462f-a3ec-8505603146a6")
        .build()
    ).toList();
  }

  @Test
  public void testSaveUser_WithUserObject_ExpectUserObjectSaved() {
    List<User> users = userService.findUsers();

    User expectedUser = getUsers().get(0);
    assertTrue(expectedUser.getFullName().equals(users.get(2).getFullName()));
  }

  @Test
  public void testDisconnect_WithUserObject_ExpectedUserStatusIsOffline() {
    User user = getUsers().get(1);
    User userWithStatusOnline = User.builder()
      .nickName(user.getNickName())
      .fullName(user.getFullName())
      .status(Status.ONLINE)
      .build();

    userService.disconnect(userWithStatusOnline);

    User expectedUserStatus =
      User.builder()
        .fullName(user.getFullName())
        .nickName(user.getNickName())
        .status(Status.OFFLINE)
        .build();

    User receivedUser = userService.findUsers().get(3);

    assertTrue(receivedUser.getStatus().equals(expectedUserStatus.getStatus()));
  }

  @Test
  public void testConnect_WithUserObject_ExpectedUserStatusIsOnline() {
    User user = getUsers().get(1);
    User userWithStatusOffline = User.builder()
      .nickName(user.getNickName())
      .fullName(user.getFullName())
      .status(Status.OFFLINE)
      .build();

    userService.connect(userWithStatusOffline);

    User expectedUserStatus =
      User.builder()
        .fullName(user.getFullName())
        .nickName(user.getNickName())
        .status(Status.ONLINE)
        .build();

    User receivedUser = userService.findUsers().get(3);

    assertTrue(receivedUser.getStatus().equals(expectedUserStatus.getStatus()));
  }

}
