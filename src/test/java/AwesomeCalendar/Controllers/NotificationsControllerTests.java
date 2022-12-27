package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.NotificationsSettings;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.NotificationService;
import AwesomeCalendar.enums.NotificationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static AwesomeCalendar.Utilities.messages.ExceptionMessage.somethingWrongMessage;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.setNotificationsSuccessfullyMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class NotificationsControllerTests {
    @Mock
    NotificationService notificationService;
    @InjectMocks
    NotificationsController notificationsController;
    User user1;
    NotificationsSettings notificationsSettings1;
    @BeforeEach
    void setup() {
        user1 = new User(0L, "test@test.test", "123456");
        notificationsSettings1  = new NotificationsSettings(NotificationHandler.Email,NotificationHandler.Email,NotificationHandler.Email,NotificationHandler.Email,NotificationHandler.Email,NotificationHandler.Email);
    }

    @Test
    void setNotificationsSettings_nullUser_status400() {
        assertEquals(400, notificationsController.setNotificationsSettings(null, notificationsSettings1).getStatusCodeValue());
    }
    @Test
    void setNotificationsSettings_nullSetNotifications_somethingWrongMessage() {
        given(notificationService.setNotificationsSettings(user1, notificationsSettings1)).willReturn(null);
        assertEquals(somethingWrongMessage, notificationsController.setNotificationsSettings(user1, notificationsSettings1).getBody().getMessage());
    }
    @Test
    void setNotificationsSettings_ok_setNotificationsSuccessfullyMessage() {
        given(notificationService.setNotificationsSettings(user1, notificationsSettings1)).willReturn(notificationsSettings1);
        assertEquals(setNotificationsSuccessfullyMessage, notificationsController.setNotificationsSettings(user1, notificationsSettings1).getBody().getMessage());
    }
    @Test
    void setNotificationsSettings_illegalArgumentException_throwIllegalArgumentException() {
        given(notificationService.setNotificationsSettings(user1, notificationsSettings1)).willThrow(IllegalArgumentException.class);
        assertEquals(400, notificationsController.setNotificationsSettings(user1, notificationsSettings1).getStatusCodeValue());
    }
}
