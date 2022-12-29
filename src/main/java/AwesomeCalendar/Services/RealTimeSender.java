package AwesomeCalendar.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 The RealTimeSender class is a service that sends real-time updates to clients via websockets.
 It uses the SimpMessagingTemplate to send updates to a specific user's websocket connection.
 **/
@Service
public class RealTimeSender{
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    /**
     Sends a real-time update to a specific user's websocket connection.
     @param userEmail the email of the user to send the update to
     @param cls the class of the object being updated (used to determine which client-side component to update)
     */
    public void sendUpdate(String userEmail,Class<?> cls){
        simpMessagingTemplate.convertAndSend("/realTime/" + userEmail, cls.getSimpleName());
    }
}
