package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.NotificationsSettings;
import AwesomeCalendar.Entities.UpcomingEventNotification;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Repositories.UpcomingEventNotificationRepository;
import AwesomeCalendar.Repositories.UserRepo;
import AwesomeCalendar.Utilities.Utility;
import AwesomeCalendar.enums.NotificationHandler;
import AwesomeCalendar.enums.NotificationType;
import AwesomeCalendar.enums.NotificationsTiming;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static AwesomeCalendar.Utilities.messages.ExceptionMessage.invalidUserEmailMessage;

@Service
public class NotificationService {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private UpcomingEventNotificationRepository upcomingEventNotificationRepository;

    @Autowired
    private EventRepo eventRepository;
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private RealTimeSender realTimeSender;

    @Autowired
    private PopUpSender popUpSender;

    private static final Logger logger = LogManager.getLogger(NotificationService.class.getName());

    public NotificationsSettings setNotificationsSettings(User user, NotificationsSettings notificationsSettings) {
        User userInDB = userRepository.findByEmail(user.getEmail());
        if (userInDB == null) {
            throw new IllegalArgumentException(invalidUserEmailMessage);
        }
        userInDB.setNotificationsSettings(notificationsSettings);
        userRepository.save(userInDB);
        return userInDB.getNotificationsSettings();
    }

    public void sendNotifications(List<String> usersToSend, NotificationType notificationType) {
        if (usersToSend == null) {
            throw new IllegalArgumentException("List is null");
        }
        String message = "";
        for (String userEmail : usersToSend) {
            User userInDB = userRepository.findByEmail(userEmail);
            if (userInDB == null) {
                throw new IllegalArgumentException("Invalid user email");
            }
            switch (notificationType) {
                case EVENT_CANCEL:
                    message = "Event canceled";
                    NotificationHandler notificationHandler1 = userInDB.getNotificationsSettings().getEventCancel();
                    sendHelper(userInDB, notificationHandler1, message);
                    realTimeSender.sendUpdate(userEmail, Event.class);
                    break;

                case EVENT_INVITATION:
                    message = "You have a new event invitation";
                    NotificationHandler notificationHandler2 = userInDB.getNotificationsSettings().getEventInvitation();
                    sendHelper(userInDB, notificationHandler2, message);
                    realTimeSender.sendUpdate(userEmail, Event.class);
                    break;

                case USER_UNINVITED:
                    message = "You uninvited from event";
                    NotificationHandler notificationHandler3 = userInDB.getNotificationsSettings().getUserUninvited();
                    sendHelper(userInDB, notificationHandler3, message);
                    realTimeSender.sendUpdate(userEmail, Event.class);
                    break;

                case USER_STATUS_CHANGED:
                    message = "Your status changed";
                    NotificationHandler notificationHandler4 = userInDB.getNotificationsSettings().getUserStatusChanged();
                    sendHelper(userInDB, notificationHandler4, message);
                    realTimeSender.sendUpdate(userEmail, Event.class);
                    break;

                case EVENT_DATA_CHANGED:
                    message = "Event data changed";
                    NotificationHandler notificationHandler5 = userInDB.getNotificationsSettings().getEventDataChanged();
                    sendHelper(userInDB, notificationHandler5, message);
                    realTimeSender.sendUpdate(userEmail, Event.class);
                    break;

                case UPCOMING_EVENT:
                    message = "You have upcoming event!";
                    NotificationHandler notificationHandler6 = userInDB.getNotificationsSettings().getUpcomingEvent();
                    sendHelper(userInDB, notificationHandler6, message);
                    realTimeSender.sendUpdate(userEmail, Event.class);
                    break;

                case SHARE_CALENDAR:
                    realTimeSender.sendUpdate(userEmail, Calendar.class);
                    break;
            }
        }
    }

    public void sendHelper(User user, NotificationHandler notificationHandler, String message) {
        switch (notificationHandler) {
            case Email:
                emailSender.sendEmailNotification(user.getEmail(), message);
                break;
            case Popup:
                popUpSender.sendPopNotification(user.getEmail(), message);
                break;
            case Both:
                emailSender.sendEmailNotification(user.getEmail(), message);
                popUpSender.sendPopNotification(user.getEmail(), message);
        }
    }

    /**
     * adds an upcomingEventNotification setting for the user.
     * @param user the user that wants the notification
     * @param eventId the event he wants the notification for.
     * @param notificationsTiming the time before the event that he wants the notification.
     * @return upcomingnotification with all the details.
     * @throws IllegalArgumentException if any of the parameters are null or if the event id is invalid.
     */
    public UpcomingEventNotification addUpcomingEventNotification(User user, Long eventId, NotificationsTiming notificationsTiming) {
        Utility.checkArgsNotNull(user, eventId, notificationsTiming);
        Optional<Event> event = eventRepository.findById(eventId);
        if (!event.isPresent()) {
            throw new IllegalArgumentException("Event id incorrect");
        }
        UpcomingEventNotification upcomingEventNotification = new UpcomingEventNotification(event.get(), user, notificationsTiming);
        upcomingEventNotificationRepository.save(upcomingEventNotification);

        return upcomingEventNotification;
    }
}
