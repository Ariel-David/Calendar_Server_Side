package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import AwesomeCalendar.Utilities.Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    UserRepo userRepository;

    @InjectMocks
    AuthService authService;

    User user;

    @BeforeEach
    void setup() {
        user = new User(1L,"test.test@gmail.com", "12345");
    }

    @Test
    void addUser_NullUser_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.addUser(null));
    }

    @Test
    void addUser_EmailAlreadyInUse_throwsIllegalArgumentException() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> authService.addUser(user));
    }

    @Test
    void addUser_NullUserEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.addUser(new User(null, "12345")));
    }

    @Test
    void addUser_NullUserPassword_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.addUser(new User("test.test@gmail.com", null)));
    }

    @Test
    void addUser_GoodRequest_returnsUserWithEncryptedPassword() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(null);
        given(userRepository.save(Mockito.any(User.class))).will(returnsFirstArg());

        User user1 = authService.addUser(user);

        assertEquals(user1.getEmail(), user.getEmail());
        assertTrue(Utility.matchesPasswords(user.getPassword(), user1.getPassword()));
    }

    @Test
    void login_NullUser_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.login(null));
    }

    @Test
    void login_NullUserEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.login(new User(null, "12345")));
    }

    @Test
    void login_NullUserPassword_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.login(new User("test.test@gmail.com", null)));
    }

    @Test
    void login_InvalidEmail_throwsIllegalArgumentException() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> authService.login(user));
    }

    @Test
    void login_InvalidPassword_throwsIllegalArgumentException() {
        User withEncryptedPassword = User.registeredUser(user);
        given(userRepository.findByEmail(user.getEmail())).willReturn(withEncryptedPassword);
        user.setPassword("qwe");
        assertThrows(IllegalArgumentException.class,
                () -> authService.login(user));
    }

    @Test
    void login_GoodRequest_returnsTokenAndUser() {
        User withEncryptedPassword = User.registeredUser(user);
        given(userRepository.findByEmail(user.getEmail())).willReturn(withEncryptedPassword);
        Pair<String, User> userPair = authService.login(user);

        assertEquals(withEncryptedPassword, userPair.getSecond());
    }

    @Test
    void checkToken_NullToken_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.checkToken(null));
    }

    @Test
    void checkToken_InvalidToken_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.checkToken("qwertyuyio"));
    }

    @Test
    void checkToken_GoodRequest_throwsIllegalArgumentException() {
        authService.getKeyTokensValEmails().put("myToken", user.getEmail());
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);

        User returnedUser = authService.checkToken("myToken");

        assertEquals(user, returnedUser);
    }
}
