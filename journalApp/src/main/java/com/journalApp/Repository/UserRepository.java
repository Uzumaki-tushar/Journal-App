package com.journalApp.Repository;

import com.journalApp.Entities.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByUsername(String username);

    void deleteUserByUsername(String username);
}
