package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import static AwesomeCalendar.Utilities.messages.ExceptionMessage.*;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.loginSuccessfullyMessage;
import static AwesomeCalendar.Utilities.messages.SuccessMessages.registerSuccessfullyMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {
    @Mock
    AuthService authService;
    @InjectMocks
    AuthController authController;
    User user1;
    User user2;

    @BeforeEach
    void setup() {
        user1 = new User(0L, "test@test.test", "123456");
    }

    @Test
    void registerUser_notValidateEmail_invalidEmailMessage() {
        user2 = new User(0L, "test@test", "123456");
        assertEquals(invalidEmailMessage, authController.registerUser(user2).getBody().getMessage());
    }

    @Test
    void registerUser_notValidatePassword_invalidPasswordMessage() {
        user2 = new User(0L, "test@test.test", "1");
        assertEquals(invalidPasswordMessage, authController.registerUser(user2).getBody().getMessage());
    }

    @Test
    void registerUser_okRegisterUser_registerSuccessfullyMessage() {
        given(authService.addUser(user1)).willReturn(user1);
        assertEquals(registerSuccessfullyMessage, authController.registerUser(user1).getBody().getMessage());
    }

    @Test
    void registerUser_nullRegisterUser_somethingWrongMessage() {
        given(authService.addUser(user1)).willThrow(IllegalArgumentException.class);
        assertEquals(somethingWrongMessage, authController.registerUser(user1).getBody().getMessage());
    }

    @Test
    void login_notValidateEmail_invalidEmailMessage() {
        user2 = new User(0L, "test@test", "123456");
        assertEquals(invalidEmailMessage, authController.login(user2).getBody().getMessage());
    }

    @Test
    void login_notValidatePassword_invalidPasswordMessage() {
        user2 = new User(0L, "test@test.test", "1");
        assertEquals(invalidPasswordMessage, authController.login(user2).getBody().getMessage());
    }

    @Test
    void login_okLogin_loginSuccessfullyMessage() {
        Pair<String, User> pair = Pair.of("123", user1);
        given(authService.login(user1)).willReturn(pair);
        assertEquals(loginSuccessfullyMessage, authController.login(user1).getBody().getMessage());
    }

    @Test
    void login_nullPair_somethingWrongMessage() {
        given(authService.login(user1)).willReturn(null);
        assertEquals(somethingWrongMessage, authController.login(user1).getBody().getMessage());
    }

    @Test
    void login_IllegalArgumentException_somethingWrongMessage() {
        given(authService.login(user1)).willThrow(IllegalArgumentException.class);
        assertEquals(somethingWrongMessage, authController.login(user1).getBody().getMessage());
    }

    @Test
    void registerWithGitHub_nullCode_null() {
        assertEquals(somethingWrongMessage, authController.registerWithGitHub("undefined").getBody().getMessage());
    }
}
