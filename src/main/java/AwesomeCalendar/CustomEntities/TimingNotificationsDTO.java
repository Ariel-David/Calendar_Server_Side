package AwesomeCalendar.CustomEntities;

import AwesomeCalendar.Entities.TimingNotifications;
import AwesomeCalendar.enums.NotificationsTiming;

public class TimingNotificationsDTO {
    private Long id;

    private Long eventId;

    private NotificationsTiming notificationsTiming;

    public static TimingNotificationsDTO fromTimingNotification(TimingNotifications timingNotifications) {
        TimingNotificationsDTO dto = new TimingNotificationsDTO();
        dto.id = timingNotifications.getId();
        dto.eventId = timingNotifications.getEvent().getId();
        dto.notificationsTiming = timingNotifications.getNotificationTiming();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public NotificationsTiming getNotificationsTiming() {
        return notificationsTiming;
    }

    public void setNotificationsTiming(NotificationsTiming notificationsTiming) {
        this.notificationsTiming = notificationsTiming;
    }
}
