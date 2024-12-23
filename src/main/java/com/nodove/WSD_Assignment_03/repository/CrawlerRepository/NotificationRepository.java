package com.nodove.WSD_Assignment_03.repository.CrawlerRepository;

import com.nodove.WSD_Assignment_03.domain.SaramIn.Notification;
import com.nodove.WSD_Assignment_03.domain.users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverAndIsRead(users thisUser, Pageable pageable, Boolean isRead);

}
