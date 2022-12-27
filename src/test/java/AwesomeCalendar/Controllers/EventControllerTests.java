package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.EventService;
import AwesomeCalendar.Services.NotificationService;
import AwesomeCalendar.Services.SharingService;
import AwesomeCalendar.enums.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static AwesomeCalendar.Utilities.messages.ExceptionMessage.*;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class EventControllerTests {

    @Mock
    EventService eventService;
    @Mock
    SharingService sharingService;
    @Mock
    NotificationService notificationService;
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
    void createEvent_NullEventStart_requiredFieldMessage() {
        event1 = new Event(Event.EventAccess.PUBLIC, null, ZonedDateTime.now(), null, "test", null);
        assertEquals(requiredFieldMessage, eventController.createEvent(user1, event1).getBody().getMessage());
    }

    @Test
    void createEvent_NullEventEnd_requiredFieldMessage() {
        event1 = new Event(Event.EventAccess.PUBLIC, ZonedDateTime.now(), null, null, "test", null);
        assertEquals(requiredFieldMessage, eventController.createEvent(user1, event1).getBody().getMessage());
    }

    @Test
    void createEvent_NullEventTitle_requiredFieldMessage() {
        event1 = new Event(Event.EventAccess.PUBLIC, ZonedDateTime.now(), ZonedDateTime.now(), null, null, null);
        assertEquals(requiredFieldMessage, eventController.createEvent(user1, event1).getBody().getMessage());
    }

    @Test
    void createEvent_NullEventCreate_internalServerErrorStatus500() {
        event1 = new Event(null, ZonedDateTime.now(), ZonedDateTime.now(), null, "test", null);
        given(eventService.createEvent(event1, user1)).willReturn(null);
        assertEquals(500, eventController.createEvent(user1, event1).getStatusCodeValue());
    }

    @Test
    void createEvent_okEventCreate_eventCreatedSuccessfullyMessage() {
        event1 = new Event(null, ZonedDateTime.now(), ZonedDateTime.now(), null, "test", null);
        given(eventService.createEvent(event1, user1)).willReturn(event1);
        assertEquals(eventCreatedSuccessfullyMessage, eventController.createEvent(user1, event1).getBody().getMessage());
    }

    @Test
    void createRole_validateEmail_invalidEmailMessage() {
        assertEquals(invalidEmailMessage, eventController.createRole(0L, "test").getBody().getMessage());
    }

    @Test
    void createRole_okCreateRole_status200() {
        user2 = new User(1L, "role@role.role", "123456");
        role1 = new Role(user2, Role.RoleType.GUEST, Role.StatusType.TENTATIVE);
        given(eventService.addGuestRole(0L, "role@role.role")).willReturn(role1);
        assertEquals(200, eventController.createRole(0L, "role@role.role").getStatusCodeValue());
    }

    @Test
    void updateRoleType_okUpdateRoleType_admin() {
        user2 = new User(1L, "role@role.role", "123456");
        role1 = new Role(user2, Role.RoleType.ADMIN, Role.StatusType.TENTATIVE);
        given(eventService.updateTypeUserRole(0L, 1L)).willReturn(role1);
        assertEquals(Role.RoleType.ADMIN, eventController.updateRoleType(0L, 1L).getBody().getResponse().getRoleType());
    }

    @Test
    void updateRoleStatus_badRequest_invalidStatusMessage() {
        assertEquals(invalidStatusMessage, eventController.updateRoleStatus(user1, 0L, "hi").getBody().getMessage());
    }

    @Test
    void updateRoleStatus_okUpdateRoleStatus_invalidStatusMessage() {
        role1 = new Role(user1, Role.RoleType.ADMIN, Role.StatusType.APPROVED);
        given(eventService.updateStatusUserRole(0L, user1, "APPROVED")).willReturn(role1);
        given(eventService.getEventOrganizer(0L)).willReturn(Optional.of(user1));
//        given(notificationService.sendNotifications(List.of(user1.getEmail()), NotificationType.USER_STATUS_CHANGED)).willReturn(null);
        assertEquals(roleStatusChangedSuccessfullyMessage, eventController.updateRoleStatus(user1, 0L, "APPROVED").getBody().getMessage());
    }

    @Test
    void deleteEvent_nullEventId_invalidEventIdMessage() {
        assertEquals(invalidEventIdMessage, eventController.deleteEvent(null).getBody().getMessage());
    }

    @Test
    void deleteEvent_nullEvent_somethingWrongMessage() {
        given(eventService.deleteEvent(0L)).willReturn(null);
        assertEquals(somethingWrongMessage, eventController.deleteEvent(0L).getBody().getMessage());
    }

    @Test
    void deleteEvent_okDeleted_deleteEventSuccessfullyMessage() {
        event1 = new Event(0L, null, ZonedDateTime.now(), ZonedDateTime.now(), null, "test", null);
        given(eventService.deleteEvent(0L)).willReturn(event1);
        assertEquals(deleteEventSuccessfullyMessage, eventController.deleteEvent(0L).getBody().getMessage());
    }

    @Test
    void getEvent_isNotPresent_somethingWrongMessage() {
        given(eventService.getEvent(0L)).willReturn(Optional.ofNullable(null));
        assertEquals(somethingWrongMessage, eventController.getEvent(0L).getBody().getMessage());
    }

    @Test
    void getEvent_okGetEvent_getEventSuccessfullyMessage() {
        event1 = new Event(null, ZonedDateTime.now(), ZonedDateTime.now(), null, "test", null);
        given(eventService.getEvent(0L)).willReturn(Optional.ofNullable(event1));
        assertEquals(getEventSuccessfullyMessage, eventController.getEvent(0L).getBody().getMessage());
    }

    @Test
    void getEventsBetweenDates_nullEventList_somethingWrongMessage() {
        ZonedDateTime z = ZonedDateTime.now();
        given(sharingService.isShared(user1, null)).willReturn(List.of());
        given(eventService.getEventsBetweenDates(user1, z, z, List.of())).willReturn(null);
        assertEquals(somethingWrongMessage, eventController.getEventsBetweenDates(user1, z, z, null).getBody().getMessage());
    }

    @Test
    void getEventsBetweenDates_okGetEventsBetweenDates_getEventsBetweenDatesSuccessfullyMessage() {
        ZonedDateTime z = ZonedDateTime.now();
        event1 = new Event(null, ZonedDateTime.now(), ZonedDateTime.now(), null, "test", null);
        List<Event> eventList = new ArrayList<>();
        eventList.add(event1);
        given(sharingService.isShared(user1, null)).willReturn(List.of());
        given(eventService.getEventsBetweenDates(user1, z, z, List.of())).willReturn(eventList);
        assertEquals(getEventsBetweenDatesSuccessfullyMessage, eventController.getEventsBetweenDates(user1, z, z, null).getBody().getMessage());
    }

    @Test
    void removeUser_nullEventId_somethingWrongMessage() {
        assertEquals(somethingWrongMessage, eventController.removeUser(null, "a@a.a").getBody().getMessage());
    }

    @Test
    void removeUser_nullUserEmail_somethingWrongMessage() {
        assertEquals(somethingWrongMessage, eventController.removeUser(0L, null).getBody().getMessage());
    }

    @Test
    void removeUser_okDeleteRole_getEventsBetweenDatesSuccessfullyMessage() {
        role1 = new Role(user1, Role.RoleType.ADMIN, Role.StatusType.APPROVED);
        given(eventService.deleteRole(0L, "a@a.a")).willReturn(role1);
        assertEquals(getEventsBetweenDatesSuccessfullyMessage, eventController.removeUser(0L, "a@a.a").getBody().getMessage());
    }

    @Test
    void removeUser_nullDeleteRole_somethingWrongMessage() {
        given(eventService.deleteRole(0L, "a@a.a")).willReturn(null);
        assertEquals(somethingWrongMessage, eventController.removeUser(0L, "a@a.a").getBody().getMessage());
    }

    @Test
    void updateEvent_permissionAdmin_FieldsAdminCantUpdateMessage() {
        event1 = new Event(null, ZonedDateTime.now(), ZonedDateTime.now(), null, "test", null);
        assertEquals(FieldsAdminCantUpdateMessage, eventController.updateEvent(Role.RoleType.ADMIN, 0L, event1).getBody().getMessage());
    }

    @Test
    void updateEvent_okUpdateEvent_FieldsAdminCantUpdateMessage() {
        event1 = new Event(0L, null, null, null, "haifa", null, null);
        given(eventService.updateEvent(0L, event1)).willReturn(event1);
        assertEquals(updateEventSuccessfullyMessage, eventController.updateEvent(Role.RoleType.ADMIN, 0L, event1).getBody().getMessage());
    }

    @Test
    void getRolesOfEvent_nullEventId_400() {
        assertEquals(400, eventController.getRolesOfEvent(null).getStatusCodeValue());
    }

    @Test
    void getRolesOfEvent_okGetRolesOfEvent_200() {
        List<Role> listRoles = new ArrayList<>();
        given(eventService.getRolesForEvent(0L)).willReturn(listRoles);
        assertEquals(200, eventController.getRolesOfEvent(0L).getStatusCodeValue());
    }
}
