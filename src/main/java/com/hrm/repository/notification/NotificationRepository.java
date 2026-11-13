package com.hrm.repository.notification;

import com.hrm.Entity.notification.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreateTimeDesc(String userId, Pageable pageable);

    long countByUserIdAndStatus(String userId, int status);

    Optional<Notification> findByIdAndUserId(int id, String userId);

    List<Notification> findByUserIdAndStatus(String userId, int status);
}