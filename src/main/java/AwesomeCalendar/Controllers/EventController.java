package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.EventService;
import AwesomeCalendar.Services.RoleService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private RoleService roleService;

    @GetMapping("/new")
    public ResponseEntity<Event> createEvent(@RequestAttribute("theUser") User user, @RequestBody Event event) {
        if (user == null) return ResponseEntity.badRequest().build();

        if (event.getTime() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (event.getDate() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (event.getDuration() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (event.getTitle() == null) {
            return ResponseEntity.badRequest().build();
        }

        Event createdEvent = eventService.createEvent(event);

        roleService.addRole(createdEvent, user, Role.RoleType.ORGANIZER, Role.StatusType.APPROVED);
        return ResponseEntity.ok().body(null);
    }
}
