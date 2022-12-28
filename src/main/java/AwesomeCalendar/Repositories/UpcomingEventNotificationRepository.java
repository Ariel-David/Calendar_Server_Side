package AwesomeCalendar.Repositories;

import AwesomeCalendar.Entities.UpcomingEventNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpcomingEventNotificationRepository extends JpaRepository<UpcomingEventNotification, Long> {
}
