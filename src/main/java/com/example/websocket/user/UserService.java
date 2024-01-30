package com.example.websocket.user;

import com.example.websocket.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.websocket.user.Status.OFFLINE;
import static com.example.websocket.user.Status.ONLINE;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public void saveUser(User user) {
    user.setStatus(ONLINE);
    userRepository.save(user);
  }

  public void disconnect(User user) {
    userRepository.findById(user.getNickName())
      .ifPresent(storedUser -> {
          storedUser.setStatus(OFFLINE);
          userRepository.save(storedUser);
        }
      );
  }

  public void connect(User user) {
    userRepository.findById(user.getNickName())
      .ifPresent(storedUser -> {
        storedUser.setStatus(ONLINE);
        userRepository.save(storedUser);
      });
  }

  public List<User> findUsers() {
    return userRepository.findAll();
  }

  public List<User> findFriend(User user) {
    return user.getUserFriends()
      .stream()
      .map(nickName -> userRepository.findById(nickName)
        .orElse(null))
      .collect(Collectors.toList());
  }

}
