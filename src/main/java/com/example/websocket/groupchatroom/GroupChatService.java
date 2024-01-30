package com.example.websocket.groupchatroom;

import com.example.websocket.unreadmessages.UnreadMessagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupChatService {

  private final GroupChatRoomRepository repository;
  private final UnreadMessagesService unreadMessagesService;

  public Optional<String> getGroupChatRoomId(String userId, String chatId) {
    return repository.findByChatUsersIdAndGroupChatName(List.of(userId), chatId)
      .map(GroupChatRoom::getId);
  }

  public String createGroupChat(String moderId, String chatName, List<String> groupUsersId) {
    var chatId = String.format("%s_%s", moderId, chatName);
    groupUsersId.add(moderId);
    GroupChatRoom groupChatRoom = GroupChatRoom.builder()
      .moderId(moderId)
      .groupChatId(chatId)
      .chatUsersId(groupUsersId)
      .groupChatName(chatName)
      .build();

    repository.save(groupChatRoom);
    groupUsersId
      .forEach(userId  ->
        unreadMessagesService.create(chatId, userId));

    return chatId;
  }

  public List<GroupChatRoom> getGroupChatRooms(String userId) {
    return repository.findGroupChatRoomsByChatUsersIdContaining(userId);
  }

  public GroupChatRoom getGroupChatRoom(String chatId) {
    return repository.findByGroupChatId(chatId);
  }
}
