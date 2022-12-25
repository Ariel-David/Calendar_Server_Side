package AwesomeCalendar.Entities;

import javax.persistence.*;

@Entity
public class NotificationsSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    private boolean eventInvitation = false;
    private boolean userStatusChanged = false;
    private boolean eventDataChanged = false;
    private boolean eventCanceled = false;
    private boolean userUninvited = false;
    private boolean upComingEvent = false;

    public boolean isEventInvitation() {
        return eventInvitation;
    }

    public void setEventInvitation(boolean eventInvitation) {
        this.eventInvitation = eventInvitation;
    }

    public boolean isUserStatusChanged() {
        return userStatusChanged;
    }

    public void setUserStatusChanged(boolean userStatusChanged) {
        this.userStatusChanged = userStatusChanged;
    }

    public boolean isEventDataChanged() {
        return eventDataChanged;
    }

    public void setEventDataChanged(boolean eventDataChanged) {
        this.eventDataChanged = eventDataChanged;
    }

    public boolean isEventCanceled() {
        return eventCanceled;
    }

    public void setEventCanceled(boolean eventCanceled) {
        this.eventCanceled = eventCanceled;
    }

    public boolean isUserUninvited() {
        return userUninvited;
    }

    public void setUserUninvited(boolean userUninvited) {
        this.userUninvited = userUninvited;
    }

    public boolean isUpComingEvent() {
        return upComingEvent;
    }

    public void setUpComingEvent(boolean upComingEvent) {
        this.upComingEvent = upComingEvent;
    }

    public Long getId() {
        return id;
    }
}
