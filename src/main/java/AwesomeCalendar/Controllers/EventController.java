package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.Event;
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
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        /*Gson gson = new Gson();
        System.out.println(gson.fromJson(user ,User.class).toString());
        if (user != null) return ResponseEntity.ok().body(gson.fromJson(user ,User.class));
        return ResponseEntity.ok().body(null);*/
        return ResponseEntity.ok().body(eventService.createEvent(event));
    }
}
