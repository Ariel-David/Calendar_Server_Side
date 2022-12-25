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
        for (User user : usersToSend) {
            User userInDB = userRepository.findByEmail(user.getEmail());
            if (userInDB == null) {
                throw new IllegalArgumentException("Invalid user email");
            }
            switch (notificationType) {
                case EVENT_CANCEL:
                    NotificationHandler notificationHandler1 = user.getNotificationsSettings().getEventCancel();
                    sendHelper(user,notificationHandler1);

                    break;
                case EVENT_INVITATION:
                    NotificationHandler notificationHandler2 = user.getNotificationsSettings().getEventInvitation();
                    sendHelper(user,notificationHandler2);

                    break;
                case USER_UNINVITED:
                    NotificationHandler notificationHandler3 = user.getNotificationsSettings().getUserUninvited();
                    sendHelper(user,notificationHandler3);

                    break;
                case USER_STATUS_CHANGED:
                    NotificationHandler notificationHandler4 = user.getNotificationsSettings().getUserStatusChanged();
                    sendHelper(user,notificationHandler4);

                    break;
                case EVENT_DATA_CHANGED:
                    NotificationHandler notificationHandler5 = user.getNotificationsSettings().getEventDataChanged();
                    sendHelper(user,notificationHandler5);

                    break;
                case UPCOMING_EVENT:
                    NotificationHandler notificationHandler6 = user.getNotificationsSettings().getUpcomingEvent();
                    sendHelper(user,notificationHandler6);
                    break;
            }
        }
    }

    public void sendHelper(User user , NotificationHandler notificationHandler){
        switch (notificationHandler){
            case Email:
                emailSender.eventCanceled(user.getEmail());
                break;
            case Popup:
                //PopUp
                break;
            case Both:
                emailSender.eventCanceled(user.getEmail());
                //PopUp
        }
    }
}
