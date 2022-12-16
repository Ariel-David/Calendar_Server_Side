package AwesomeCalendar.Controllers;

import AwesomeCalendar.CustomEntities.UserDTO;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * checks if the email, password is valid, and send the user to the addUser method in AuthService
     *
     * @param user - the user's data
     * @return a saved user with response body
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> registerUser(@RequestBody User user) {
//        CustomResponse<UserDTO> response = new CustomResponse<>(null, emptyString);
        try {
//            Optional<CustomResponse<UserDTO>> isValid = checkValidEmail(user.getEmail(), response);
//            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}
//            isValid = checkValidName(user.getName(), response);
//            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}
//            isValid = checkValidPassword(user.getPassword(), response);
//            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}

//            logger.info(beforeAnAction(user.getEmail(), "register"));
            UserDTO registerUserDTO = UserDTO.convertUserToUserDTO(authService.addUser(user));
//            response.setResponse(UserDTO.userToUserDTO(registerUser));
//            response.setMessage(registrationSuccessfulMessage);
//            EmailUtilityFacade.sendMessage(registerUser.getEmail(), registerUser.getVerifyCode());
//            logger.info(registrationSuccessfulMessage);
            return ResponseEntity.ok().body(registerUserDTO);
        } catch (IllegalArgumentException e) {
//            logger.error(e.getMessage());
//            response.setMessage(e.getMessage());
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
//        CustomResponse<UserDTO> response = new CustomResponse<>(null, emptyString);
        try {
//            Optional<CustomResponse<UserDTO>> isValid = checkValidEmail(user.getEmail(), response);
//            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}
//            isValid = checkValidPassword(user.getPassword(), response);
//            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}
//
//            logger.info(beforeAnAction(user.getEmail(), "login"));
            String token = authService.login(user);
//            UserDTO loginUserDTO = UserDTO.convertUserToUserDTO(authService.login(user));
//            response.setResponse(UserDTO.userToUserDTO(loginUser));
//            response.setMessage(loginSuccessfulMessage);
//            response.setHeaders(authService.getKeyEmailsValTokens().get(loginUser.getEmail()));
//            logger.info(loginSuccessfulMessage);
            return ResponseEntity.ok().body(token);
        } catch (IllegalArgumentException e) {
//            logger.error(e.getMessage());
//            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
}
