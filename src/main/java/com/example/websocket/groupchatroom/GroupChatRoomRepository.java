package com.example.websocket.groupchatroom;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GroupChatRoomRepository extends MongoRepository<GroupChatRoom, String> {

  Optional<GroupChatRoom> findByChatUsersIdAndGroupChatName(List<String> chatUsersId, String groupChatName);
  List<GroupChatRoom> findGroupChatRoomsByChatUsersIdContaining(String groupChatId);
  GroupChatRoom findByGroupChatId(String chatId);
}
