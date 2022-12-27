package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.NotificationsSettings;
import AwesomeCalendar.Entities.TimingNotifications;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Repositories.UserRepo;
import AwesomeCalendar.Utilities.Utility;
import AwesomeCalendar.enums.NotificationHandler;
import AwesomeCalendar.enums.NotificationType;
import AwesomeCalendar.enums.NotificationsTiming;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private PopUpSender popUpSender;

    @Autowired
    private EventRepo eventRepository;

    private static final Logger logger = LogManager.getLogger(NotificationService.class.getName());

    public NotificationsSettings setNotificationsSettings(User user, NotificationsSettings notificationsSettings) {
        User userInDB = userRepository.findByEmail(user.getEmail());
        if (userInDB == null) {
            throw new IllegalArgumentException("Invalid user email");
        }
        userInDB.setNotificationsSettings(notificationsSettings);
        userRepository.save(userInDB);
        return userInDB.getNotificationsSettings();
    }

    public void sendNotifications(List<String> usersToSend, NotificationType notificationType) {
        if(usersToSend == null){
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
                    break;

                case EVENT_INVITATION:
                    message = "You have a new event invitation";
                    NotificationHandler notificationHandler2 = userInDB.getNotificationsSettings().getEventInvitation();
                    sendHelper(userInDB, notificationHandler2,message);
                    break;

                case USER_UNINVITED:
                    message = "You uninvited from event";
                    NotificationHandler notificationHandler3 = userInDB.getNotificationsSettings().getUserUninvited();
                    sendHelper(userInDB, notificationHandler3,message);
                    break;

                case USER_STATUS_CHANGED:
                    message = "Your status changed";
                    NotificationHandler notificationHandler4 = userInDB.getNotificationsSettings().getUserStatusChanged();
                    sendHelper(userInDB, notificationHandler4,message);
                    break;

                case EVENT_DATA_CHANGED:
                    message = "Event data changed";
                    NotificationHandler notificationHandler5 = userInDB.getNotificationsSettings().getEventDataChanged();
                    sendHelper(userInDB, notificationHandler5,message);
                    break;

                case UPCOMING_EVENT:
                    message = "You have upcoming event!";
                    NotificationHandler notificationHandler6 = userInDB.getNotificationsSettings().getUpcomingEvent();
                    sendHelper(userInDB, notificationHandler6,message);
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

    public TimingNotifications addTimingNotification(User user, Long eventId, NotificationsTiming timing) {
        Utility.checkArgsNotNull(user, eventId, timing);
        Optional<Event> event = eventRepository.findById(eventId);
        if (!event.isPresent()) {
            throw new IllegalArgumentException("Invalid event Id");
        }
        Optional<User> userFromDb = userRepository.findById(user.getId());
        if (!userFromDb.isPresent()) {
            throw new IllegalArgumentException("Invalid user");
        }
        TimingNotifications thisNotification = new TimingNotifications(event.get(), timing);
        userFromDb.get().getNotificationsSettings().addUpcomingEventNotifications(thisNotification);

        userRepository.save(userFromDb.get());
        return thisNotification;
    }
}
