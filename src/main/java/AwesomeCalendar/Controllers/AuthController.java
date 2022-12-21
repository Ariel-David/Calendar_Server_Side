package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.UserDTO;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.AuthService;
import AwesomeCalendar.Utilities.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<UserDTO> registerUser(@RequestBody User user) {
        if (!Validate.email(user.getEmail())) {
            return ResponseEntity.badRequest().body(null);
        }
        if (!Validate.password(user.getPassword())) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            UserDTO registerUserDTO = UserDTO.convertUserToUserDTO(authService.addUser(user));
            return ResponseEntity.ok().body(registerUserDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * checks if the email, password is valid, and send the user to the login method in AuthService
     *
     * @param user - the user's data
     * @return user with response body
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody User user) {
        if (!Validate.email(user.getEmail())) {
            return ResponseEntity.badRequest().body(null);
        }
        if (!Validate.password(user.getPassword())) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            String token = authService.login(user);
            if (token != null) {
                Map<String, String> map = new HashMap<>();
                map.put("token", token);
                return ResponseEntity.ok(gson.toJson(map)); // 200
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
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }
}

