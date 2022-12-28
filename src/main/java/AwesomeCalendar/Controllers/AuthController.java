package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomLoginResponse;
import AwesomeCalendar.CustomEntities.CustomResponse;
import AwesomeCalendar.CustomEntities.UserDTO;
import AwesomeCalendar.Entities.GitHubEmail;
import AwesomeCalendar.Entities.GithubUser;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.AuthService;
import AwesomeCalendar.Utilities.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import org.springframework.web.client.RestTemplate;

import static AwesomeCalendar.CustomEntities.UserDTO.convertUserToUserDTO;
import static AwesomeCalendar.Utilities.messages.ExceptionMessage.*;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private static final Gson gson = new Gson();


    /**
     * checks if the email, password is valid, and send the user to the addUser method in AuthService
     *
     * @param user - the user's data
     * @return a saved user with response body
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> registerUser(@RequestBody User user) {
        logger.debug("Got request for registering - " + user);
        CustomResponse<UserDTO> cResponse;
        if (!Validate.email(user.getEmail())) {
            cResponse = new CustomResponse<>(null, invalidEmailMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        if (!Validate.password(user.getPassword())) {
            cResponse = new CustomResponse<>(null, invalidPasswordMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        try {
            UserDTO registerUserDTO = convertUserToUserDTO(authService.addUser(user));
            cResponse = new CustomResponse<>(registerUserDTO, registerSuccessfullyMessage);
            logger.debug("Successfully registered - " + user);
            return ResponseEntity.ok().body(cResponse);
        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, somethingWrongMessage);
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
    public ResponseEntity<CustomLoginResponse<UserDTO>> login(@RequestBody User user) {
        logger.debug("Got request for login - " + user);
        CustomLoginResponse<UserDTO> cResponse;
        if (!Validate.email(user.getEmail())) {
            cResponse = new CustomLoginResponse<>(null, null, invalidEmailMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        if (!Validate.password(user.getPassword())) {
            cResponse = new CustomLoginResponse<>(null, null, invalidPasswordMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        try {
            Pair<String, User> pair = authService.login(user);
            if (pair != null) {
                cResponse = new CustomLoginResponse<>(convertUserToUserDTO(pair.getSecond()), pair.getFirst(), loginSuccessfullyMessage);
                logger.debug("Successfully logged in - " + user);
                return ResponseEntity.ok(cResponse); // 200
            }
        } catch (IllegalArgumentException e) {
            cResponse = new CustomLoginResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        cResponse = new CustomLoginResponse<>(null, null, somethingWrongMessage);
        return ResponseEntity.badRequest().body(cResponse);
    }
}

