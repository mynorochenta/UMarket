package com.UMarket.backend.service;

import com.UMarket.backend.model.Listing;
import com.UMarket.backend.model.Message;
import com.UMarket.backend.model.User;
import com.UMarket.backend.repository.ListingRepository;
import com.UMarket.backend.repository.MessageRepository;
import com.UMarket.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    public Message send(Message message, Long senderId, Long recipientId, Long listingId) {
        if (senderId.equals(recipientId)) {
            throw new RuntimeException("You cannot send a message to yourself");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        message.setSender(sender);
        message.setRecipient(recipient);
        message.setListing(listing);
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        return messageRepository.save(message);
    }

    public List<Message> getInbox(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return messageRepository.findByRecipientOrderBySentAtDesc(user);
    }

    public long countUnread(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return messageRepository.countByRecipientAndReadFalse(user);
    }

    public Message markAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getRecipient().getId().equals(userId)) {
            throw new RuntimeException("You do not have permission to mark this message as read");
        }

        message.setRead(true);
        return messageRepository.save(message);
    }
}
