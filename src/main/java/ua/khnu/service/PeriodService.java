package ua.khnu.service;

import ua.khnu.dto.File;
import ua.khnu.entity.Period;

import java.time.DayOfWeek;
import java.util.List;

public interface PeriodService {

    List<Period> getUpcomingUserClasses(int userId);

    void addAll(File file, int userId);

    List<Period> getPeriodByDayAndIndex(int periodIndex, DayOfWeek dayOfWeek);

    void removeAllClassesInGroupsUserOwn(int userId);

}
