package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomResponse;
import AwesomeCalendar.CustomEntities.EventDTO;
import AwesomeCalendar.CustomEntities.RoleDTO;
import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.EventService;
import AwesomeCalendar.Services.RoleService;
import AwesomeCalendar.Utilities.Validate;
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
    private RoleService roleService;

    @PostMapping("/new")
    public ResponseEntity<CustomResponse<EventDTO>> createEvent(@RequestAttribute("user") User user, @RequestBody Event event) {
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
        Event createdEvent = eventService.createEvent(event);
        if (createdEvent == null) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.internalServerError().body(cResponse);
        }
        cResponse = new CustomResponse<>(convertEventToEventDTO(createdEvent), null, eventCreatedSuccessfullyMessage);
        roleService.addRole(createdEvent, user, Role.RoleType.ORGANIZER, Role.StatusType.APPROVED);
        return ResponseEntity.ok().body(cResponse);
    }

    @RequestMapping(value = "new/role", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<RoleDTO>> createRole(@RequestParam("eventId") Long eventId, @RequestParam("userEmail") String userEmail) {
        CustomResponse<RoleDTO> cResponse;
        if (!Validate.email(userEmail)) {
            cResponse = new CustomResponse<>(null, null, invalidEmailMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        Role newRole = roleService.addGuestRole(eventId, userEmail);
        cResponse = new CustomResponse<>(convertRoleToRoleDTO(newRole), null, roleCreatedSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }

    @RequestMapping(value = "update/role/type", method = RequestMethod.PATCH)
    public ResponseEntity<CustomResponse<RoleDTO>> updateRoleType(@RequestParam("eventId") Long eventId, @RequestParam("userId") Long userId) {
        Role newRole = roleService.updateTypeUserRole(eventId, userId);
        CustomResponse<RoleDTO> cResponse = new CustomResponse<>(convertRoleToRoleDTO(newRole), null, roleTypeChangedSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }

    @RequestMapping(value = "update/role/status", method = RequestMethod.PATCH)
    public ResponseEntity<CustomResponse<RoleDTO>> updateRoleStatus(@RequestAttribute("user") User user, @RequestParam("eventId") Long eventId, @RequestParam("status") String status) {
        CustomResponse<RoleDTO> cResponse;
        if (status.equals("TENTATIVE") || status.equals("REJECTED") || status.equals("APPROVED")) {
            Role newRole = roleService.updateStatusUserRole(eventId, user, status);
            cResponse = new CustomResponse<>(convertRoleToRoleDTO(newRole), null, roleStatusChangedSuccessfullyMessage);
            return ResponseEntity.ok().body(cResponse);
        }
        cResponse = new CustomResponse<>(null, null, invalidStatusMessage);
        return ResponseEntity.badRequest().body(cResponse);
    }

    @DeleteMapping(value = "delete")
    public ResponseEntity<CustomResponse<EventDTO>> deleteEvent(@RequestParam("eventId") Long eventId) {
        CustomResponse<EventDTO> cResponse;
        if (eventId == null) {
            cResponse = new CustomResponse<>(null, null, invalidEventIdMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        roleService.deleteRolesForEvent(eventId);
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
        CustomResponse<List<EventDTO>> cResponse;
        List<Event> eventList = eventService.getEventsBetweenDates(startDate, endDate);
        if (eventList == null) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        cResponse = new CustomResponse<>(convertEventListToEventDTOList(eventList), null, getEventsBetweenDatesSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }

    @PatchMapping("/removeUser")
    public ResponseEntity<CustomResponse<RoleDTO>> removeUser(@RequestParam("eventId") Long eventId, @RequestParam("userEmail") String userEmail) {
        CustomResponse<RoleDTO> cResponse;
        if (eventId == null || userEmail == null) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        Role isDeleted = roleService.deleteRole(eventId, userEmail);
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
}
