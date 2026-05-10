package com.UMarket.backend.repository;

import com.UMarket.backend.model.Message;
import com.UMarket.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRecipientOrderBySentAtDesc(User recipient);

    List<Message> findBySenderOrderBySentAtDesc(User sender);

    long countByRecipientAndReadFalse(User recipient);
}
