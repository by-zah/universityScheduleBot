package ua.khnu.repository;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.khnu.entity.Schedule;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ScheduleRepository {
    private final Gson gson;
    private final ReentrantLock lock;
    private Schedule schedule;

    @Autowired
    public ScheduleRepository(Gson gson) {
        this.gson = gson;
        lock = new ReentrantLock();
    }

    public Optional<Schedule> getSchedule() {
        return Optional.ofNullable(schedule);
    }

    public void setScheduleFromJSON(String json) {
        lock.lock();
        this.schedule = gson.fromJson(json, Schedule.class);
        lock.unlock();
    }

    public boolean isSchedulePresent() {
        return schedule != null;
    }
}
