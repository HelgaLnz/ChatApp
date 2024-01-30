package com.example.websocket.user.repository;

import com.example.websocket.user.Status;
import com.example.websocket.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

  List<User> findAllByStatus(Status status);

}
