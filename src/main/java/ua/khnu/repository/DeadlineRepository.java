package ua.khnu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.khnu.entity.Deadline;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeadlineRepository extends JpaRepository<Deadline, Integer> {

    Optional<Deadline> findByGroupNameAndClassNameAndDeadLineTime(String groupName, String className, LocalDateTime deadlineTime);

    List<Deadline> findByDeadLineTimeGreaterThan(LocalDateTime deadlineTime);

}
