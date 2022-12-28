package AwesomeCalendar.Entities;

import AwesomeCalendar.enums.NotificationsTiming;

import javax.persistence.*;

@Entity
public class UpcomingEventNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private NotificationsTiming NotificationTiming;

    public UpcomingEventNotification() {
    }

    public UpcomingEventNotification(Event event, User user, NotificationsTiming notificationTiming) {
        this.event = event;
        this.user = user;
        NotificationTiming = notificationTiming;
    }

    public NotificationsTiming getNotificationTiming() {
        return NotificationTiming;
    }

    public void setNotificationTiming(NotificationsTiming notificationTiming) {
        NotificationTiming = notificationTiming;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
