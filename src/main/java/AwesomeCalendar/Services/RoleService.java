package AwesomeCalendar.Services;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    RoleRepo roleRepository;

    public Role addRole(Event event, User user, Role.RoleType role, Role.StatusType status) {
        return roleRepository.save(new Role(event, user, role, status));
    }
}
