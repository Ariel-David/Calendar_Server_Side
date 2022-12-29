package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.NotificationsSettings;
import AwesomeCalendar.Entities.UpcomingEventNotification;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Repositories.UpcomingEventNotificationRepository;
import AwesomeCalendar.Repositories.UserRepo;
import AwesomeCalendar.enums.NotificationHandler;
import AwesomeCalendar.enums.NotificationType;
import AwesomeCalendar.enums.NotificationsTiming;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    UserRepo userRepository;

    @InjectMocks
    NotificationService notificationService;

    @Mock
    EmailSender emailSender;

    @Mock
    PopUpSender popUpSender;

    @Mock
    RealTimeSender realTimeSender;

    @Mock
    EventRepo eventRepository;

    @Mock
    UpcomingEventNotificationRepository upcomingEventNotificationRepository;

    User user;

    User user2;

    Event event;

    List<String> userEmails;

    @BeforeEach
    void setup() {
        user = new User(1L, "test.test@gmail.com", "12345");
        user.setNotificationsSettings(new NotificationsSettings(NotificationHandler.Both, NotificationHandler.Both, NotificationHandler.Both, NotificationHandler.Both, NotificationHandler.Both, NotificationHandler.Both));
        user2 = new User(2L, "test.second@gmail.com", "12345");
        user2.setNotificationsSettings(new NotificationsSettings(NotificationHandler.Both, NotificationHandler.Both, NotificationHandler.Both, NotificationHandler.Both, NotificationHandler.Both, NotificationHandler.Both));
        userEmails = new ArrayList<>(List.of("test.test@gmail.com", "test.second@gmail.com"));
        event = new Event(11L, Event.EventAccess.PUBLIC, ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(3), "location", "title", "description");
     }

    @Test
    void set_nullNotificationsSettings() {
        assertThrows(IllegalArgumentException.class,
                () -> notificationService.setNotificationsSettings(user, user.getNotificationsSettings()));
    }

    @Test
    void set_NotificationsSettings() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);
        given(userRepository.save(user)).willReturn(null);
        NotificationsSettings notificationsSettings = new NotificationsSettings(NotificationHandler.Both, NotificationHandler.Email, NotificationHandler.Email, NotificationHandler.Popup, NotificationHandler.Both, NotificationHandler.Email);
        notificationService.setNotificationsSettings(user, notificationsSettings);
        assertEquals(user.getNotificationsSettings(), notificationsSettings);
    }

    @Test
    void sendNotifications_NullUserList() {
        assertThrows(IllegalArgumentException.class,
                () -> notificationService.sendNotifications(null, NotificationType.EVENT_INVITATION, null));
    }

    @Test
    void sendNotifications_BadUserEmail_throwsIllegalArgumentException() {
        given(userRepository.findByEmail("myemail@google.com")).willReturn(null);
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.sendNotifications(List.of("myemail@google.com"), NotificationType.EVENT_INVITATION, event));
    }

    @Test
    void sendNotifications_EventCanceled_sendsUpdates() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);
        given(userRepository.findByEmail(user2.getEmail())).willReturn(user2);
        doNothing().when(realTimeSender).sendUpdate(Mockito.any(), Mockito.any());
        doNothing().when(emailSender).sendEmailNotification(Mockito.any(), Mockito.any());
        doNothing().when(popUpSender).sendPopNotification(Mockito.any(), Mockito.any());
        notificationService.sendNotifications(userEmails, NotificationType.EVENT_CANCEL, event);
        verify(emailSender, times(2)).sendEmailNotification(Mockito.any(), Mockito.any());
        verify(popUpSender, times(2)).sendPopNotification(Mockito.any(), Mockito.any());
        verify(realTimeSender, times(2)).sendUpdate(Mockito.any(), Mockito.any());
    }

    @Test
    void sendNotifications_EventInvitation_sendsUpdates() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);
        given(userRepository.findByEmail(user2.getEmail())).willReturn(user2);
        doNothing().when(realTimeSender).sendUpdate(Mockito.any(), Mockito.any());
        doNothing().when(emailSender).sendEmailNotification(Mockito.any(), Mockito.any());
        doNothing().when(popUpSender).sendPopNotification(Mockito.any(), Mockito.any());
        notificationService.sendNotifications(userEmails, NotificationType.EVENT_INVITATION, event);
        verify(emailSender, times(2)).sendEmailNotification(Mockito.any(), Mockito.any());
        verify(popUpSender, times(2)).sendPopNotification(Mockito.any(), Mockito.any());
        verify(realTimeSender, times(2)).sendUpdate(Mockito.any(), Mockito.any());
    }

    @Test
    void sendNotifications_UserUninvited_sendsUpdates() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);
        given(userRepository.findByEmail(user2.getEmail())).willReturn(user2);
        doNothing().when(realTimeSender).sendUpdate(Mockito.any(), Mockito.any());
        doNothing().when(emailSender).sendEmailNotification(Mockito.any(), Mockito.any());
        doNothing().when(popUpSender).sendPopNotification(Mockito.any(), Mockito.any());
        notificationService.sendNotifications(userEmails, NotificationType.USER_UNINVITED, event);
        verify(emailSender, times(2)).sendEmailNotification(Mockito.any(), Mockito.any());
        verify(popUpSender, times(2)).sendPopNotification(Mockito.any(), Mockito.any());
        verify(realTimeSender, times(2)).sendUpdate(Mockito.any(), Mockito.any());
    }

    @Test
    void sendNotifications_UserStatusChanged_sendsUpdates() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);
        given(userRepository.findByEmail(user2.getEmail())).willReturn(user2);
        doNothing().when(realTimeSender).sendUpdate(Mockito.any(), Mockito.any());
        doNothing().when(emailSender).sendEmailNotification(Mockito.any(), Mockito.any());
        doNothing().when(popUpSender).sendPopNotification(Mockito.any(), Mockito.any());
        notificationService.sendNotifications(userEmails, NotificationType.USER_STATUS_CHANGED, event);
        verify(emailSender, times(2)).sendEmailNotification(Mockito.any(), Mockito.any());
        verify(popUpSender, times(2)).sendPopNotification(Mockito.any(), Mockito.any());
        verify(realTimeSender, times(2)).sendUpdate(Mockito.any(), Mockito.any());
    }

    @Test
    void sendNotifications_EventDataChanged_sendsUpdates() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);
        given(userRepository.findByEmail(user2.getEmail())).willReturn(user2);
        doNothing().when(realTimeSender).sendUpdate(Mockito.any(), Mockito.any());
        doNothing().when(emailSender).sendEmailNotification(Mockito.any(), Mockito.any());
        doNothing().when(popUpSender).sendPopNotification(Mockito.any(), Mockito.any());
        notificationService.sendNotifications(userEmails, NotificationType.EVENT_DATA_CHANGED, event);
        verify(emailSender, times(2)).sendEmailNotification(Mockito.any(), Mockito.any());
        verify(popUpSender, times(2)).sendPopNotification(Mockito.any(), Mockito.any());
        verify(realTimeSender, times(2)).sendUpdate(Mockito.any(), Mockito.any());
    }

    @Test
    void sendNotifications_UpcomingEvent_sendsUpdates() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);
        given(userRepository.findByEmail(user2.getEmail())).willReturn(user2);
        doNothing().when(realTimeSender).sendUpdate(Mockito.any(), Mockito.any());
        doNothing().when(emailSender).sendEmailNotification(Mockito.any(), Mockito.any());
        doNothing().when(popUpSender).sendPopNotification(Mockito.any(), Mockito.any());
        notificationService.sendNotifications(userEmails, NotificationType.UPCOMING_EVENT, event);
        verify(emailSender, times(2)).sendEmailNotification(Mockito.any(), Mockito.any());
        verify(popUpSender, times(2)).sendPopNotification(Mockito.any(), Mockito.any());
        verify(realTimeSender, times(2)).sendUpdate(Mockito.any(), Mockito.any());
    }

    @Test
    void sendNotifications_SharingCalendar_sendsUpdates() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);
        doNothing().when(realTimeSender).sendUpdate(Mockito.any(), Mockito.any());
        notificationService.sendNotifications(List.of(user.getEmail()), NotificationType.SHARE_CALENDAR, event);
        verify(realTimeSender, times(1)).sendUpdate(Mockito.any(), Mockito.any());
    }

    @Test
    void sendHelper_Email_sendsToEmailSender() {
        doNothing().when(emailSender).sendEmailNotification(user.getEmail(), "notify");
        notificationService.sendHelper(user, NotificationHandler.Email, "notify");
        verify(emailSender, times(1)).sendEmailNotification(user.getEmail(), "notify");
    }

    @Test
    void sendHelper_PopUp_sendsToPopUpSender() {
        doNothing().when(popUpSender).sendPopNotification(user.getEmail(), "notify");
        notificationService.sendHelper(user, NotificationHandler.Popup, "notify");
        verify(popUpSender, times(1)).sendPopNotification(user.getEmail(), "notify");
    }

    @Test
    void sendHelper_Both_sendsToPopUpSenderAndEmailSender() {
        doNothing().when(emailSender).sendEmailNotification(user.getEmail(), "notify");
        doNothing().when(popUpSender).sendPopNotification(user.getEmail(), "notify");
        notificationService.sendHelper(user, NotificationHandler.Both, "notify");
        verify(emailSender, times(1)).sendEmailNotification(user.getEmail(), "notify");
        verify(popUpSender, times(1)).sendPopNotification(user.getEmail(), "notify");
    }

    @Test
    void sendHelper_None_doesNotSend() {
        notificationService.sendHelper(user, NotificationHandler.None, "notify");
        verify(emailSender, times(0)).sendEmailNotification(user.getEmail(), "notify");
        verify(popUpSender, times(0)).sendPopNotification(user.getEmail(), "notify");
    }

    @Test
    void getNotificationsSettings_GoodUser_ReturnsSetting(){
        NotificationsSettings notificationsSettings = notificationService.getNotificationsSettings(user);
        assertEquals(notificationsSettings, user.getNotificationsSettings());
    }

    @Test
    void addUpcomingEventNotification_NullUser_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.addUpcomingEventNotification(null, event.getId(), NotificationsTiming.HALF_HOUR));
    }

    @Test
    void addUpcomingEventNotification_NullEventId_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.addUpcomingEventNotification(user, null, NotificationsTiming.HALF_HOUR));
    }

    @Test
    void addUpcomingEventNotification_NullTiming_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.addUpcomingEventNotification(user, event.getId(), null));
    }

    @Test
    void addUpcomingEventNotification_InvalidEventId_throwsIllegalArgumentException(){
        given(eventRepository.findById(12L)).willReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.addUpcomingEventNotification(user, 12L, NotificationsTiming.HALF_HOUR));
    }

    @Test
    void addUpcomingEventNotification_GoodRequest_savesToDBAndReturnsUpcoming(){
        given(eventRepository.findById(event.getId())).willReturn(Optional.ofNullable(event));
        given(upcomingEventNotificationRepository.save(Mockito.any())).willReturn(null);
        UpcomingEventNotification upcomingEventNotification = notificationService.addUpcomingEventNotification(user, event.getId(), NotificationsTiming.HALF_HOUR);
        verify(upcomingEventNotificationRepository, times(1)).save(Mockito.any());
        assertEquals(event, upcomingEventNotification.getEvent());
        assertEquals(NotificationsTiming.HALF_HOUR, upcomingEventNotification.getNotificationTiming());
        assertEquals(user, upcomingEventNotification.getUser());
    }
}