package AwesomeCalendar.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PopUpSender {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public void sendPopNotification(String userEmail, String message) {
        simpMessagingTemplate.convertAndSend("/notifications/" + userEmail, message);
    }
}