package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.SharingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static AwesomeCalendar.Utilities.messages.ExceptionMessage.invalidEmailMessage;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.getSharedCalendarsSuccessfullyMessage;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.shareCalendarSuccessfullyMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SharingControllerTests {
    @Mock
    SharingService sharingService;
    @InjectMocks
    SharingController sharingController;
    User user1;
    User user2;

    @BeforeEach
    void setup() {
        user1 = new User(0L, "test@test.test", "123456");
    }

    @Test
    void shareCalendar_NullUser_status400() {
        assertEquals(400, sharingController.shareCalendar(null, "a@a.a").getStatusCodeValue());
    }

    @Test
    void shareCalendar_notValidateEmail_invalidEmailMessage() {
        assertEquals(invalidEmailMessage, sharingController.shareCalendar(user1, "a@a").getBody().getMessage());
    }

    @Test
    void shareCalendar_okSharingCalendar_shareCalendarSuccessfullyMessage() {
        user2 = new User(1L, "test@test.com", "123456");
        given(sharingService.shareCalendar(user1, "test@test.com")).willReturn(user2);
        assertEquals(shareCalendarSuccessfullyMessage, sharingController.shareCalendar(user1, "test@test.com").getBody().getMessage());
    }

    @Test
    void shareCalendar_nullSharedUser_status400() {
        given(sharingService.shareCalendar(user1, "test@test.com")).willThrow(IllegalArgumentException.class);
        assertEquals(400, sharingController.shareCalendar(user1, "test@test.com").getStatusCodeValue());
    }

    @Test
    void sharedWithMeCalendars_nullUser_status400() {
        assertEquals(400, sharingController.sharedWithMeCalendars(null).getStatusCodeValue());
    }

    @Test
    void sharedWithMeCalendars_ok_getSharedCalendarsSuccessfullyMessage() {
        assertEquals(getSharedCalendarsSuccessfullyMessage, sharingController.sharedWithMeCalendars(user1).getBody().getMessage());
    }
}
