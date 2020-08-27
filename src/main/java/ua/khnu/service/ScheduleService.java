package ua.khnu.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.dto.ScheduleContainer;
import ua.khnu.entity.ScheduleUnit;
import ua.khnu.exception.BotException;
import ua.khnu.repository.ScheduleRepository;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.List;

import static java.time.DayOfWeek.MONDAY;

@Service
public class ScheduleService {
    private final ScheduleRepository repository;
    private final Gson gson;
    private final ScheduleContainer scheduleContainer;
    private static final Type SCHEDULE_LIST_TYPE = TypeToken.getParameterized(List.class, ScheduleUnit.class).getType();

    @Autowired
    public ScheduleService(ScheduleRepository repository, Gson gson, ScheduleContainer scheduleContainer) {
        this.repository = repository;
        this.gson = gson;
        this.scheduleContainer = scheduleContainer;
    }

    @PostConstruct
    private void setUpSchedule(){
        scheduleContainer.setSchedule(repository.getAll());
    }

    public void updateScheduleFromJson(String json) {
        List<ScheduleUnit> schedule = gson.fromJson(json, SCHEDULE_LIST_TYPE);
        if (schedule == null || schedule.isEmpty()) {
            throw new BotException("Invalid schedule");
        }
        repository.clear();
        repository.createAll(schedule);
        scheduleContainer.setSchedule(schedule);
    }
}
