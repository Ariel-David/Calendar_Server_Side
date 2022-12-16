package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Services.EventService;
import AwesomeCalendar.Services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin
@Controller
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private RoleService roleService;

    @RequestMapping("/new")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        return ResponseEntity.ok().body(eventService.createEvent(event));
    }
}
