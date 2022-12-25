package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomResponse;
import AwesomeCalendar.Entities.NotificationsSettings;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static AwesomeCalendar.Utilities.messages.ExceptionMessage.somethingWrongMessage;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.setNotificationsSuccessfullyMessage;

@CrossOrigin
@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/settings")
    public ResponseEntity<CustomResponse<NotificationsSettings>> setNotificationsSettings(@RequestAttribute("user") User user, @RequestBody NotificationsSettings notificationsSettings) {
        if (user == null) return ResponseEntity.badRequest().build();
        CustomResponse<NotificationsSettings> cResponse;
        NotificationsSettings setNotifications = notificationService.setNotificationsSettings(user, notificationsSettings);
        if (setNotifications == null) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.internalServerError().body(cResponse);
        }
        cResponse = new CustomResponse<>(setNotifications, null, setNotificationsSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }

}