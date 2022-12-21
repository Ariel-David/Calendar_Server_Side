package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Services.EventService;
import AwesomeCalendar.Services.RoleService;
import AwesomeCalendar.Utilities.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private RoleService roleService;

    @PostMapping("/new")
    public ResponseEntity<Event> createEvent(@RequestAttribute("user") User user, @RequestBody Event event) {
        if (user == null) return ResponseEntity.badRequest().build();

        if (event.getStart() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (event.getEnd() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (event.getTitle() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (event.getEventAccess() == null) {
            event.setEventAccess(Event.EventAccess.PRIVATE);
        }

        Event createdEvent = eventService.createEvent(event);

        roleService.addRole(createdEvent, user, Role.RoleType.ORGANIZER, Role.StatusType.APPROVED);
        return ResponseEntity.ok().body(null);
    }

    @RequestMapping(value = "new/role", method = RequestMethod.POST)
    public ResponseEntity<Role> createRole(@RequestParam("eventId") Long eventId, @RequestParam("userEmail") String userEmail) {
        if (!Validate.email(userEmail)) {
            return ResponseEntity.badRequest().body(null);
        }
        Role newRole = roleService.addGuestRole(eventId, userEmail);
        return ResponseEntity.ok().body(newRole);
    }

    @RequestMapping(value = "update/role/type", method = RequestMethod.PATCH)
    public ResponseEntity<Role> updateRoleType(@RequestParam("eventId") Long eventId, @RequestParam("userId") Long userId) {
        Role newRole = roleService.updateTypeUserRole(eventId, userId);
        return ResponseEntity.ok().body(newRole);
    }

    @RequestMapping(value = "update/role/status", method = RequestMethod.PATCH)
    public ResponseEntity<Role> updateRoleStatus(@RequestAttribute("user") User user, @RequestParam("eventId") Long eventId,  @RequestParam("status") String status) {
        if(status.equals("TENTATIVE") || status.equals("REJECTED") || status.equals("APPROVED")){
            Role newRole = roleService.updateStatusUserRole(eventId, user, status);
            return ResponseEntity.ok().body(newRole);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @DeleteMapping(value = "delete")
    public ResponseEntity<String> deleteEvent(@RequestParam("eventId") Long eventId) {
        if (eventId == null) {
            return ResponseEntity.notFound().build();
        }
        roleService.deleteRolesForEvent(eventId);
        Event deleted_event = eventService.deleteEvent(eventId);
        if (deleted_event == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>("Successful deleting event: " + deleted_event.getId(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getEvent", method = RequestMethod.GET)
    public ResponseEntity<Event> getEvent(@RequestParam("id") long id) {
        Optional<Event> found_event = eventService.getEvent(id);
        if (!found_event.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(found_event.get(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getBetweenDates", method = RequestMethod.GET)
    public ResponseEntity<List<Event>> getEventsBetweenDates(@RequestAttribute("user") User user, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endDate") ZonedDateTime endDate) {
        List<Event> eventList = eventService.getEventsBetweenDates(startDate,endDate);
        if (eventList == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(eventList, HttpStatus.OK);
    }

    @PatchMapping("/removeUser")
    public ResponseEntity<Void> removeUser(@RequestParam("eventId") Long eventId, @RequestParam("userEmail") String userEmail) {
        if (eventId == null || userEmail == null) {
            return ResponseEntity.badRequest().build();
        }
        Boolean isDeleted = roleService.deleteRole(eventId, userEmail);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResponseEntity<Event> updateEvent(@RequestAttribute("userType") Role.RoleType userType, @RequestParam("eventId") Long eventId, @RequestBody Event event) {
        if (userType != null && userType.equals(Role.RoleType.ADMIN)) {
            if (event.getTitle() != null || event.getStart() != null || event.getEnd() != null) {
                return ResponseEntity.badRequest().body(null);
            }
        }
        Event updateEvent = eventService.updateEvent(eventId, event);
        return ResponseEntity.ok().body(updateEvent);
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<Role>> getRolesOfEvent(@RequestParam Long eventId) {
        if (eventId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(roleService.getRolesForEvent(eventId));
    }

}
