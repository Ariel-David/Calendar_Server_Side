package AwesomeCalendar.Entities;

import AwesomeCalendar.enums.NotificationsTiming;

import javax.persistence.*;
import java.time.Duration;

@Entity
public class TimingNotifications {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false)
    private NotificationsTiming NotificationTiming;

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

    public void setId(Long id) {

        this.id = id;
    }
}
