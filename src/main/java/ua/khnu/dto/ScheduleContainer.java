package ua.khnu.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ua.khnu.entity.ScheduleUnit;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Getter
public class ScheduleContainer {
    private final List<ScheduleUnit> schedule;

    public ScheduleContainer() {
        schedule = new CopyOnWriteArrayList<>();
    }

    public void setSchedule(List<ScheduleUnit> schedule) {
        this.schedule.clear();
        this.schedule.addAll(schedule);
    }
}
