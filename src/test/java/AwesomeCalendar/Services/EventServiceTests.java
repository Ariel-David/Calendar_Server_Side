package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

    @Mock
    EventRepo eventRepository;

    @Mock
    UserRepo userRepository;

    @InjectMocks
    EventService eventService;

    User user;

    User userTwo;

    Event event;

    @BeforeEach
    void setup() {
        user = new User(1L, "test@test.com", "12345");
        userTwo = new User(2L, "test.test@gmail.com", "12345");
        event = new Event(10L, Event.EventAccess.PUBLIC, ZonedDateTime.now(), ZonedDateTime.now().plusHours(3),
                "location", "title", "description");
    }

    @Test
    void createEvent_NullEvent_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(null, user));
    }

    @Test
    void createEvent_NullUser_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(event, null));
    }

    @Test
    void createEvent_GoodRequest_returnsEventWithOrganizer() {
        given(eventRepository.save(event)).willReturn(event);

        Event returnedEvent = eventService.createEvent(event, user);

        assertEquals(event, returnedEvent);
        assertTrue(event.getUserRoles().stream()
                .anyMatch(role -> role.getUser() == user && role.getRoleType() == Role.RoleType.ORGANIZER));
    }

    @Test
    void updateEvent_NullEventId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(null, event));
    }

    @Test
    void updateEvent_NullEvent_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(10L, null));
    }

    @Test
    void updateEvent_ChangeAccess_returnsEventWithChangedAccess() {
        event.setEventAccess(Event.EventAccess.PUBLIC);
        given(eventRepository.findById(10L)).willReturn(Optional.ofNullable(event));
        given(eventRepository.save(event)).willReturn(event);

        Event returnedEvent = eventService.updateEvent(10L,
                new Event(null, Event.EventAccess.PRIVATE, null, null, null, null, null));

        assertEquals(event.getId(), returnedEvent.getId());
        assertEquals(Event.EventAccess.PRIVATE, returnedEvent.getEventAccess());
    }

    @Test
    void updateEvent_ChangeStart_returnsEventWithChangedStart() {
        event.setStart(ZonedDateTime.now());
        given(eventRepository.findById(10L)).willReturn(Optional.ofNullable(event));
        given(eventRepository.save(event)).willReturn(event);
        ZonedDateTime newTime = ZonedDateTime.now().minusDays(7);

        Event returnedEvent = eventService.updateEvent(10L,
                new Event(null, null, newTime, null, null, null, null));

        assertEquals(event.getId(), returnedEvent.getId());
        assertEquals(newTime, returnedEvent.getStart());
    }

    @Test
    void updateEvent_ChangeEnd_returnsEventWithChangedEnd() {
        event.setEnd(ZonedDateTime.now().plusDays(7));
        given(eventRepository.findById(10L)).willReturn(Optional.ofNullable(event));
        given(eventRepository.save(event)).willReturn(event);
        ZonedDateTime newTime = ZonedDateTime.now().plusDays(1);

        Event returnedEvent = eventService.updateEvent(10L,
                new Event(null, null, null, newTime, null, null, null));

        assertEquals(event.getId(), returnedEvent.getId());
        assertEquals(newTime, returnedEvent.getEnd());
    }

    @Test
    void updateEvent_ChangeLocation_returnsEventWithChangedLocation() {
        event.setLocation("starting here");
        given(eventRepository.findById(10L)).willReturn(Optional.ofNullable(event));
        given(eventRepository.save(event)).willReturn(event);

        Event returnedEvent = eventService.updateEvent(10L,
                new Event(null, null, null, null, "ending here", null, null));

        assertEquals(event.getId(), returnedEvent.getId());
        assertEquals("ending here", returnedEvent.getLocation());
    }

    @Test
    void updateEvent_ChangeTitle_returnsEventWithChangedTitle() {
        event.setTitle("starting here");
        given(eventRepository.findById(10L)).willReturn(Optional.ofNullable(event));
        given(eventRepository.save(event)).willReturn(event);

        Event returnedEvent = eventService.updateEvent(10L,
                new Event(null, null, null, null, null, "ending here", null));

        assertEquals(event.getId(), returnedEvent.getId());
        assertEquals("ending here", returnedEvent.getTitle());
    }

    @Test
    void updateEvent_ChangeDescription_returnsEventWithChangedDescription() {
        event.setTitle("starting here");
        given(eventRepository.findById(10L)).willReturn(Optional.ofNullable(event));
        given(eventRepository.save(event)).willReturn(event);

        Event returnedEvent = eventService.updateEvent(10L,
                new Event(null, null, null, null, null, null, "ending here"));

        assertEquals(event.getId(), returnedEvent.getId());
        assertEquals("ending here", returnedEvent.getDescription());
    }

    @Test
    void deleteEvent_NullEventId_throwsIllegalArgumentException() {
        given(eventRepository.findById(null)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> eventService.deleteEvent(null));
    }

    @Test
    void deleteEvent_IncorrectEventId_throwsIllegalArgumentException() {
        given(eventRepository.findById(11L)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> eventService.deleteEvent(11L));
    }

    @Test
    void deleteEvent_GoodRequest_returnsDeletedEvent() {
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));

        Event returnedEvent = eventService.deleteEvent(10L);
        assertEquals(event, returnedEvent);
    }

    @Test
    void getEvent_NullEventId_returnsEmptyOptional() {
        given(eventRepository.findById(null)).willReturn(Optional.empty());

        Optional<Event> returnedEvent = eventService.getEvent(null);

        assertFalse(returnedEvent.isPresent());
    }

    @Test
    void getEvent_IncorrectEventId_returnsEmptyOptional() {
        given(eventRepository.findById(11L)).willReturn(Optional.empty());

        Optional<Event> returnedEvent = eventService.getEvent(11L);

        assertFalse(returnedEvent.isPresent());
    }

    @Test
    void getEvent_GoodRequest_returnsEvent() {
        given(eventRepository.findById(10L)).willReturn(Optional.ofNullable(event));

        Optional<Event> returnedEvent = eventService.getEvent(10L);

        assertTrue(returnedEvent.isPresent());
    }

    @Test
    void getEventsBetweenDates_NullUser_throwsIllegalArgumentController() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.getEventsBetweenDates(null, ZonedDateTime.now(),
                        ZonedDateTime.now().plusDays(7), List.of(user)));
    }

    @Test
    void getEventsBetweenDates_NullStartTime_throwsIllegalArgumentController() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.getEventsBetweenDates(user, null,
                        ZonedDateTime.now().plusDays(7), List.of(user)));
    }

    @Test
    void getEventsBetweenDates_NullEndTime_throwsIllegalArgumentController() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.getEventsBetweenDates(user, ZonedDateTime.now(),
                        null, List.of(user)));
    }

    @Test
    void getEventsBetweenDates_NullUsersList_throwsIllegalArgumentController() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.getEventsBetweenDates(user, ZonedDateTime.now(),
                        ZonedDateTime.now().plusDays(7), null));
    }

    @Test
    void getEventsBetweenDates_StartTimeAfterEndTime_returnsEmptyList() {
        ZonedDateTime startTime = ZonedDateTime.now().plusDays(7);
        ZonedDateTime endTime = ZonedDateTime.now();
        given(eventRepository.findEventByStartBetween(startTime, endTime)).willReturn(List.of());
        List<Event> eventsBetweenDates = eventService.getEventsBetweenDates(user, startTime, endTime, List.of(user));

        assertEquals(0, eventsBetweenDates.size());
    }

    @Test
    void getEventsBetweenDates_UserDoesNotHaveAccessToEvent_returnsEmptyList() {
        ZonedDateTime startTime = ZonedDateTime.now();
        ZonedDateTime endTime = ZonedDateTime.now().plusDays(7);
        given(eventRepository.findEventByStartBetween(startTime, endTime)).willReturn(List.of(event));
        List<Event> eventsBetweenDates = eventService.getEventsBetweenDates(user, startTime, endTime, List.of(user));

        assertEquals(0, eventsBetweenDates.size());
    }

    @Test
    void getEventsBetweenDates_GoodRequest_returnsEventsList() {
        ZonedDateTime startTime = ZonedDateTime.now();
        ZonedDateTime endTime = ZonedDateTime.now().plusDays(7);
        event.AddUserRole(new Role(user, Role.RoleType.ORGANIZER, Role.StatusType.APPROVED));
        given(eventRepository.findEventByStartBetween(startTime, endTime)).willReturn(List.of(event));
        List<Event> eventsBetweenDates = eventService.getEventsBetweenDates(user, startTime, endTime, List.of(user));

        assertEquals(1, eventsBetweenDates.size());
        assertEquals(event, eventsBetweenDates.get(0));
    }

    @Test
    void addGuestRole_NullEventId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.addGuestRole(null, "test.test@gmail.com"));
    }

    @Test
    void addGuestRole_NullUserEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.addGuestRole(10L, null));
    }

    @Test
    void addGuestRole_InvalidEventId_throwsIllegalArgumentException() {
        given(eventRepository.findById(11L)).willReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                eventService.addGuestRole(11L, "test.test@gmail.com"));
    }

    @Test
    void addGuestRole_InvalidUserEmail_throwsIllegalArgumentException() {
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));
        given(userRepository.findByEmail("test.test@gmail.com")).willReturn(null);
        assertThrows(IllegalArgumentException.class, () ->
                eventService.addGuestRole(10L, "test.test@gmail.com"));
    }

    @Test
    void addGuestRole_UserAlreadyGuest_throwsIllegalArgumentException() {
        event.AddUserRole(new Role(userTwo, Role.RoleType.GUEST, Role.StatusType.TENTATIVE));
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));
        given(userRepository.findByEmail("test.test@gmail.com")).willReturn(userTwo);
        assertThrows(IllegalArgumentException.class, () ->
                eventService.addGuestRole(10L, "test.test@gmail.com"));
    }

    @Test
    void addGuestRole_GoodRequest_returnsNewlyCreatedRole() {
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));
        given(userRepository.findByEmail("test.test@gmail.com")).willReturn(userTwo);

        Role returnedRole = eventService.addGuestRole(10L, "test.test@gmail.com");

        assertTrue(event.getUserRoles().stream().anyMatch(role -> role.getUser().equals(userTwo)));
        assertEquals(userTwo, returnedRole.getUser());
        assertEquals(Role.RoleType.GUEST, returnedRole.getRoleType());
        assertEquals(Role.StatusType.TENTATIVE, returnedRole.getStatusType());
    }

    @Test
    void updateTypeUserRole_NullEventId_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateTypeUserRole(null, 2L));
    }

    @Test
    void updateTypeUserRole_NullUserId_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateTypeUserRole(10L, null));
    }

    @Test
    void updateTypeUserRole_InvalidEventId_throwsIllegalArgument() {
        given(eventRepository.findById(11L)).willReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateTypeUserRole(11L, 2L));
    }

    @Test
    void updateTypeUserRole_InvalidUserId_throwsIllegalArgument() {
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));
        given(userRepository.findById(3L)).willReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateTypeUserRole(10L, 3L));
    }

    @Test
    void updateTypeUserRole_UserNotInvitedToEvent_throwsIllegalArgument() {
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));
        given(userRepository.findById(2L)).willReturn(Optional.of(userTwo));
        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateTypeUserRole(10L, 2L));
    }

    @Test
    void updateTypeUserRole_UserOriginallyGuest_changeUserRoleToAdmin() {
        event.AddUserRole(new Role(userTwo, Role.RoleType.GUEST, Role.StatusType.TENTATIVE));
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));
        given(userRepository.findById(2L)).willReturn(Optional.of(userTwo));
        given(eventRepository.save(event)).willReturn(event);

        Role role = eventService.updateTypeUserRole(10L, 2L);

        assertEquals(userTwo, role.getUser());
        assertEquals(Role.RoleType.ADMIN, role.getRoleType());
        assertTrue(event.getUserRoles().contains(role));
    }

    @Test
    void updateTypeUserRole_UserOriginallyAdmin_changeUserRoleToGuest() {
        event.AddUserRole(new Role(userTwo, Role.RoleType.ADMIN, Role.StatusType.TENTATIVE));
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));
        given(userRepository.findById(2L)).willReturn(Optional.of(userTwo));
        given(eventRepository.save(event)).willReturn(event);

        Role role = eventService.updateTypeUserRole(10L, 2L);

        assertEquals(userTwo, role.getUser());
        assertEquals(Role.RoleType.GUEST, role.getRoleType());
        assertTrue(event.getUserRoles().contains(role));
    }

    @Test
    void updateStatusUserRole_NullEventId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateStatusUserRole(null, user, "APPROVED"));
    }

    @Test
    void updateStatusUserRole_NullUser_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateStatusUserRole(event.getId(), null, "APPROVED"));
    }

    @Test
    void updateStatusUserRole_NullStatus_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateStatusUserRole(event.getId(), user, null));
    }

    @Test
    void updateStatusUserRole_InvalidEventId_throwsIllegalArgumentException() {
        given(eventRepository.findById(11L)).willReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateStatusUserRole(11L, user, "APPROVED"));
    }

    @Test
    void updateStatusUserRole_UserNotInvitedToEvent_throwsIllegalArgumentException() {
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));
        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateStatusUserRole(event.getId(), user, "APPROVED"));
    }

    @Test
    void updateStatusUserRole_ChangeStatusToRejected_returnsStatusRejected() {
        event.AddUserRole(new Role(user, Role.RoleType.ORGANIZER, Role.StatusType.APPROVED));
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));

        Role returnedRole = eventService.updateStatusUserRole(event.getId(), user, "REJECTED");

        assertEquals(user, returnedRole.getUser());
        assertEquals(Role.StatusType.REJECTED, returnedRole.getStatusType());
    }

    @Test
    void updateStatusUserRole_InvalidStatus_DoesNothing() {
        event.AddUserRole(new Role(user, Role.RoleType.ORGANIZER, Role.StatusType.APPROVED));
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));

        Role returnedRole = eventService.updateStatusUserRole(event.getId(), user, "MAYBE");

        assertEquals(user, returnedRole.getUser());
        assertEquals(Role.StatusType.APPROVED, returnedRole.getStatusType());
    }

    @Test
    void deleteRole_NullEventId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
            eventService.deleteRole(null, "test.test@gmail.com"));
    }

    @Test
    void deleteRole_NullUserEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.deleteRole(10L, null));
    }

    @Test
    void deleteRole_InvalidEventId_throwsIllegalArgumentException() {
        given(eventRepository.findById(11L)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                eventService.deleteRole(11L, "test@gmail.com"));
    }

    @Test
    void deleteRole_InvalidUserId_throwsIllegalArgumentException() {
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));
        given(userRepository.findByEmail("test@gmail.com")).willReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                eventService.deleteRole(10L, "test@gmail.com"));
    }

    @Test
    void deleteRole_UserNotGuestInEvent_throwsIllegalArgumentException() {
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));
        given(userRepository.findByEmail("test@gmail.com")).willReturn(user);

        assertThrows(IllegalArgumentException.class, () ->
                eventService.deleteRole(10L, "test@gmail.com"));
    }

    @Test
    void deleteRole_UserOrganizerInEvent_throwsIllegalArgumentException() {
        event.AddUserRole(new Role(user, Role.RoleType.ORGANIZER, Role.StatusType.APPROVED));
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);

        assertThrows(IllegalArgumentException.class, () ->
                eventService.deleteRole(10L, user.getEmail()));
    }

    @Test
    void deleteRole_GoodRequest_deletesUserRole() {
        event.AddUserRole(new Role(user, Role.RoleType.GUEST, Role.StatusType.APPROVED));
        given(eventRepository.findById(10L)).willReturn(Optional.of(event));
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);

        eventService.deleteRole(10L, user.getEmail());

        assertEquals(0, event.getUserRoles().size());
    }

    @Test
    void getRolesForEvent_NullEventId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.getRolesForEvent(null));
    }

    @Test
    void getRolesForEvent_InvalidEventId_throwsIllegalArgumentException() {
        given(eventRepository.findById(11L)).willReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                eventService.getRolesForEvent(11L));
    }

    @Test
    void getRolesForEvent_GoodRequest_returnsRoleList() {
        Role roleOne = new Role(user, Role.RoleType.ORGANIZER, Role.StatusType.APPROVED);
        Role roleTwo = new Role(user, Role.RoleType.GUEST, Role.StatusType.TENTATIVE);
        event.AddUserRole(roleOne);
        event.AddUserRole(roleTwo);
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));

        List<Role> rolesForEvent = eventService.getRolesForEvent(event.getId());

        assertEquals(2, rolesForEvent.size());
        assertTrue(rolesForEvent.containsAll(List.of(roleOne, roleTwo)));
    }

    @Test
    void getRoleByEventAnsUser_NullEventId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.getRoleByEventAndUSer(null, user));
    }

    @Test
    void getRoleByEventAnsUser_NullUser_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                eventService.getRoleByEventAndUSer(event.getId(), null));
    }

    @Test
    void getRoleByEventAnsUser_InvalidEventId_throwsIllegalArgumentException() {
        given(eventRepository.findById(event.getId())).willReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                eventService.getRoleByEventAndUSer(event.getId(), user));
    }

    @Test
    void getRoleByEventAnsUser_UserNotInGuestList_throwsIllegalArgumentException() {
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));
        assertThrows(IllegalArgumentException.class, () ->
                eventService.getRoleByEventAndUSer(event.getId(), user));
    }

    @Test
    void getRoleByEventAnsUser_GoodRequest_returnsRole() {
        Role role = new Role(user, Role.RoleType.ORGANIZER, Role.StatusType.APPROVED);
        event.AddUserRole(role);
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));

        Role roleByEventAndUSer = eventService.getRoleByEventAndUSer(event.getId(), user);

        assertEquals(role, roleByEventAndUSer);
    }
}
