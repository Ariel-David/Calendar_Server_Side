package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.EventService;
import AwesomeCalendar.Services.RoleService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        /*Gson gson = new Gson();
        System.out.println(gson.fromJson(user ,User.class).toString());
        if (user != null) return ResponseEntity.ok().body(gson.fromJson(user ,User.class));
        return ResponseEntity.ok().body(null);*/
        return ResponseEntity.ok().body(eventService.createEvent(event));
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
