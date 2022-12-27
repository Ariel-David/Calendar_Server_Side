package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.NotificationsSettings;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import AwesomeCalendar.enums.NotificationHandler;
import AwesomeCalendar.enums.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    UserRepo userRepository;

    @InjectMocks
    NotificationService notificationService;

    @Mock
    EmailSender emailSender;

    @Mock
    PopUpSender popUpSender;

    User user;

    @BeforeEach
    void setup() {
        user = new User(1L,"test.test@gmail.com", "12345");
    }

    @Test
    void set_nullNotificationsSettings() {
        assertThrows(IllegalArgumentException.class,
                () -> notificationService.setNotificationsSettings(user,user.getNotificationsSettings()));
    }

    @Test
    void set_NotificationsSettings() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);
        given(userRepository.save(user)).willReturn(null);
        NotificationsSettings notificationsSettings = new NotificationsSettings(NotificationHandler.Both,NotificationHandler.Email,NotificationHandler.Email,NotificationHandler.Popup,NotificationHandler.Both,NotificationHandler.Email);
        notificationService.setNotificationsSettings(user,notificationsSettings);
        assertEquals(user.getNotificationsSettings(),notificationsSettings);
    }

    @Test
    void sendNotifications_NullUserList() {
        assertThrows(IllegalArgumentException.class,
                () -> notificationService.sendNotifications(null,NotificationType.EVENT_INVITATION));
    }

    @Test
    void sendHelper() {
        doNothing().when(emailSender).sendEmailNotification(user.getEmail(),"notify");
        notificationService.sendHelper(user,NotificationHandler.Email,"notify");
        verify(emailSender,times(1)).sendEmailNotification(user.getEmail(),"notify");
    }
}