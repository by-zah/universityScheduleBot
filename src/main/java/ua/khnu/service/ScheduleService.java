package ua.khnu.service;

import ua.khnu.entity.ScheduleUnit;

import java.util.Optional;

public interface ScheduleService {

    void updateScheduleFromJson(String json, long userId);

    ScheduleUnit getFirstClassTime();

    Optional<ScheduleUnit> getNearest();

}
