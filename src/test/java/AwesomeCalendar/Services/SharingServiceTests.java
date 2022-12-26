package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class SharingServiceTests {
    @Mock
    UserRepo userRepository;

    @InjectMocks
    SharingService sharingService;

    User user1;
    User user2;

    @BeforeEach
    void setup() {
        user1 = new User(0L, "test@test.com", "12345");
        user2 = new User(1L, "gideon@gmail.com", "98765");
    }

    @Test
    void shareCalendar_NullUser_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> sharingService.shareCalendar(null,
                        "gideon.jaffe@gmail.com"));
    }

    @Test
    void shareCalendar_NullEmail_throwsIllegalArgumentException() {
        given(userRepository.findByEmail(null)).willReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> sharingService.shareCalendar(user1,
                        null));
    }

    @Test
    void shareCalendar_BadEmail_throwsIllegalArgumentException() {
        given(userRepository.findByEmail("ooblah")).willReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> sharingService.shareCalendar(user1,
                        "ooblah"));
    }

    @Test
    void shareCalendar_AlreadySharedCalendar_throwsIllegalArgumentException() {
        given(userRepository.findByEmail(user2.getEmail())).willReturn(user2);
        user2.addSharedCalendar(user1);
        assertThrows(IllegalArgumentException.class,
                () -> sharingService.shareCalendar(user1,
                        user2.getEmail()));
    }

    @Test
    void shareCalendar_GoodRequest_returnsSharedWithUser() {
        given(userRepository.findByEmail(user2.getEmail())).willReturn(user2);
        given(userRepository.save(user2)).willReturn(user2);

        User userReturned = sharingService.shareCalendar(user1, user2.getEmail());

        assertEquals(user2, userReturned);
        assertTrue(user2.getSharedWithMeCalendars().contains(user1));
    }

    @Test
    void isShared_NullUser_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> sharingService.isShared(null, new ArrayList<>()));
    }

    @Test
    void isShared_NullUsersList_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> sharingService.isShared(user1, null));
    }

    @Test
    void isShared_BadEmailInList_returnsEmptyList() {
        given(userRepository.findByEmailIn(List.of("badString"))).willReturn(List.of());

        List<User> usersList = sharingService.isShared(user1, List.of("badString"));

        assertEquals(0, usersList.size());
    }

    @Test
    void isShared_EmailOfUserNotSharedWith_throwsIllegalArgumentException() {
        given(userRepository.findByEmailIn(List.of(user2.getEmail()))).willReturn(List.of(user2));
        assertThrows(IllegalArgumentException.class,
                () -> sharingService.isShared(user1, List.of(user2.getEmail())));
    }

    @Test
    void isShared_goodRequest_returnsListOfUsers() {
        given(userRepository.findByEmailIn(List.of(user2.getEmail()))).willReturn(List.of(user2));
        user1.addSharedCalendar(user2);

        List<User> shared = sharingService.isShared(user1, List.of(user2.getEmail()));

        assertEquals(1, shared.size());
        assertEquals(user2, shared.get(0));
    }
}
