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

    /**
     * Set all the notifications settings
     *
     * @param user                  the user
     * @param notificationsSettings the notifications settings of the user
     * @return successResponse with the notifications settings, a Http-status
     */
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<NotificationsSettings>> setNotificationsSettings(@RequestAttribute("user") User user, @RequestBody NotificationsSettings notificationsSettings) {
        if (user == null) return ResponseEntity.badRequest().build();
        CustomResponse<NotificationsSettings> cResponse;
        try {
            NotificationsSettings setNotifications = notificationService.setNotificationsSettings(user, notificationsSettings);
            if (setNotifications == null) {
                cResponse = new CustomResponse<>(null, somethingWrongMessage);
                return ResponseEntity.internalServerError().body(cResponse);
            }
            cResponse = new CustomResponse<>(setNotifications, setNotificationsSuccessfullyMessage);
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }
    }
}