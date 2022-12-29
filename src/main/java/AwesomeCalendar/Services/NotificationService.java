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
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import java.time.ZonedDateTime;
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

    public void sendNotifications(List<String> usersToSend, NotificationType notificationType, Event event) {
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
//                    message = "Event canceled";
                    message = String.format("Subject: Event canceled\n" +
                            "Title: " + event.getTitle() + "\n" +
                            "Start: " + event.getStart() + "\n" +
                            "End: " + event.getEnd() + "\n" +
                            "location: " + event.getLocation() + "\n" +
                            "description: " + event.getDescription() + "\n" +
                            "eventAccess: " + event.getEventAccess() + "\n");
                    NotificationHandler notificationHandler1 = userInDB.getNotificationsSettings().getEventCancel();
                    sendHelper(userInDB, notificationHandler1, message);
                    realTimeSender.sendUpdate(userEmail, Event.class);
                    break;

                case EVENT_INVITATION:
//                    message = "You have a new event invitation";
                    message = String.format("Subject: You have a new event invitation\n" +
                            "Title: " + event.getTitle() + "\n" +
                            "Start: " + event.getStart() + "\n" +
                            "End: " + event.getEnd() + "\n" +
                            "location: " + event.getLocation() + "\n" +
                            "description: " + event.getDescription() + "\n" +
                            "eventAccess: " + event.getEventAccess() + "\n");
                    NotificationHandler notificationHandler2 = userInDB.getNotificationsSettings().getEventInvitation();
                    sendHelper(userInDB, notificationHandler2, message);
                    realTimeSender.sendUpdate(userEmail, Event.class);
                    break;

                case USER_UNINVITED:
//                    message = "Your invitation to the event has been removed";
                    message = String.format("Subject: Your invitation to the event has been removed\n" +
                            "Title: " + event.getTitle() + "\n" +
                            "Start: " + event.getStart() + "\n" +
                            "End: " + event.getEnd() + "\n" +
                            "location: " + event.getLocation() + "\n" +
                            "description: " + event.getDescription() + "\n" +
                            "eventAccess: " + event.getEventAccess() + "\n");
                    NotificationHandler notificationHandler3 = userInDB.getNotificationsSettings().getUserUninvited();
                    sendHelper(userInDB, notificationHandler3, message);
                    realTimeSender.sendUpdate(userEmail, Event.class);
                    break;

                case USER_STATUS_CHANGED:
//                    message = "Your status has changed";
                    message = String.format("Subject: Your status has changed\n" +
                            "Title: " + event.getTitle() + "\n" +
                            "Start: " + event.getStart() + "\n" +
                            "End: " + event.getEnd() + "\n" +
                            "location: " + event.getLocation() + "\n" +
                            "description: " + event.getDescription() + "\n" +
                            "eventAccess: " + event.getEventAccess() + "\n");
                    NotificationHandler notificationHandler4 = userInDB.getNotificationsSettings().getUserStatusChanged();
                    sendHelper(userInDB, notificationHandler4, message);
                    realTimeSender.sendUpdate(userEmail, Event.class);
                    break;

                case EVENT_DATA_CHANGED:
//                    message = "The event data has been updated";
                    message = String.format("Subject: The event data has been updated\n" +
                            "Title: " + event.getTitle() + "\n" +
                            "Start: " + event.getStart() + "\n" +
                            "End: " + event.getEnd() + "\n" +
                            "location: " + event.getLocation() + "\n" +
                            "description: " + event.getDescription() + "\n" +
                            "eventAccess: " + event.getEventAccess() + "\n");
                    NotificationHandler notificationHandler5 = userInDB.getNotificationsSettings().getEventDataChanged();
                    sendHelper(userInDB, notificationHandler5, message);
                    realTimeSender.sendUpdate(userEmail, Event.class);
                    break;

                case UPCOMING_EVENT:
//                    message = "A reminder of an upcoming event";
                    message = String.format("Subject: A reminder for an event that starts in "+ event.getStart().minusMinutes(ZonedDateTime.now().getMinute()) +" \n" +
                            "Title: " + event.getTitle() + "\n" +
                            "Start: " + event.getStart() + "\n" +
                            "End: " + event.getEnd() + "\n" +
                            "location: " + event.getLocation() + "\n" +
                            "description: " + event.getDescription() + "\n" +
                            "eventAccess: " + event.getEventAccess() + "\n");
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

    public NotificationsSettings getNotificationsSettings(User user) {
        return user.getNotificationsSettings();
    }

    /**
     * adds an upcomingEventNotification setting for the user.
     *
     * @param user                the user that wants the notification
     * @param eventId             the event he wants the notification for.
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

    /**
     * runs every minute
     * <p>
     * checks if any upcoming events have notifications that needs to be sent and sends them.
     */
    @Scheduled(fixedRate = 1000 * 60)
    private void upcomingNotificationRunner() {
        logger.debug("starting check for upcoming event notifications");
        List<UpcomingEventNotification> all = upcomingEventNotificationRepository.findAll();
        for (int i = 0; i < all.size(); ) {
            UpcomingEventNotification currentNotification = all.get(i);
            if (shouldNotify(currentNotification)) {
                sendNotifications(List.of(currentNotification.getUser().getEmail()), NotificationType.UPCOMING_EVENT, currentNotification.getEvent());
                upcomingEventNotificationRepository.delete(currentNotification);
                all.remove(i);
            } else {
                i++;
            }
        }
    }

    private boolean shouldNotify(UpcomingEventNotification notification) {
        if (notification == null) {
            return false;
        }
        ZonedDateTime timeToNotify = notification.getEvent().getStart();
        switch (notification.getNotificationTiming()) {
            case ONE_DAY:
                timeToNotify = timeToNotify.minusDays(1);
                break;
            case THREE_HOURS:
                timeToNotify = timeToNotify.minusHours(3);
                break;
            case ONE_HOUR:
                timeToNotify = timeToNotify.minusHours(1);
                break;
            case HALF_HOUR:
                timeToNotify = timeToNotify.minusMinutes(30);
                break;
            case TEN_MIN:
                timeToNotify = timeToNotify.minusMinutes(10);
                break;
        }
        return ZonedDateTime.now().plusHours(2).isAfter(timeToNotify);
    }
}
