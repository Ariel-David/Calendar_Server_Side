package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.NotificationsSettings;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private UserRepo userRepository;

    private static final Logger logger = LogManager.getLogger(NotificationService.class.getName());

    public NotificationsSettings setNotificationsSettings(User user, NotificationsSettings notificationsSettings) {
        User userInDB = userRepository.findByEmail(user.getEmail());
        if (userInDB == null) {
            throw new IllegalArgumentException("Invalid user email");
        }
        user.setNotificationsSettings(notificationsSettings);
        return user.getNotificationsSettings();
    }
}
