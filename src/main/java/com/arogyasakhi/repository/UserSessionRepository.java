package com.arogyasakhi.repository;

import com.arogyasakhi.model.UserSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends MongoRepository<UserSession, String> {
    
    @Query("{'chat_id': ?0}")
    Optional<UserSession> findByChatId(Long chatId);
    
    @Query(value = "{'chat_id': ?0}", delete = true)
    void deleteByChatId(Long chatId);
}
