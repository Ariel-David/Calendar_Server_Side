package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.CustomLoginResponse;
import AwesomeCalendar.CustomEntities.UserDTO;
import AwesomeCalendar.Entities.GitHubEmail;
import AwesomeCalendar.Entities.GithubUser;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static AwesomeCalendar.Utilities.messages.ExceptionMessage.couldNotGetUserFromGithubMessage;
import static AwesomeCalendar.Utilities.messages.ExceptionMessage.somethingWrongMessage;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.gitHubUserLoggedInSuccessfullyMessage;

@CrossOrigin
@RestController
@RequestMapping("/auth/github")
public class GithubController {
    @Autowired
    private AuthService authService;
    private static final Logger logger = LogManager.getLogger(AuthController.class);

    /**
     * Login using GitHub
     *
     * @param code - the code from GitHub's API
     * @return a SuccessResponse - OK status, a message, the login data - user's DTO and the generated token
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<CustomLoginResponse<UserDTO>> registerWithGitHub(@RequestParam String code) {
        logger.debug("Got request for login through github - " + code);
        if (code.equals("undefined")) {
            return ResponseEntity.badRequest().body(new CustomLoginResponse<UserDTO>(null, null, somethingWrongMessage));
        }
        try {
            User user = githubConfig(code);
            try {
                authService.addUser(user);
            } catch (IllegalArgumentException e) {

            }
            Pair<String, User> login = authService.login(user);
            String loginToken = login.getFirst();
            logger.debug("Successfully logged in through github - " + user);
            return ResponseEntity.ok().body(new CustomLoginResponse<UserDTO>(UserDTO.convertUserToUserDTO(login.getSecond()), loginToken, gitHubUserLoggedInSuccessfullyMessage));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new CustomLoginResponse<UserDTO>(null, null, e.getMessage()));
        }
    }

    private User githubConfig(String code) {
        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> res = rest.postForEntity("https://github.com/login/oauth/access_token?code=" + code + "&client_id=2298388bcf5985aa7bcb" + "&client_secret=c50b29b012b0b535aa7d2f20627b8ebf790b390a" + "&scope=user:email", null, String.class);
        HttpHeaders headers = new HttpHeaders();
        String token = res.getBody().split("&")[0].split("=")[1];
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<GithubUser> exchange = rest.exchange("https://api.github.com/user", HttpMethod.GET, entity, GithubUser.class);
        GithubUser githubUser = exchange.getBody();
        if (githubUser == null) {
            throw new IllegalArgumentException(couldNotGetUserFromGithubMessage);
        }
        githubUser.setAccessToken(token);
        ResponseEntity<GitHubEmail[]> exchange2 = rest.exchange("https://api.github.com/user/emails", HttpMethod.GET, entity, GitHubEmail[].class);
        GitHubEmail[] githubUserMail = exchange2.getBody();
        if (githubUserMail == null) {
            throw new IllegalArgumentException(couldNotGetUserFromGithubMessage);
        }
        return newUser(githubUser, githubUserMail);
    }

    private User newUser(GithubUser githubUser, GitHubEmail[] githubUserMail) {
        return new User(githubUserMail[githubUserMail.length - 1].getEmail(), githubUser.getName() + githubUserMail[githubUserMail.length - 1].getEmail());
    }
}
