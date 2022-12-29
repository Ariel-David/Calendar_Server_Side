package AwesomeCalendar.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**

 Service class for sending pop-up notifications.

 Uses the {@link SimpMessagingTemplate} to convert and send the notification message to a specific user.

 @author [Your Name]
 */
@Service
public class PopUpSender {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    /**
     Method to send a pop-up notification to a specific user.
     @param userEmail the email of the user to send the notification to
     @param message the message to send as the notification
     @throws IllegalArgumentException if the user email is null
     */
    public void sendPopNotification(String userEmail, String message) {
        if(userEmail == null){
            throw new IllegalArgumentException("Invalid user email");
        }
        simpMessagingTemplate.convertAndSend("/notifications/" + userEmail, message);
    }
}
