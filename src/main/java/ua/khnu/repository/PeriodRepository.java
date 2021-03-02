package ua.khnu.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    List<Period> findByGroupInAndIdDayAndIdPeriodTypeIn(List<Group> groups, DayOfWeek day, List<PeriodType> periodTypes);

    @Query("UPDATE Period p SET p.roomNumber=:newRoom WHERE p.group.name=:groupName and p.scheduleUnit.index=:periodIndex")
    void updateByGroupNameAndPeriodIndex(String groupName, int periodIndex, String newRoom);

    boolean existsByName(String periodName);

    @Query("SELECT DISTINCT p.name  FROM Period p ORDER BY p.name")
    List<String> findAllDistinctByName();
}
