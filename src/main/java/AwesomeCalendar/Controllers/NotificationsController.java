package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomResponse;
import AwesomeCalendar.Entities.NotificationsSettings;
import AwesomeCalendar.Entities.UpcomingEventNotification;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.NotificationService;
import AwesomeCalendar.enums.NotificationsTiming;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static AwesomeCalendar.Utilities.messages.ExceptionMessage.mustSendTokenEventIdAndTimingMessage;
import static AwesomeCalendar.Utilities.messages.ExceptionMessage.somethingWrongMessage;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.createdUpcomingNotificationSuccessfullyMessage;
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

    /**
     * adds an upcomingEventNotification setting for the user.
     * @param user the user that wants the notification
     * @param eventId the event he wants the notification for.
     * @param timing the time before the event that he wants the notification.
     * @return if there are no problems - a response entity with a status code of OK and a body containing the new
     * upcoming notification created.
     * if there is a problem - return a code of bad request and the body will be null with an error message.
     */
    @RequestMapping(value = "/upcoming", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UpcomingEventNotification>> addUpcomingNotification(@RequestAttribute("user") User user, @RequestParam Long eventId, @RequestParam NotificationsTiming timing) {
        if (user == null || eventId == null || timing == null) {
            return ResponseEntity.badRequest().body(new CustomResponse<>(null, mustSendTokenEventIdAndTimingMessage));
        }
        try {
            UpcomingEventNotification upcomingEventNotification = notificationService.addUpcomingEventNotification(user, eventId, timing);
            return ResponseEntity.ok(new CustomResponse<>(upcomingEventNotification, createdUpcomingNotificationSuccessfullyMessage));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new CustomResponse<>(null, e.getMessage()));
        }
    }
}