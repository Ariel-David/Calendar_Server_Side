package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Repositories.RoleRepo;
import AwesomeCalendar.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    RoleRepo roleRepository;
    @Autowired
    EventRepo eventRepository;
    @Autowired
    UserRepo userRepository;

    public Role addRole(Event event, User user, Role.RoleType role, Role.StatusType status) {
        return roleRepository.save(new Role(event, user, role, status));
    }

    public Role addGuestRole(Long eventId, String userEmail) {
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        User userInDB = userRepository.findByEmail(userEmail);
        if (userInDB == null) {
            throw new IllegalArgumentException("Invalid user email");
        }
        Role roleInDB = roleRepository.findByEventAndUser(eventInDB.get(), userInDB);
        if (roleInDB != null) {
            throw new IllegalArgumentException("Exist role");
        }
        return roleRepository.save(new Role(eventInDB.get(), userInDB, Role.RoleType.GUEST, Role.StatusType.TENTATIVE));
    }

    public Role updateTypeUserRole(Long eventId, Long userId) {
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        Optional<User> UserInDB = userRepository.findById(userId);
        if (!UserInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid user id");
        }
        Role updatedRoleInDB = roleRepository.findByEventAndUser(eventInDB.get(), UserInDB.get());
        if (updatedRoleInDB == null) {
            throw new IllegalArgumentException("You have not received an invitation to this event");
        }

        if (updatedRoleInDB.getRoleType().equals(Role.RoleType.GUEST)) {
            updatedRoleInDB.setRoleType(Role.RoleType.ADMIN);
        } else if (updatedRoleInDB.getRoleType().equals(Role.RoleType.ADMIN)) {
            updatedRoleInDB.setRoleType(Role.RoleType.GUEST);
        }

        return roleRepository.save(updatedRoleInDB);
    }

    public Role updateStatusUserRole(Long eventId, User user, String status) {
        Optional<Event> eventInDB = eventRepository.findById(eventId);
        if (!eventInDB.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }
        Role updatedRoleInDB = roleRepository.findByEventAndUser(eventInDB.get(), user);
        if (updatedRoleInDB == null) {
            throw new IllegalArgumentException("You have not received an invitation to this event");
        }

        if (status.equals("APPROVED")) {
            updatedRoleInDB.setStatusType(Role.StatusType.APPROVED);
        } else if (status.equals("REJECTED")) {
            updatedRoleInDB.setStatusType(Role.StatusType.REJECTED);
        } else if (status.equals("TENTATIVE")) {
            updatedRoleInDB.setStatusType(Role.StatusType.TENTATIVE);
        }

        return roleRepository.save(updatedRoleInDB);
    }

    public Role deleteRole(Long eventId, String userEmail) {
        Optional<Role> userRole = roleRepository.getByEventIdAndUserEmail(eventId, userEmail);
        if (!userRole.isPresent() || userRole.get().getRoleType() == Role.RoleType.ORGANIZER) {
            throw new IllegalArgumentException("Trying to delete an organizer");
        }
        roleRepository.deleteById(userRole.get().getId());
        return userRole.get();
    }

    public List<Role> deleteRolesForEvent(Long eventId) {
        return roleRepository.deleteByEventId(eventId);
    }

    public List<Role> getRolesForEvent(Long eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("event id cant bt null");
        }
        return roleRepository.getByEventId(eventId);
    }
}
