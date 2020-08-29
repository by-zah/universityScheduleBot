package ua.khnu.dto;

import org.springframework.stereotype.Component;
import ua.khnu.entity.ScheduleUnit;

import java.util.List;

@Component
public class ScheduleContainer {
    private List<ScheduleUnit> schedule;


    public List<ScheduleUnit> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<ScheduleUnit> schedule) {
        this.schedule = schedule;
    }
}
