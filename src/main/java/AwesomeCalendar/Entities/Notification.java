package AwesomeCalendar.Entities;

import AwesomeCalendar.enums.NotificationHandler;
import AwesomeCalendar.enums.NotificationType;

import javax.persistence.*;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false)
    private NotificationHandler notificationHandler;

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationHandler getNotificationHandler() {
        return notificationHandler;
    }

    public void setNotificationHandler(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    public Long getId() {
        return id;
    }
}
