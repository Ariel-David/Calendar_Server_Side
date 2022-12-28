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

    /**
     * Login using GitHub
     *
     * @param code - the code from GitHub's API
     * @return a SuccessResponse - OK status, a message, the login data - user's DTO and the generated token
     */
    @RequestMapping(value = "gitHub", method = RequestMethod.POST)
    public ResponseEntity<CustomLoginResponse<UserDTO>> registerWithGitHub(@RequestParam String code) {
        logger.debug("Got request for login through github - " + code);
        if (code.equals("undefined")) {
            return ResponseEntity.badRequest().body(new CustomLoginResponse<UserDTO>(null, null, somethingWrongMessage));
        }
        try {
            RestTemplate rest = new RestTemplate();
            ResponseEntity<String> res = rest.postForEntity("https://github.com/login/oauth/access_token?code=" + code + "&client_id=2298388bcf5985aa7bcb" + "&client_secret=c50b29b012b0b535aa7d2f20627b8ebf790b390a" + "&scope=user:email", null, String.class);
            HttpHeaders headers = new HttpHeaders();
            String token = res.getBody().split("&")[0].split("=")[1];
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<GithubUser> exchange = rest.exchange("https://api.github.com/user", HttpMethod.GET, entity, GithubUser.class);
            GithubUser githubUser = exchange.getBody();
            if (githubUser == null) {
                return ResponseEntity.badRequest().body(new CustomLoginResponse<>(null, null, couldNotGetUserFromGithubMessage));
            }
            githubUser.setAccessToken(token);
            ResponseEntity<GitHubEmail[]> exchange2 = rest.exchange("https://api.github.com/user/emails", HttpMethod.GET, entity, GitHubEmail[].class);
            GitHubEmail[] githubUserMail = exchange2.getBody();
            if (githubUserMail == null) {
                return ResponseEntity.badRequest().body(new CustomLoginResponse<>(null, null, couldNotGetUserFromGithubMessage));
            }
            User user = new User(githubUserMail[githubUserMail.length - 1].getEmail(), githubUser.getName() + githubUserMail[githubUserMail.length - 1].getEmail());
            try {
                authService.addUser(user);
            } catch (IllegalArgumentException e) {

            }
            Pair<String, User> login = authService.login(user);
            String loginToken = login.getFirst();
            logger.debug("Successfully logged in through github - " + user);
            return ResponseEntity.ok().body(new CustomLoginResponse<UserDTO>(UserDTO.convertUserToUserDTO(login.getSecond()), loginToken, gitHubUserLoggedInSuccessfullyMessage));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new CustomLoginResponse<UserDTO>(null, null, somethingWrongMessage));
        }
    }
}

