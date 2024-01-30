package com.example.websocket.unreadmessages;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UnreadMessagesRepository extends MongoRepository<UnreadMessages, String> {

  List<UnreadMessages> getByChatIdAndRecipientId(String chatId, String recipientId);
}
