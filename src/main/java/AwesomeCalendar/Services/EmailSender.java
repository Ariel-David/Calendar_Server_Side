package AwesomeCalendar.Services;

import AwesomeCalendar.Utilities.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 A service for sending email notifications to users.

 @implNote The service uses a JavaMailSender to send emails to users. The service provides a method for sending an email notification to a user with a specified email address and body.
 */
@Service
public class EmailSender {
    @Autowired
    private JavaMailSender mailSender;

    /**
     Sends an email notification to a user with the specified email address and body.
     @param userEmail the email address of the user to receive the notification
     @param body the body of the email notification
     @throws IllegalArgumentException if the provided user email is invalid
     @implNote The method first checks if the provided user email is null. If it is, an IllegalArgumentException is thrown. If it is not, the method constructs
     an Email object with the provided user email and body, and uses the JavaMailSender to send the email.
     */
    public void sendEmailNotification(String userEmail,String body) {
        if(userEmail == null){
            throw new IllegalArgumentException("Invalid user email");
        }
        Email email = new Email.Builder().to(userEmail).subject("You Have A New Notification!").content(body).build();
        mailSender.send(email.convertIntoMessage());
    }

}
