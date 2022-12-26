package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomResponse;
import AwesomeCalendar.CustomEntities.EventDTO;
import AwesomeCalendar.CustomEntities.RoleDTO;
import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.EventService;
import AwesomeCalendar.Services.SharingService;
import AwesomeCalendar.Utilities.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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

    private static final Logger logger = LogManager.getLogger(EventController.class);

    @PostMapping("/new")
    public ResponseEntity<CustomResponse<EventDTO>> createEvent(@RequestAttribute("user") User user, @RequestBody Event event) {
        logger.debug("Got request to create event - " + event);
        if (user == null) return ResponseEntity.badRequest().build();
        CustomResponse<EventDTO> cResponse;
        if (event.getStart() == null) {
            cResponse = new CustomResponse<>(null, null, requiredFieldMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        if (event.getEnd() == null) {
            cResponse = new CustomResponse<>(null, null, requiredFieldMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        if (event.getTitle() == null) {
            cResponse = new CustomResponse<>(null, null, requiredFieldMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        if (event.getEventAccess() == null) {
            event.setEventAccess(Event.EventAccess.PRIVATE);
        }
        Event createdEvent = eventService.createEvent(event, user);
        if (createdEvent == null) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.internalServerError().body(cResponse);
        }
        cResponse = new CustomResponse<>(convertEventToEventDTO(createdEvent), null, eventCreatedSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }

    @RequestMapping(value = "new/role", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<RoleDTO>> createRole(@RequestParam("eventId") Long eventId, @RequestParam("userEmail") String userEmail) {
        logger.debug("Got request to add guest role to event:" + eventId + " for user:" + userEmail);
        CustomResponse<RoleDTO> cResponse;
        if (!Validate.email(userEmail)) {
            cResponse = new CustomResponse<>(null, null, invalidEmailMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        Role newRole = eventService.addGuestRole(eventId, userEmail);
        cResponse = new CustomResponse<>(convertRoleToRoleDTO(newRole), null, roleCreatedSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }

    @RequestMapping(value = "update/role/type", method = RequestMethod.PATCH)
    public ResponseEntity<CustomResponse<RoleDTO>> updateRoleType(@RequestParam("eventId") Long eventId, @RequestParam("userId") Long userId) {
        logger.debug("Got request to change role to event:" + eventId + " for user:" + userId);
        Role newRole = eventService.updateTypeUserRole(eventId, userId);
        CustomResponse<RoleDTO> cResponse = new CustomResponse<>(convertRoleToRoleDTO(newRole), null, roleTypeChangedSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }

    @RequestMapping(value = "update/role/status", method = RequestMethod.PATCH)
    public ResponseEntity<CustomResponse<RoleDTO>> updateRoleStatus(@RequestAttribute("user") User user, @RequestParam("eventId") Long eventId, @RequestParam("status") String status) {
        logger.debug("Got request to add guest status to event:" + eventId + " for user:" + user.getEmail());
        CustomResponse<RoleDTO> cResponse;
        if (status.equals("TENTATIVE") || status.equals("REJECTED") || status.equals("APPROVED")) {
            Role newRole = eventService.updateStatusUserRole(eventId, user, status);
            cResponse = new CustomResponse<>(convertRoleToRoleDTO(newRole), null, roleStatusChangedSuccessfullyMessage);
            return ResponseEntity.ok().body(cResponse);
        }
        cResponse = new CustomResponse<>(null, null, invalidStatusMessage);
        return ResponseEntity.badRequest().body(cResponse);
    }

    @DeleteMapping(value = "delete")
    public ResponseEntity<CustomResponse<EventDTO>> deleteEvent(@RequestParam("eventId") Long eventId) {
        logger.debug("Got request delete event:" + eventId);
        CustomResponse<EventDTO> cResponse;
        if (eventId == null) {
            cResponse = new CustomResponse<>(null, null, invalidEventIdMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        //roleService.deleteRolesForEvent(eventId);
        Event deleted_event = eventService.deleteEvent(eventId);
        if (deleted_event == null) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        cResponse = new CustomResponse<>(convertEventToEventDTO(deleted_event), null, deleteEventSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }

    @RequestMapping(value = "/getEvent", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<EventDTO>> getEvent(@RequestParam("id") long id) {
        logger.debug("Got request to get event:" + id);
        CustomResponse<EventDTO> cResponse;
        Optional<Event> found_event = eventService.getEvent(id);
        if (!found_event.isPresent()) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        cResponse = new CustomResponse<>(convertEventToEventDTO(found_event.get()), null, getEventSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }

    @RequestMapping(value = "/getBetweenDates", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<EventDTO>>> getEventsBetweenDates(@RequestAttribute("user") User user, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endDate") ZonedDateTime endDate) {
        logger.debug("Got request to get events between dates:" + startDate + "-" + endDate);
        CustomResponse<List<EventDTO>> cResponse;
        List<Event> eventList = eventService.getEventsBetweenDates(startDate, endDate);
        if (eventList == null) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        cResponse = new CustomResponse<>(convertEventListToEventDTOList(eventList), null, getEventsBetweenDatesSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
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
        List<User> shared = sharingService.isShared(user, usersEmails);
        List<Event> eventList = eventService.getEventsBetweenDates(user, startDate, endDate, shared);
        if (eventList == null) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        cResponse = new CustomResponse<>(convertEventListToEventDTOList(eventList), null, getEventsBetweenDatesSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }

    @PatchMapping("/removeUser")
    public ResponseEntity<CustomResponse<RoleDTO>> removeUser(@RequestParam("eventId") Long eventId, @RequestParam("userEmail") String userEmail) {
        logger.debug("Got request to remove user:" + userEmail + " from event:" + eventId);
        CustomResponse<RoleDTO> cResponse;
        if (eventId == null || userEmail == null) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        Role isDeleted = eventService.deleteRole(eventId, userEmail);
        if (isDeleted != null) {
            cResponse = new CustomResponse<>(convertRoleToRoleDTO(isDeleted), null, getEventsBetweenDatesSuccessfullyMessage);
            return ResponseEntity.ok().body(cResponse);
        } else {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResponseEntity<CustomResponse<EventDTO>> updateEvent(@RequestAttribute("userType") Role.RoleType userType, @RequestParam("eventId") Long eventId, @RequestBody Event event) {
        logger.debug("Got request to update event:" + eventId);
        CustomResponse<EventDTO> cResponse;
        if (userType != null && userType.equals(Role.RoleType.ADMIN)) {
            if (event.getTitle() != null || event.getStart() != null || event.getEnd() != null) {
                cResponse = new CustomResponse<>(null, null, FieldsAdminCantUpdateMessage);
                return ResponseEntity.badRequest().body(cResponse);
            }
        }
        Event updateEvent = eventService.updateEvent(eventId, event);
        cResponse = new CustomResponse<>(convertEventToEventDTO(updateEvent), null, updateEventSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<Role>> getRolesOfEvent(@RequestParam Long eventId) {
        logger.debug("Got request to get roles of events:" + eventId);
        if (eventId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(eventService.getRolesForEvent(eventId));
    }

}
