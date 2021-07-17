package ua.khnu.service;

import ua.khnu.dto.File;
import ua.khnu.entity.Period;
import ua.khnu.entity.pk.PeriodPK;

import java.time.DayOfWeek;
import java.util.List;

public interface PeriodService {

    List<Period> getUpcomingUserClasses(long userId);

    void addAll(File file, long userId);

    List<Period> getPeriodByDayAndIndex(int periodIndex, DayOfWeek dayOfWeek);

    void removeAllClassesInGroupsUserOwn(long userId);

    void updateClassRoom(PeriodPK id, String newRoom);

    List<Period> getAllPeriods();

    List<String> getAllClassesNames();
}
