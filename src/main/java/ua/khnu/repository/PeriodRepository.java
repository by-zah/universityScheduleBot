package ua.khnu.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.Group;
import ua.khnu.entity.Period;
import ua.khnu.entity.PeriodType;
import ua.khnu.entity.pk.PeriodPK;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface PeriodRepository extends JpaRepository<Period, PeriodPK> {

    List<Period> findAllByIdIndexAndIdDayAndIdPeriodTypeIn(int index, DayOfWeek day, List<PeriodType> periodTypes);

    List<Period> findByGroupInAndIdDay(List<Group> groups, DayOfWeek day);
}
