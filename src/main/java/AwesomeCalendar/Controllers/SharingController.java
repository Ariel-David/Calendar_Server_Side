package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomResponse;
import AwesomeCalendar.CustomEntities.UserDTO;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.SharingService;
import AwesomeCalendar.Utilities.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static AwesomeCalendar.Utilities.messages.ExceptionMessage.*;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.*;

@CrossOrigin
@RestController
@RequestMapping("/sharing")
public class SharingController {
    @Autowired
    private SharingService sharingService;

    private static final Logger logger = LogManager.getLogger(SharingController.class);

    /**
     * share a calendar with another user. the other user will be able to see this user's public events
     * even if he was not invited to them
     * @param user the user that wants to share a calendar with another user
     * @param userEmail the email of the other user that the user wants to share their calendar with
     * @return the user that was shared the calendar
     */
    @PostMapping("/share")
    public ResponseEntity<CustomResponse<UserDTO>> shareCalendar(@RequestAttribute("user") User user, @RequestParam String userEmail) {
        logger.debug("got request to share calendar");
        if (user == null) return ResponseEntity.badRequest().build();
        CustomResponse<UserDTO> cResponse;
        if (!Validate.email(userEmail)) {
            cResponse = new CustomResponse<>(null, null, invalidEmailMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        try {
            User sharedUser = sharingService.shareCalendar(user, userEmail);
            cResponse = new CustomResponse<>(UserDTO.convertUserToUserDTO(sharedUser), null, shareCalendarSuccessfullyMessage);
            logger.debug("successfully shared calendar");
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    /**
     * gets all calendars shared with the user. the user should call this function
     * before requesting calendars, so he can see what calendars he can access to
     * @param user the user that wants to see what calendars are shared with him.
     *             if called from a rest call, the server expects a token and the filter will change it to user object.
     * @return all the calendars that are shared with the user.
     */
    @GetMapping("/sharedWithMe")
    public ResponseEntity<CustomResponse<List<UserDTO>>> sharedWithMeCalendars(@RequestAttribute("user") User user) {
        logger.debug("got request for calendars shared with me");
        if (user == null) return ResponseEntity.badRequest().build();
        List<User> sharedWithMeCalendars = user.getSharedWithMeCalendars();
        sharedWithMeCalendars.add(user);
        CustomResponse<List<UserDTO>> cResponse = new CustomResponse<>(UserDTO.convertUserListToUserDTOList(sharedWithMeCalendars), null, getSharedCalendarsSuccessfullyMessage);
        return ResponseEntity.ok().body(cResponse);
    }
}
