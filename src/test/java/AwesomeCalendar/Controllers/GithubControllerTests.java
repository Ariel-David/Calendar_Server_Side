package AwesomeCalendar.Controllers;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static AwesomeCalendar.Utilities.messages.ExceptionMessage.somethingWrongMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GithubControllerTests {
    @Mock
    AuthService authService;
    @InjectMocks
    GithubController githubController;
    User user1;

    @BeforeEach
    void setup() {
        user1 = new User(0L, "test@test.test", "123456");
    }

    @Test
    void registerWithGitHub_nullCode_null() {
        assertEquals(somethingWrongMessage, githubController.registerWithGitHub("undefined").getBody().getMessage());
    }
}
