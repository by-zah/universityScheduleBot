package ua.khnu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.ScheduleUnit;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleUnit, Integer> {

}
