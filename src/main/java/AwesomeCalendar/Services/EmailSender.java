package AwesomeCalendar.Services;

import AwesomeCalendar.Utilities.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailNotification(String userEmail,String body) {
        Email email = new Email.Builder().to(userEmail).subject("You Have A New Notification!").content(body).build();
        mailSender.send(email.convertIntoMessage());
    }

}
