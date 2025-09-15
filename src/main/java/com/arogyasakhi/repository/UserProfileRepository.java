package com.arogyasakhi.repository;

import com.arogyasakhi.model.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    
    @Query("{'chat_id': ?0}")
    Optional<UserProfile> findByChatId(Long chatId);
    
    @Query(value = "{'chat_id': ?0}", delete = true)
    void deleteByChatId(Long chatId);
    
    @Query("{'status': ?0}")
    java.util.List<UserProfile> findByStatus(UserProfile.ProfileStatus status);
}
