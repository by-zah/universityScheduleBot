package ua.khnu.dto;

import org.springframework.stereotype.Component;
import ua.khnu.entity.ScheduleUnit;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ScheduleContainer {
    private final ReentrantLock lock;
    private List<ScheduleUnit> schedule;

    public ScheduleContainer() {
        lock = new ReentrantLock();
    }

    public List<ScheduleUnit> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<ScheduleUnit> schedule) {
        lock.lock();
        this.schedule = schedule;
        lock.unlock();
    }
}
