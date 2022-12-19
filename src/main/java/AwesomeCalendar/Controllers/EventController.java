package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.EventService;
import AwesomeCalendar.Services.RoleService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@CrossOrigin
@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private RoleService roleService;

    @GetMapping("/new")
    public ResponseEntity<Event> createEvent(@RequestAttribute("user") User user, @RequestBody Event event) {
        if (user == null) return ResponseEntity.badRequest().build();

        if (event.getTime() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (event.getDate() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (event.getDuration() == null) {
            event.setDuration(Duration.of(1, ChronoUnit.HOURS));
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

    @DeleteMapping(value = "{event}")
    public ResponseEntity<String> deleteEvent(@PathVariable Event event) {
        if (event.getId() == null) {
            return ResponseEntity.notFound().build();
        }
        Event deleted_event = eventService.deleteEvent(event);
        if (deleted_event == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>("Successful deleting event: " + deleted_event.getId(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getEvent", method = RequestMethod.GET)
    public ResponseEntity<Event> getEvent(@RequestParam("id") long id) {
        Event found_event = eventService.getEvent(id);
        if (found_event == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(found_event, HttpStatus.OK);
    }
}
