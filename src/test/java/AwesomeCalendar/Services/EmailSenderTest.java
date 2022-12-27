package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.UserRepo;
import AwesomeCalendar.Utilities.Email;
import AwesomeCalendar.enums.NotificationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderTest {

    @Mock
    UserRepo userRepository;
    @InjectMocks
    EmailSender emailSender;

    @Mock
    JavaMailSender mailSender;
    User user;

    @BeforeEach
    void setup() {
        user = new User(1L,"test.test@gmail.com", "12345");
    }

    @Test
    void sendEmailNotification_nullUserEmail(){
        assertThrows(IllegalArgumentException.class, () -> emailSender.sendEmailNotification(null,"body"));
    }

    @Test
    void sendEmailNotification(){
        Email email = new Email.Builder().to(user.getEmail()).subject("You Have A New Notification!").content("body").build();
        doNothing().when(mailSender).send(email.convertIntoMessage());
        emailSender.sendEmailNotification(user.getEmail(),"body");
        verify(mailSender,times(1)).send(email.convertIntoMessage());
    }
}