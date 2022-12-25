package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomResponse;
import AwesomeCalendar.CustomEntities.UserDTO;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.SharingService;
import AwesomeCalendar.Utilities.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static AwesomeCalendar.Utilities.messages.ExceptionMessage.*;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.*;

@CrossOrigin
@RestController
@RequestMapping("/sharing")
public class SharingController {
    @Autowired
    private SharingService sharingService;

    @PostMapping("/share")
    public ResponseEntity<CustomResponse<UserDTO>> shareCalendar(@RequestAttribute("user") User user, @RequestParam String userEmail) {
        if (user == null) return ResponseEntity.badRequest().build();
        CustomResponse<UserDTO> cResponse;
        if (!Validate.email(userEmail)) {
            cResponse = new CustomResponse<>(null, null, invalidEmailMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        try {
            User sharedUser = sharingService.shareCalendar(user, userEmail);
            cResponse = new CustomResponse<>(UserDTO.convertUserToUserDTO(sharedUser), null, shareCalendarSuccessfullyMessage);
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, null, e.getMessage());
            return ResponseEntity.badRequest().body(cResponse);
        }

    }
}
