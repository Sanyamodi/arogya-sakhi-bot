package com.arogyasakhi.repository;

import com.arogyasakhi.model.HealthConsultation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HealthConsultationRepository extends MongoRepository<HealthConsultation, String> {
    
    @Query(value = "{'chat_id': ?0}", sort = "{'consultation_time': -1}")
    List<HealthConsultation> findByChatIdOrderByConsultationTimeDesc(Long chatId);
    
    @Query("{'doctor_recommended': true}")
    List<HealthConsultation> findByDoctorRecommendedTrue();
    
    @Query("{'severity': ?0}")
    List<HealthConsultation> findBySeverity(String severity);
}
