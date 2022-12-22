package AwesomeCalendar.Services;

import AwesomeCalendar.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SharingService {

    @Autowired
    private UserRepo userRepository;
}
