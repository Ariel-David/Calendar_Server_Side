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
import java.util.Set;
import java.util.stream.Collectors;
/**

 A service for managing events and their related data.

 @implNote The service uses an EventRepo and UserRepo to access and manipulate events and users in the database. The service provides methods for creating, updating, and deleting events, as well as for retrieving the organizer of an event, a specific event, and all events between two dates.
 */
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

    /**
     * Update an event based on the given event and saves it in the database.
     * @param event the event details from which to create the event.
     * @param eventId the user that is creating the event.
     * @return the created event.
     * @throws IllegalArgumentException if the event or user are null
     */
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

    /**
     Deletes an event from the database.
     @param eventId the id of the event to delete.
     @return the deleted event.
     @throws IllegalArgumentException if the eventId is null or if the event with the given id does not exist in the database.
     */
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

    /**
     Retrieves the organizer of an event from the database.
     @param eventId the id of the event for which to retrieve the organizer.
     @return an optional containing the organizer of the event, or an empty optional if no such event exists or if the event has no organizer.
     @throws IllegalArgumentException if the eventId is null.
     */
    public Optional<User> getEventOrganizer(Long eventId){
        logger.debug("Get the organizer of the event");
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        logger.debug("Get the event from DB");
        return eventInDB.get().getUserRoles().stream().filter(role -> role.getRoleType().equals(Role.RoleType.ORGANIZER)).map(role -> role.getUser()).findFirst();
    }

    /**
     * Retrieves an event by id.
     * @param id the id of the event to retrieve.
     * @return the event with the given id, if it exists. Returns an empty Optional if the event does not exist.
     * @throws IllegalArgumentException if the event id is null.
     */
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
                        || (event.getEventAccess() == Event.EventAccess.PRIVATE && event.getUserRoles().stream().anyMatch(role -> role.getUser().equals(user)))))
                .collect(Collectors.toList());
    }

    /**
     Adds a guest role to the specified event for the user with the given email.
     @param eventId the id of the event to add the guest role to
     @param userEmail the email of the user to add the guest role for
     @return the created guest role
     @throws IllegalArgumentException if the event id or user email are null, or if an invalid event id or user email is provided
     @throws IllegalArgumentException if the user already has a role in the event
     */
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
        Set<Role> userRoles = eventInDB.get().getUserRoles();
        if (userRoles.stream().anyMatch((role) -> role.getUser() == userInDB)) {
            throw new IllegalArgumentException("Exist role");
        }
        Role role = new Role(userInDB, Role.RoleType.GUEST, Role.StatusType.TENTATIVE);
        eventInDB.get().addUserRole(role);
        eventRepository.save(eventInDB.get());
        return role;
    }

    /**
     Update the type of the role of the user in the event
     @param eventId the id of the event
     @param userId the id of the user
     @return the updated role of the user
     @throws IllegalArgumentException if the event id, user id or role type are null or if the event id or user id are invalid
     */
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

    /**
     Updates the status of the role of the given user in the given event.
     @param eventId the id of the event whose role's status is to be updated
     @param user the user whose role's status is to be updated
     @param status the new status to be set for the role
     @return the updated role
     @throws IllegalArgumentException if any of the arguments are null or if the event id or user are invalid
     @throws IllegalArgumentException if the user has not received an invitation to the event
     */
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

    /**
     Deletes a role for a user in an event.
     @param eventId the id of the event
     @param userEmail the email of the user
     @return the deleted role
     @throws IllegalArgumentException if the event id is invalid or if the user id is invalid or if the user is not in the guest list or if the user is the organizer
     */
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

    /**
     Gets the roles for an event.
     @param eventId the id of the event
     @return the set of roles for the event
     @throws IllegalArgumentException if the event id is invalid
     */
    public Set<Role> getRolesForEvent(Long eventId) {
        Utility.checkArgsNotNull(eventId);
        logger.debug("getting roles for event:" + eventId);
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        return eventInDB.get().getUserRoles();
    }

    /**
     Gets the role of a user in an event.
     @param eventId the id of the event
     @param user the user
     @return the role of the user in the event
     @throws IllegalArgumentException if the event id is invalid or if the user is not in the guest list
     */
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

    /**
     Retrieves an event by its ID.
     @param eventId The ID of the event to retrieve.
     @return The event with the specified ID.
     @throws IllegalArgumentException if the event ID is invalid or the event does not exist.
     */
    public Event getEventById(Long eventId) {
        logger.info("getting event by id " + eventId);
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        return eventInDB.get();
    }
}
