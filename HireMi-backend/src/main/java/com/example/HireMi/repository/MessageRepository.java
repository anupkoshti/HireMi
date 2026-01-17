package com.example.HireMi.repository;

import com.example.HireMi.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    
    List<Message> findBySenderId(String senderId);
    Page<Message> findBySenderId(String senderId, Pageable pageable);
    List<Message> findByRecipientId(String recipientId);
    Page<Message> findByRecipientId(String recipientId, Pageable pageable);
    List<Message> findByThreadId(String threadId);
    List<Message> findByThreadIdOrderBySentAtAsc(String threadId);
    
    Page<Message> findByRecipientIdOrderBySentAtDesc(String recipientId, Pageable pageable);
    Page<Message> findBySenderIdOrderBySentAtDesc(String senderId, Pageable pageable);
    
    @Query("{ $or: [ { 'senderId': ?0 }, { 'recipientId': ?0 } ] }")
    Page<Message> findMessagesByUserId(String userId, Pageable pageable);
    
    @Query("{ 'recipientId': ?0, 'isRead': false }")
    List<Message> findUnreadMessagesByRecipient(String recipientId);
    
    List<Message> findByRecipientIdAndIsReadFalse(String recipientId);
    List<Message> findByThreadIdAndRecipientIdAndIsReadFalse(String threadId, String recipientId);
    
    @Query(value = "{ 'recipientId': ?0, 'isRead': false }", count = true)
    Long countUnreadMessagesByRecipient(String recipientId);
    
    @Query(value = "{ 'recipientId': ?0, 'isRead': false }", count = true)
    Long countByRecipientIdAndIsReadFalse(String recipientId);
    
    @Query(value = "{ 'messageType': ?0 }", count = true)
    Long countByMessageType(String messageType);
    
    @Query(value = "{ 'senderId': ?0, 'messageType': ?1 }", count = true)
    Long countBySenderIdAndMessageType(String senderId, String messageType);
    
    @Query(value = "{ 'senderId': ?0, 'sentAt': { $gte: ?1, $lte: ?2 } }", count = true)
    Long countBySenderIdAndSentAtBetween(String senderId, LocalDateTime startDate, LocalDateTime endDate);
}