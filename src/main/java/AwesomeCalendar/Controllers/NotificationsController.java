package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomResponse;
import AwesomeCalendar.CustomEntities.TimingNotificationsDTO;
import AwesomeCalendar.Entities.NotificationsSettings;
import AwesomeCalendar.Entities.TimingNotifications;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.NotificationService;
import AwesomeCalendar.enums.NotificationsTiming;
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
     * @param user the user
     * @param notificationsSettings the notifications settings of the user
     * @return successResponse with the notifications settings, a Http-status
     */
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

    @PostMapping("/timing")
    public ResponseEntity<CustomResponse<TimingNotificationsDTO>> addTimingNotification(@RequestAttribute("user") User user,
                                                                                     @RequestParam("eventId") Long eventId,
                                                                                     @RequestParam("timing") NotificationsTiming timing) {
        if (user == null || eventId == null || timing == null) {
            return ResponseEntity.badRequest().build();
        }
        TimingNotifications timingNotification;

        try {
            timingNotification = notificationService.addTimingNotification(user, eventId, timing);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new CustomResponse<>(null, null , e.getMessage()));
        }

        if (timingNotification == null) {
            return ResponseEntity.internalServerError().body(new CustomResponse<>(null, null, somethingWrongMessage));
        }
        return ResponseEntity.ok().body(new CustomResponse<>(TimingNotificationsDTO.fromTimingNotification(timingNotification), null, "success"));
    }
}