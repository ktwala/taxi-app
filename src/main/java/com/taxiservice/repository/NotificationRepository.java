package com.taxiservice.repository;

import com.taxiservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByAssocMemberId(Long assocMemberId);

    List<Notification> findByStatus(String status);

    List<Notification> findByNotificationType(String notificationType);

    List<Notification> findByAssocMemberIdAndStatus(Long assocMemberId, String status);

    @Query("SELECT n FROM Notification n WHERE n.assocMemberId = :memberId AND n.status = 'UNREAD'")
    List<Notification> findUnreadByMember(Long memberId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.assocMemberId = :memberId AND n.status = 'UNREAD'")
    Long countUnreadByMember(Long memberId);

    @Query("SELECT n FROM Notification n WHERE n.status = 'UNREAD' ORDER BY n.createdAt DESC")
    List<Notification> findAllUnread();
}
