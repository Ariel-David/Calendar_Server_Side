package AwesomeCalendar.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RealTimeSender{
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public void sendUpdate(String userEmail,Class<?> cls){
        simpMessagingTemplate.convertAndSend("/realTime/" + userEmail, cls.getSimpleName());
    }
}
