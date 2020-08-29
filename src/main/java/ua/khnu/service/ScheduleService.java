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

@Service
public class ScheduleService {
    private static final Type SCHEDULE_LIST_TYPE = TypeToken.getParameterized(List.class, ScheduleUnit.class).getType();
    private final ScheduleRepository scheduleRepository;
    private final Gson gson;
    private final ScheduleContainer scheduleContainer;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository, Gson gson, ScheduleContainer scheduleContainer) {
        this.scheduleRepository = scheduleRepository;
        this.gson = gson;
        this.scheduleContainer = scheduleContainer;
    }

    @PostConstruct
    private void setUpSchedule() {
        scheduleContainer.setSchedule(scheduleRepository.getAll());
    }

    public void updateScheduleFromJson(String json) {
        try {
            List<ScheduleUnit> schedule = gson.fromJson(json, SCHEDULE_LIST_TYPE);
            if (schedule == null || schedule.isEmpty()) {
                throw new BotException("Invalid schedule");
            }
            scheduleRepository.clear();
            scheduleRepository.createAll(schedule);
            scheduleContainer.setSchedule(schedule);
        } catch (IllegalStateException e) {
            throw new BotException("Invalid json");
        }
    }
}
