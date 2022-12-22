package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomResponse;
import AwesomeCalendar.CustomEntities.UserDTO;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.AuthService;
import AwesomeCalendar.Utilities.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import org.springframework.web.client.RestTemplate;

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
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }

    private static class githubUser {
        public String getLogin() {
            return Login;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        String Login;
        String name;
        String email;
        githubUser(){

        }
    }

    @RequestMapping(value = "gitHub", method = RequestMethod.POST)
    public ResponseEntity<String> registerWithGitHub(@RequestBody String code) {
        try {
            RestTemplate rest = new RestTemplate();
            rest.postForEntity("https://github.com/login/oauth/access_token?code=" + code + "&client_id=2298388bcf5985aa7bcb" + "&client_secret=c50b29b012b0b535aa7d2f20627b8ebf790b390a", null, String.class);
//            HttpHeaders headers = new HttpHeaders();
//            headers.set(HttpHeaders.AUTHORIZATION, "bearer " + token);
//            HttpEntity<Void> entity = new HttpEntity<>(headers);
//            ResponseEntity<githubUser> githubUser = rest.exchange("https://api.github.com/user/", HttpMethod.GET, entity, githubUser.class);

        } catch (IllegalArgumentException e) {
            cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
            return ResponseEntity.badRequest().body(cResponse);
        }
        cResponse = new CustomResponse<>(null, null, somethingWrongMessage);
        return ResponseEntity.badRequest().body(cResponse);
    }
}

