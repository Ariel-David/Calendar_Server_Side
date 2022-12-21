package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomResponse;
import AwesomeCalendar.CustomEntities.UserDTO;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.AuthService;
import AwesomeCalendar.Utilities.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static AwesomeCalendar.CustomEntities.UserDTO.convertUserToUserDTO;
import static AwesomeCalendar.Utilities.messages.ExceptionMessage.*;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    private static final Gson gson = new Gson();


    /**
     * checks if the email, password is valid, and send the user to the addUser method in AuthService
     *
     * @param user - the user's data
     * @return a saved user with response body
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> registerUser(@RequestBody User user) {
        CustomResponse<UserDTO> cResponse;
        if (!Validate.email(user.getEmail())) {
            cResponse = new CustomResponse<>(null, null, invalidEmailMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        if (!Validate.password(user.getPassword())) {
            cResponse = new CustomResponse<>(null, null, invalidPasswordMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        try {
            UserDTO registerUserDTO = convertUserToUserDTO(authService.addUser(user));
            cResponse = new CustomResponse<>(registerUserDTO, null, registerSuccessfullyMessage);
            return ResponseEntity.ok().body(cResponse);
        }
        catch (IllegalArgumentException e){
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
    }

    /**
     * checks if the email, password is valid, and send the user to the login method in AuthService
     *
     * @param user - the user's data
     * @return user with response body
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> login(@RequestBody User user) {
        CustomResponse<UserDTO> cResponse;
        if (!Validate.email(user.getEmail())) {
            cResponse = new CustomResponse<>(null, null, invalidEmailMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        if (!Validate.password(user.getPassword())) {
            cResponse = new CustomResponse<>(null, null, invalidPasswordMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        try {
            Pair<String, User> pair = authService.login(user);
            if (pair != null) {
                cResponse = new CustomResponse<>(convertUserToUserDTO(pair.getSecond()), pair.getFirst(), loginSuccessfullyMessage);
                return ResponseEntity.ok(cResponse); // 200
            }
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
        return ResponseEntity.badRequest().body(cResponse);
    }
}

