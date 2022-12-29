package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PopUpSenderTest {
    @Mock
    UserRepo userRepository;

    @InjectMocks
    PopUpSender popUpSender;

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;
    User user;

    @BeforeEach
    void setup() {
        user = new User(1L, "test.test@gmail.com", "12345");
    }

    @Test
    void sendPopNotification_NullUserEmail() {
        assertThrows(IllegalArgumentException.class, () -> popUpSender.sendPopNotification(null, "body"));
    }

    @Test
    void sendPopNotification() {
        doNothing().when(simpMessagingTemplate).convertAndSend("/notifications/" + user.getEmail(), "message");
        popUpSender.sendPopNotification(user.getEmail(), "message");
        verify(simpMessagingTemplate, times(1)).convertAndSend("/notifications/" + user.getEmail(), "message");
    }
}