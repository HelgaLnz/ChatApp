package com.example.websocket.unreadmessages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnreadMessagesService {

  private final UnreadMessagesRepository unReadMessagesRepository;

  public Integer getCountUnReadMessage(String chatId, String recipientId) {
    return unReadMessagesRepository
      .getByChatIdAndRecipientId(chatId, recipientId)
      .stream()
      .filter(unreadMessages -> recipientId.equals(unreadMessages.getRecipientId()))
      .findFirst().orElseThrow().getCountUnreadMessage();
  }

  public UnreadMessages create(String chatId, String recipientId) {
    UnreadMessages unreadMessages = UnreadMessages.builder()
      .chatId(chatId)
      .recipientId(recipientId)
      .countUnreadMessage(0)
      .build();

    return unReadMessagesRepository.save(unreadMessages);
  }

  @Transactional()
  public void addUnReadMessageCount(String chatId, String recipientId) {
    unReadMessagesRepository.getByChatIdAndRecipientId(chatId, recipientId).stream()
      .filter(unreadMessages -> recipientId.equals(unreadMessages.getRecipientId()))
      .forEach(unreadMessages -> {
        var currentCountUnreadMessage = unreadMessages.getCountUnreadMessage();
        unreadMessages.setCountUnreadMessage(currentCountUnreadMessage + 1);
        unReadMessagesRepository.save(unreadMessages);
      });
  }

  @Transactional()
  public void setAsRead(String chatId, String recipientId) {
    var a = unReadMessagesRepository.getByChatIdAndRecipientId(chatId, recipientId).stream()
      .filter(unreadMessages -> recipientId.equals(unreadMessages.getRecipientId())).toList();
    for (var message :
      a) {
      message.setCountUnreadMessage(0);
      unReadMessagesRepository.save(message);
    }
  }
}
