package AwesomeCalendar.Entities;
import AwesomeCalendar.enums.NotificationHandler;


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
    private NotificationHandler userStatusChanged = NotificationHandler.None;

    @Column(nullable = false)
    private NotificationHandler eventDataChanged = NotificationHandler.None;

    @Column(nullable = false)
    private NotificationHandler eventCancel = NotificationHandler.None;

    @Column(nullable = false)
    private NotificationHandler userUninvited = NotificationHandler.None;

    @Column(nullable = false)
    private NotificationHandler upcomingEvent = NotificationHandler.None;

    public NotificationsSettings() {
    }

    public NotificationsSettings(NotificationHandler eventInvitation, NotificationHandler userStatusChanged, NotificationHandler eventDataChanged, NotificationHandler eventCancel, NotificationHandler userUninvited, NotificationHandler upcomingEvent) {
        this.eventInvitation = eventInvitation;
        this.userStatusChanged = userStatusChanged;
        this.eventDataChanged = eventDataChanged;
        this.eventCancel = eventCancel;
        this.userUninvited = userUninvited;
        this.upcomingEvent = upcomingEvent;
    }

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
        return userStatusChanged;
    }

    public void setUserStatusChanged(NotificationHandler userStatusChanged) {
        this.userStatusChanged = userStatusChanged;
    }

    public NotificationHandler getEventDataChanged() {
        return eventDataChanged;
    }

    public void setEventDataChanged(NotificationHandler eventDataChanged) {
        this.eventDataChanged = eventDataChanged;
    }

    public NotificationHandler getEventCancel() {
        return eventCancel;
    }

    public void setEventCancel(NotificationHandler eventCancel) {
        this.eventCancel = eventCancel;
    }

    public NotificationHandler getUserUninvited() {
        return userUninvited;
    }

    public void setUserUninvited(NotificationHandler userUninvited) {
        this.userUninvited = userUninvited;
    }

    public NotificationHandler getUpcomingEvent() {
        return upcomingEvent;
    }

    public void setUpcomingEvent(NotificationHandler upcomingEvent) {
        this.upcomingEvent = upcomingEvent;
    }
}
