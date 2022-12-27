package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Repositories.UserRepo;
import AwesomeCalendar.Utilities.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepo eventRepository;

    @Autowired
    private UserRepo userRepository;

    private static final Logger logger = LogManager.getLogger(EventService.class.getName());

    /**
     * create an event based on the given event and saves it in the database.
     * @param event the event details from which to create the event.
     * @param user the user that is creating the event.
     * @return the created event.
     * @throws IllegalArgumentException if the event or user are null
     */
    public Event createEvent(Event event, User user) {
        try {
            Utility.checkArgsNotNull(event, user);
            logger.info("Creating event:" + event);
            event.addUserRole(new Role(user, Role.RoleType.ORGANIZER, Role.StatusType.APPROVED));
            return eventRepository.save(event);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Arguments must be non null");
        }


    }
    public Event updateEvent(Long eventId, Event event) {
        logger.info("Updating event:" + eventId + " details to:" + event);
        Utility.checkArgsNotNull(eventId, event);
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        if (event.getEventAccess() != null) {
            eventInDB.get().setEventAccess(event.getEventAccess());
        }
        if (event.getStart() != null) {
            eventInDB.get().setStart(event.getStart());
        }
        if (event.getEnd() != null) {
            eventInDB.get().setEnd(event.getEnd());
        }
        if (event.getLocation() != null && !event.getLocation().equals("")) {
            eventInDB.get().setLocation(event.getLocation());
        }
        if (event.getTitle() != null && !event.getTitle().equals("")) {
            eventInDB.get().setTitle(event.getTitle());
        }
        if (event.getDescription() != null && !event.getDescription().equals("")) {
            eventInDB.get().setDescription(event.getDescription());
        }
        eventRepository.save(eventInDB.get());
        return eventInDB.get();
    }

    public Event deleteEvent(Long eventId) {
        Utility.checkArgsNotNull(eventId);
        logger.debug("Check if the event exist in DB");
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        logger.debug("Delete the event from DB");
        eventRepository.delete(eventInDB.get());
        return eventInDB.get();
    }

    public Optional<User> getEventOrganizer(Long eventId){
        logger.debug("Get the organizer of the event");
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        logger.debug("Get the event from DB");
        return eventInDB.get().getUserRoles().stream().filter(role -> role.getRoleType().equals(Role.RoleType.ORGANIZER)).map(role -> role.getUser()).findFirst();
    }

    public Optional<Event> getEvent(Long id){
        Utility.checkArgsNotNull(id);
        logger.debug("Check if the event exist in DB");
        Optional<Event> eventInDB = eventRepository.findById(id);
        logger.debug("Found the event");
        return eventInDB;
    }
    @Deprecated
    public List<Event> getEventsBetweenDates(ZonedDateTime startDate , ZonedDateTime endDate){
        logger.debug("Getting events between dates");
        return eventRepository.findEventByStartBetween(startDate , endDate);
    }

    /**
     * gets all events between start date and end date that at lease one user from the list is invited to.
     * @param startDate where to start the cut of the relevant events.
     * @param endDate where to end the cut of the relevant events.
     * @param calendars the users of which we want to see their calendars.
     * @return all the events matching the parameters.
     * @throws IllegalArgumentException if one of the parameters is null
     */
    public List<Event> getEventsBetweenDates(User user, ZonedDateTime startDate , ZonedDateTime endDate, List<User> calendars){
        Utility.checkArgsNotNull(user, startDate, endDate, calendars);
        logger.debug("Getting events between dates by calendars");
        List<Event> events = eventRepository.findEventByStartBetween(startDate, endDate);
        return events.stream()
                .filter(event -> event.getUserRoles().stream().anyMatch(role -> calendars.contains(role.getUser()))
                        && (event.getEventAccess() == Event.EventAccess.PUBLIC
                        || (event.getEventAccess() == Event.EventAccess.PRIVATE && event.getUserRoles().contains(user))))
                .collect(Collectors.toList());
    }

    public Role addGuestRole(Long eventId, String userEmail) {
        Utility.checkArgsNotNull(eventId, userEmail);
        logger.info("Adding guest role for user:" + userEmail + " in event:" + eventId);
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        User userInDB = userRepository.findByEmail(userEmail);
        if (userInDB == null) {
            throw new IllegalArgumentException("Invalid user email");
        }
        List<Role> userRoles = eventInDB.get().getUserRoles();
        if (userRoles.stream().anyMatch((role) -> role.getUser() == userInDB)) {
            throw new IllegalArgumentException("Exist role");
        }
        Role role = new Role(userInDB, Role.RoleType.GUEST, Role.StatusType.TENTATIVE);
        eventInDB.get().addUserRole(role);
        eventRepository.save(eventInDB.get());
        return role;
    }

    public Role updateTypeUserRole(Long eventId, Long userId) {
        Utility.checkArgsNotNull(eventId, userId);
        logger.info("Updating user role for user:" + userId + " in event:" + eventId);
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        Optional<User> userInDB = userRepository.findById(userId);
        if (!userInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid user id");
        }
        Optional<Role> userRole = eventInDB.get().getUserRole(userInDB.get());
        if (!userRole.isPresent()) {
            throw new IllegalArgumentException("You have not received an invitation to this event");
        }

        if (userRole.get().getRoleType().equals(Role.RoleType.GUEST)) {
            userRole.get().setRoleType(Role.RoleType.ADMIN);
        } else if (userRole.get().getRoleType().equals(Role.RoleType.ADMIN)) {
            userRole.get().setRoleType(Role.RoleType.GUEST);
        }
        eventRepository.save(eventInDB.get());
        return userRole.get();
    }

    public Role updateStatusUserRole(Long eventId, User user, String status) {
        Utility.checkArgsNotNull(eventId, user, status);
        logger.info("Updating user status for user:" + user.getEmail() + " in event:" + eventId);
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        Optional<Role> userRole = eventInDB.get().getUserRole(user);
        if (!userRole.isPresent()) {
            throw new IllegalArgumentException("You have not received an invitation to this event");
        }
        if (status.equals("APPROVED")) {
            userRole.get().setStatusType(Role.StatusType.APPROVED);
        } else if (status.equals("REJECTED")) {
            userRole.get().setStatusType(Role.StatusType.REJECTED);
        } else if (status.equals("TENTATIVE")) {
            userRole.get().setStatusType(Role.StatusType.TENTATIVE);
        }

        eventRepository.save(eventInDB.get());
        return userRole.get();
    }

    public Role deleteRole(Long eventId, String userEmail) {
        Utility.checkArgsNotNull(eventId, userEmail);
        logger.info("deleting role for user:" + userEmail + " in event:" + eventId);
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        User userInDB = userRepository.findByEmail(userEmail);
        if (userInDB == null) {
            throw new IllegalArgumentException("Invalid user id");
        }
        Optional<Role> userRole = eventInDB.get().getUserRole(userInDB);
        if (!userRole.isPresent()) {
            throw new IllegalArgumentException("User not in guest list");
        } else if (userRole.get().getRoleType() == Role.RoleType.ORGANIZER) {
            throw new IllegalArgumentException("Trying to delete an organizer");
        }
        eventInDB.get().removeUserRole(userRole.get());
        eventRepository.save(eventInDB.get());
        return userRole.get();
    }

    public List<Role> getRolesForEvent(Long eventId) {
        Utility.checkArgsNotNull(eventId);
        logger.debug("getting roles for event:" + eventId);
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        return eventInDB.get().getUserRoles();
    }

    public Role getRoleByEventAndUSer(Long eventId, User user) {
        Utility.checkArgsNotNull(eventId, user);
        logger.info("getting role for user:" + user.getEmail() + " in event:" + eventId);
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        Optional<Role> userRole = eventInDB.get().getUserRole(user);
        if (!userRole.isPresent()) {
            throw new IllegalArgumentException("User not in guest list");
        }
        return userRole.get();
    }
}
