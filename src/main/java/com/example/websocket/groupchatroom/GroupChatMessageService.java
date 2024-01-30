package com.example.websocket.groupchatroom;

import com.example.websocket.unreadmessages.UnreadMessagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupChatMessageService {

  private final GroupChatMessageRepository chatMessageRepository;
  private final GroupChatService groupChatService;
  private final UnreadMessagesService unreadMessagesService;


  public GroupChatMessage save(GroupChatMessage chatMessage, String chatId) {
    chatMessage.setChatId(chatId);
    chatMessageRepository.save(chatMessage);

    groupChatService.getGroupChatRoom(chatId)
      .getChatUsersId().stream()
      .filter(userId -> !userId.equals(chatMessage.getSenderId()))
      .forEach(userId ->
        unreadMessagesService.addUnReadMessageCount(chatId, userId));

    return chatMessage;
  }

  public List<GroupChatMessage> findChatMessages(
    String chatId
  ) {
    return chatMessageRepository.findByChatId(chatId);
  }
}
