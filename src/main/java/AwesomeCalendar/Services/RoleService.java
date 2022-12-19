package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Repositories.RoleRepo;
import AwesomeCalendar.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Role addGuestRole(Long eventId, String userEmail){
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

    public Boolean deleteRole(Long eventId, String userEmail) {
        Optional<Role> userRole = roleRepository.getByEventIdAndUserEmail(eventId, userEmail);
        if (!userRole.isPresent() || userRole.get().getRoleType() == Role.RoleType.ORGANIZER) {
            return false;
        }
        roleRepository.deleteById(userRole.get().getId());
        return true;
    }
}
