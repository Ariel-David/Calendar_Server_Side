package AwesomeCalendar.Services;

import AwesomeCalendar.Utilities.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {
    @Autowired
    private JavaMailSender mailSender;

    private void sendEmailNotification(String userEmail,String body) {
        Email email = new Email.Builder().to(userEmail).subject("You Have A New Notification!").content(body).build();
        mailSender.send(email.convertIntoMessage());
    }
    public void userStatusChanged(String userEmail){
        sendEmailNotification(userEmail,"You have a new event invitation");
    }
    public void eventDataChanged(String userEmail){
        sendEmailNotification(userEmail,"Event data changed");
    }
    public void eventCanceled(String userEmail){
        sendEmailNotification(userEmail,"Event canceled");
    }
    public void userUninvitedFromEvent(String userEmail){
        sendEmailNotification(userEmail,"You uninvited from event");
    }
    public void notificationUpcomingEvents(String userEmail){
        sendEmailNotification(userEmail,"Your event is upcoming!");
    }

}
