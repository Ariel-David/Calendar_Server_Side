package AwesomeCalendar.Entities;

import AwesomeCalendar.enums.NotificationHandler;
import AwesomeCalendar.enums.NotificationType;

import javax.persistence.*;

@Entity
public class NotificationsSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private NotificationHandler eventInvitation = NotificationHandler.None;

    @Column(nullable = false)
    private NotificationHandler UserStatusChanged = NotificationHandler.None;

    @Column(nullable = false)
    private NotificationHandler EventDataChanged = NotificationHandler.None;

    @Column(nullable = false)
    private NotificationHandler EventCancel = NotificationHandler.None;

    @Column(nullable = false)
    private NotificationHandler UserUninvited = NotificationHandler.None;

    @Column(nullable = false)
    private NotificationHandler UpcomingEvent = NotificationHandler.None;

    public Long getId() {
        return id;
    }

    public NotificationHandler getEventInvitation() {
        return eventInvitation;
    }

    public void setEventInvitation(NotificationHandler eventInvitation) {
        this.eventInvitation = eventInvitation;
    }

    public NotificationHandler getUserStatusChanged() {
        return UserStatusChanged;
    }

    public void setUserStatusChanged(NotificationHandler userStatusChanged) {
        UserStatusChanged = userStatusChanged;
    }

    public NotificationHandler getEventDataChanged() {
        return EventDataChanged;
    }

    public void setEventDataChanged(NotificationHandler eventDataChanged) {
        EventDataChanged = eventDataChanged;
    }

    public NotificationHandler getEventCancel() {
        return EventCancel;
    }

    public void setEventCancel(NotificationHandler eventCancel) {
        EventCancel = eventCancel;
    }

    public NotificationHandler getUserUninvited() {
        return UserUninvited;
    }

    public void setUserUninvited(NotificationHandler userUninvited) {
        UserUninvited = userUninvited;
    }

    public NotificationHandler getUpcomingEvent() {
        return UpcomingEvent;
    }

    public void setUpcomingEvent(NotificationHandler upcomingEvent) {
        UpcomingEvent = upcomingEvent;
    }
}
