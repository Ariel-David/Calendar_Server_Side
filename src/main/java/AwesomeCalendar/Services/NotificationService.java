package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.NotificationsSettings;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import AwesomeCalendar.enums.NotificationHandler;
import AwesomeCalendar.enums.NotificationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private EmailSender emailSender;

    private static final Logger logger = LogManager.getLogger(NotificationService.class.getName());

    public NotificationsSettings setNotificationsSettings(User user, NotificationsSettings notificationsSettings) {
        User userInDB = userRepository.findByEmail(user.getEmail());
        if (userInDB == null) {
            throw new IllegalArgumentException("Invalid user email");
        }
        user.setNotificationsSettings(notificationsSettings);
        return user.getNotificationsSettings();
    }

    public void sendNotifications(List<User> usersToSend, NotificationType notificationType) {
        String message = "";
        for (User user : usersToSend) {
            User userInDB = userRepository.findByEmail(user.getEmail());
            if (userInDB == null) {
                throw new IllegalArgumentException("Invalid user email");
            }
            switch (notificationType) {
                case EVENT_CANCEL:
                    message = "Event canceled";
                    NotificationHandler notificationHandler1 = user.getNotificationsSettings().getEventCancel();
                    sendHelper(user, notificationHandler1, message);
                    break;

                case EVENT_INVITATION:
                    message = "You have a new event invitation";
                    NotificationHandler notificationHandler2 = user.getNotificationsSettings().getEventInvitation();
                    sendHelper(user, notificationHandler2,message);
                    break;

                case USER_UNINVITED:
                    message = "You uninvited from event";
                    NotificationHandler notificationHandler3 = user.getNotificationsSettings().getUserUninvited();
                    sendHelper(user, notificationHandler3,message);
                    break;

                case USER_STATUS_CHANGED:
                    message = "Your status changed";
                    NotificationHandler notificationHandler4 = user.getNotificationsSettings().getUserStatusChanged();
                    sendHelper(user, notificationHandler4,message);
                    break;

                case EVENT_DATA_CHANGED:
                    message = "Event data changed";
                    NotificationHandler notificationHandler5 = user.getNotificationsSettings().getEventDataChanged();
                    sendHelper(user, notificationHandler5,message);
                    break;

                case UPCOMING_EVENT:
                    message = "You have upcoming event!";
                    NotificationHandler notificationHandler6 = user.getNotificationsSettings().getUpcomingEvent();
                    sendHelper(user, notificationHandler6,message);
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
                //PopUp
                break;
            case Both:
                emailSender.sendEmailNotification(user.getEmail(), message);
                //PopUp
        }
    }
}
