package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.EventService;
import AwesomeCalendar.Services.SharingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class EventControllerTests {

    @Mock
    EventService eventService;
    @Mock
    SharingService sharingService;
    @InjectMocks
    EventController eventController;
    User user1;
    User user2;
    Event event1;
    Role role1;
    @BeforeEach
    void setup() {
        user1 = new User(0L, "test@test.test", "123456");
    }

    @Test
    void createEvent_NullUser_status400() {
        assertEquals(400, eventController.createEvent(null, event1).getStatusCodeValue());
    }

    @Test
    void createEvent_NullEventStart_status400() {
        event1 = new Event(Event.EventAccess.PUBLIC, null, ZonedDateTime.now(), null, "test", null);
        assertEquals(400, eventController.createEvent(user1, event1).getStatusCodeValue());
    }

    @Test
    void createEvent_NullEventEnd_status400() {
        event1 = new Event(Event.EventAccess.PUBLIC, ZonedDateTime.now(), null, null, "test", null);
        assertEquals(400, eventController.createEvent(user1, event1).getStatusCodeValue());
    }

    @Test
    void createEvent_NullEventTitle_status400() {
        event1 = new Event(Event.EventAccess.PUBLIC, ZonedDateTime.now(), ZonedDateTime.now(), null, null, null);
        assertEquals(400, eventController.createEvent(user1, event1).getStatusCodeValue());
    }

    @Test
    void createEvent_NullEventCreate_internalServerErrorStatus500() {
        event1 = new Event(null, ZonedDateTime.now(), ZonedDateTime.now(), null, "test", null);
        given(eventService.createEvent(event1, user1)).willReturn(null);
        assertEquals(500, eventController.createEvent(user1, event1).getStatusCodeValue());
    }

    @Test
    void createEvent_okEventCreate_status200() {
        event1 = new Event(null, ZonedDateTime.now(), ZonedDateTime.now(), null, "test", null);
        given(eventService.createEvent(event1, user1)).willReturn(event1);
        assertEquals(200, eventController.createEvent(user1, event1).getStatusCodeValue());
    }

    @Test
    void createRole_validateEmail_status400() {
        assertEquals(400, eventController.createRole(0L, "test").getStatusCodeValue());
    }
    @Test
    void createRole_okCreateRole_status200() {
        user2 = new User(1L, "role1@role1.role1", "123456");
        role1 = new Role(user2, Role.RoleType.GUEST, Role.StatusType.TENTATIVE);
        given(eventService.addGuestRole(0L, "role@role.role")).willReturn(role1);
        assertEquals(200, eventController.createRole(0L, "role@role.role").getStatusCodeValue());
    }
}
