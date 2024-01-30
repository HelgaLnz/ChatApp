package com.example.websocket.groupchatroom;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface GroupChatMessageRepository extends MongoRepository<GroupChatMessage, String> {

  List<GroupChatMessage> findByChatId(String chatId);
}
