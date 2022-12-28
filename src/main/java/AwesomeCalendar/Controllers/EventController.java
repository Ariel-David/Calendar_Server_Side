package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomResponse;
import AwesomeCalendar.CustomEntities.EventDTO;
import AwesomeCalendar.CustomEntities.RoleDTO;
import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.EventService;
import AwesomeCalendar.Services.NotificationService;
import AwesomeCalendar.Services.SharingService;
import AwesomeCalendar.Utilities.Validate;
import AwesomeCalendar.enums.NotificationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static AwesomeCalendar.CustomEntities.EventDTO.*;
import static AwesomeCalendar.CustomEntities.RoleDTO.*;
import static AwesomeCalendar.Utilities.messages.ExceptionMessage.*;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.*;

@CrossOrigin
@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @Autowired
    private SharingService sharingService;

    @Autowired
    NotificationService notificationService;

    private static final Logger logger = LogManager.getLogger(EventController.class);

    /**
     * Add new event to the user's calendar
     *
     * @param user  the user that creates the event
     * @param event the new event data
     * @return a SuccessResponse - OK status, a message, the new event data
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<EventDTO>> createEvent(@RequestAttribute("user") User user, @RequestBody Event event) {
        logger.debug("Got request to create event - " + event);
        if (user == null) return ResponseEntity.badRequest().build();
        CustomResponse<EventDTO> cResponse;
        if (event.getStart() == null) {
            cResponse = new CustomResponse<>(null, requiredFieldMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        if (event.getEnd() == null) {
            cResponse = new CustomResponse<>(null, requiredFieldMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        if (event.getTitle() == null) {
            cResponse = new CustomResponse<>(null, requiredFieldMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        if (event.getEventAccess() == null) {
            event.setEventAccess(Event.EventAccess.PRIVATE);
        }
        try {
            Event createdEvent = eventService.createEvent(event, user);
            if (createdEvent == null) {
                cResponse = new CustomResponse<>(null, somethingWrongMessage);
                return ResponseEntity.internalServerError().body(cResponse);
            }
            cResponse = new CustomResponse<>(convertEventToEventDTO(createdEvent), eventCreatedSuccessfullyMessage);
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    /**
     * Add new guest to the event
     *
     * @param eventId   event Id
     * @param userEmail the user that will add to the event
     * @return a SuccessResponse - OK status, a message, the new role
     */
    @RequestMapping(value = "new/role", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<RoleDTO>> createRole(@RequestParam("eventId") Long eventId, @RequestParam("userEmail") String userEmail) {
        logger.debug("Got request to add guest role to event:" + eventId + " for user:" + userEmail);
        CustomResponse<RoleDTO> cResponse;
        if (!Validate.email(userEmail)) {
            cResponse = new CustomResponse<>(null, invalidEmailMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        try {
            Role newRole = eventService.addGuestRole(eventId, userEmail);
            cResponse = new CustomResponse<>(convertRoleToRoleDTO(newRole), roleCreatedSuccessfullyMessage);
            notificationService.sendNotifications(List.of(userEmail), NotificationType.EVENT_INVITATION);
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    /**
     * Update role type for user
     *
     * @param eventId the eventID
     * @param userId  the id of the user that we change role for
     * @return a SuccessResponse - OK status, a message, the new role
     */
    @RequestMapping(value = "update/role/type", method = RequestMethod.PATCH)
    public ResponseEntity<CustomResponse<RoleDTO>> updateRoleType(@RequestParam("eventId") Long eventId, @RequestParam("userId") Long userId) {
        logger.debug("Got request to change role to event:" + eventId + " for user:" + userId);
        CustomResponse<RoleDTO> cResponse;
        try {
            Role newRole = eventService.updateTypeUserRole(eventId, userId);
            cResponse = new CustomResponse<>(convertRoleToRoleDTO(newRole), roleTypeChangedSuccessfullyMessage);
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }

    }

    /**
     * Update role status for user
     *
     * @param user    the user that we change status for
     * @param eventId the event
     * @param status  the new status
     * @return a SuccessResponse - OK status, a message, the updated role with the status
     */
    @RequestMapping(value = "update/role/status", method = RequestMethod.PATCH)
    public ResponseEntity<CustomResponse<RoleDTO>> updateRoleStatus(@RequestAttribute("user") User user, @RequestParam("eventId") Long eventId, @RequestParam("status") String status) {
        logger.debug("Got request to add guest status to event:" + eventId + " for user:" + user.getEmail());
        CustomResponse<RoleDTO> cResponse;
        try {
            if (status.equals("TENTATIVE") || status.equals("REJECTED") || status.equals("APPROVED")) {
                Role newRole = eventService.updateStatusUserRole(eventId, user, status);
                cResponse = new CustomResponse<>(convertRoleToRoleDTO(newRole), roleStatusChangedSuccessfullyMessage);
                notificationService.sendNotifications(List.of(eventService.getEventOrganizer(eventId).get().getEmail()), NotificationType.USER_STATUS_CHANGED);
                return ResponseEntity.ok().body(cResponse);
            }
            cResponse = new CustomResponse<>(null, invalidStatusMessage);
            return ResponseEntity.badRequest().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    /**
     * Delete event from DB
     *
     * @param eventId - the event to delete
     * @return successResponse with OK status ,deleted event, a Message
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<CustomResponse<EventDTO>> deleteEvent(@RequestAttribute("user") User user, @RequestParam("eventId") Long eventId) {
        logger.debug("Got request delete event:" + eventId);
        CustomResponse<EventDTO> cResponse;
        if (eventId == null) {
            cResponse = new CustomResponse<>(null, invalidEventIdMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        try {
            Event deleted_event = eventService.deleteEvent(eventId);
            if (deleted_event == null) {
                cResponse = new CustomResponse<>(null, somethingWrongMessage);
                return ResponseEntity.badRequest().body(cResponse);
            }
            cResponse = new CustomResponse<>(convertEventToEventDTO(deleted_event), deleteEventSuccessfullyMessage);
            List<String> listUserInEvent = deleted_event.getUserRoles().stream().map(role -> role.getUser().getEmail()).filter(email -> !email.equals(user.getEmail())).collect(Collectors.toList());
            notificationService.sendNotifications(listUserInEvent, NotificationType.EVENT_CANCEL);
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    /**
     * Get event from DB
     *
     * @param id the event id
     * @return a SuccessResponse - OK status, a message, the event
     */
    @RequestMapping(value = "/getEvent", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<EventDTO>> getEvent(@RequestParam("id") long id) {
        logger.debug("Got request to get event:" + id);
        CustomResponse<EventDTO> cResponse;
        try {
            Optional<Event> found_event = eventService.getEvent(id);
            if (!found_event.isPresent()) {
                cResponse = new CustomResponse<>(null, somethingWrongMessage);
                return ResponseEntity.badRequest().body(cResponse);
            }
            cResponse = new CustomResponse<>(convertEventToEventDTO(found_event.get()), getEventSuccessfullyMessage);
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    /**
     * Get an event between specific dates
     *
     * @param user      the user
     * @param startDate the start date of the event
     * @param endDate   the end date of the even
     */
    @Deprecated
    @RequestMapping(value = "/getBetweenDates", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<EventDTO>>> getEventsBetweenDates(@RequestAttribute("user") User user, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endDate") ZonedDateTime endDate) {
        logger.debug("Got request to get events between dates:" + startDate + "-" + endDate);
        CustomResponse<List<EventDTO>> cResponse;
        try {
            List<Event> eventList = eventService.getEventsBetweenDates(startDate, endDate);
            if (eventList == null) {
                cResponse = new CustomResponse<>(null, somethingWrongMessage);
                return ResponseEntity.badRequest().body(cResponse);
            }
            cResponse = new CustomResponse<>(convertEventListToEventDTOList(eventList), getEventsBetweenDatesSuccessfullyMessage);
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    /**
     * gets all events between start date and end date that at lease one user from the list is invited to.
     *
     * @param user        the user requesting all the events.
     * @param startDate   where to start the cut of the relevant events.
     * @param endDate     where to end the cut of the relevant events.
     * @param usersEmails the email of the users of which we want to see their calendars.
     * @return all the events matching the parameters.
     */
    @RequestMapping(value = "/getCalendarsBetweenDates", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<EventDTO>>> getEventsBetweenDates(@RequestAttribute("user") User user, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
                                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endDate") ZonedDateTime endDate, @RequestParam List<String> usersEmails) {
        logger.debug("Got request to get events between dates:" + startDate + "-" + endDate + " from calendars:" + usersEmails);
        CustomResponse<List<EventDTO>> cResponse;
        try {
            List<User> shared = sharingService.isShared(user, usersEmails);
            List<Event> eventList = eventService.getEventsBetweenDates(user, startDate, endDate, shared);
            if (eventList == null) {
                cResponse = new CustomResponse<>(null, somethingWrongMessage);
                return ResponseEntity.badRequest().body(cResponse);
            }
            cResponse = new CustomResponse<>(convertEventListToEventDTOList(eventList), getEventsBetweenDatesSuccessfullyMessage);
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    /**
     * Remove user from the event
     *
     * @param eventId   the event id
     * @param userEmail the email of the user that is being deleted from the event
     * @return a SuccessResponse - OK status, a message, the list of emails of deleted users
     */
    @RequestMapping(value = "/removeUser", method = RequestMethod.PATCH)
    public ResponseEntity<CustomResponse<RoleDTO>> removeUser(@RequestParam("eventId") Long eventId, @RequestParam("userEmail") String userEmail) {
        logger.debug("Got request to remove user:" + userEmail + " from event:" + eventId);
        CustomResponse<RoleDTO> cResponse;
        if (eventId == null || userEmail == null) {
            cResponse = new CustomResponse<>(null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        try {
            Role isDeleted = eventService.deleteRole(eventId, userEmail);
            if (isDeleted != null) {
                cResponse = new CustomResponse<>(convertRoleToRoleDTO(isDeleted), getEventsBetweenDatesSuccessfullyMessage);
                notificationService.sendNotifications(List.of(userEmail), NotificationType.USER_UNINVITED);
                return ResponseEntity.ok().body(cResponse);
            } else {
                cResponse = new CustomResponse<>(null, somethingWrongMessage);
                return ResponseEntity.badRequest().body(cResponse);
            }
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    /**
     * Update event data
     *
     * @param userType the role of the user
     * @param eventId  - the event id
     * @param event    - the event data
     * @return successResponse with updated event,Message,OK status
     */
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResponseEntity<CustomResponse<EventDTO>> updateEvent(@RequestAttribute("user") User user,
                                                                @RequestAttribute("userType") Role.RoleType userType, @RequestParam("eventId") Long eventId, @RequestBody Event event) {
        logger.debug("Got request to update event:" + eventId);
        CustomResponse<EventDTO> cResponse;
        if (userType != null && userType.equals(Role.RoleType.ADMIN)) {
            if (event.getTitle() != null || event.getStart() != null || event.getEnd() != null) {
                cResponse = new CustomResponse<>(null, FieldsAdminCantUpdateMessage);
                return ResponseEntity.badRequest().body(cResponse);
            }
        }
        try {
            Event updateEvent = eventService.updateEvent(eventId, event);
            cResponse = new CustomResponse<>(convertEventToEventDTO(updateEvent), updateEventSuccessfullyMessage);
            List<String> listUserInEvent = updateEvent.getUserRoles().stream().map(role -> role.getUser().getEmail())
                    .filter(email -> !email.equals(user.getEmail())).collect(Collectors.toList());
            notificationService.sendNotifications(listUserInEvent, NotificationType.EVENT_DATA_CHANGED);
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    /**
     * Get all the roles at the event
     *
     * @param eventId the event id
     * @return successResponse with roles of the event, a Http-status
     */
    @RequestMapping(value = "/getUsers", method = RequestMethod.GET)
    public ResponseEntity<List<Role>> getRolesOfEvent(@RequestParam Long eventId) {
        logger.debug("Got request to get roles of events:" + eventId);
        if (eventId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok().body(eventService.getRolesForEvent(eventId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

    }

}
