package com.example.websocket.chatroom;

import com.example.websocket.unreadmessages.UnreadMessagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

  private final ChatRoomRepository repository;
  private final UnreadMessagesService unreadMessagesService;

  public Optional<String> getChatRoomId(
    String senderId,
    String recipientId,
    boolean createNewRoomIfNotExist){
    return repository.findBySenderIdAndRecipientId(senderId, recipientId)
      .map(ChatRoom::getChatId)
      .or(() -> {
        if (createNewRoomIfNotExist){
          var chatId = createChat(senderId, recipientId);
          return Optional.of(chatId);
        }
        return Optional.empty();
      });
  }

  private String createChat(String senderId, String recipientId) {
    var chatId = String.format("%s_%s", senderId, recipientId);
    ChatRoom senderRecipient = ChatRoom.builder()
      .chatId(chatId)
      .senderId(senderId)
      .recipientId(recipientId)
      .build();

    ChatRoom recipientSender = ChatRoom.builder()
      .chatId(chatId)
      .senderId(recipientId)
      .recipientId(senderId)
      .build();
    repository.save(senderRecipient);
    repository.save(recipientSender);
    unreadMessagesService.create(chatId, senderId);
    unreadMessagesService.create(chatId, recipientId);
    return chatId;
  }
}
