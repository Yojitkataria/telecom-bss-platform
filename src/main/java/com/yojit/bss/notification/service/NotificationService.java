package com.yojit.bss.notification.service;

import com.yojit.bss.customer.entity.Customer;
import com.yojit.bss.notification.entity.Notification;
import com.yojit.bss.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void sendNotification(Customer customer, String channel, String subject, String message) {
        Notification notification = Notification.builder()
                .customer(customer)
                .channel(channel.toUpperCase())
                .subject(subject)
                .message(message)
                .status("SENT")
                .build();

        notificationRepository.save(notification);
    }
}