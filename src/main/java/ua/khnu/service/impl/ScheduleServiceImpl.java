package ua.khnu.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.dto.ScheduleContainer;
import ua.khnu.entity.ScheduleUnit;
import ua.khnu.exception.BotException;
import ua.khnu.repository.ScheduleRepository;
import ua.khnu.repository.UserRepository;
import ua.khnu.service.ScheduleService;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static ua.khnu.util.Constants.TEN_MINUTES_IN_MILLIS;
import static ua.khnu.util.Constants.TIME_ZONE_ID;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private static final Type SCHEDULE_LIST_TYPE = TypeToken.getParameterized(List.class, ScheduleUnit.class).getType();
    private final ScheduleRepository scheduleRepository;
    private final Gson gson;
    private final ScheduleContainer scheduleContainer;
    private final UserRepository userRepository;

    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, Gson gson, ScheduleContainer scheduleContainer,
                               UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.gson = gson;
        this.scheduleContainer = scheduleContainer;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void setUpSchedule() {
        scheduleContainer.setSchedule(scheduleRepository.findAll());
    }

    @Override
    @Transactional
    public void updateScheduleFromJson(String json, int userId) {
        var user = userRepository.findById(userId);
        if (user.isPresent() && !user.get().isSupper()) {
            throw new BotException("You have not permission to this operation");
        }
        try {
            List<ScheduleUnit> schedule = gson.fromJson(json, SCHEDULE_LIST_TYPE);
            if (schedule == null || schedule.isEmpty()) {
                throw new BotException("Empty schedule");
            }
            scheduleRepository.deleteAll();
            scheduleRepository.saveAll(schedule);
            scheduleContainer.setSchedule(schedule);
        } catch (IllegalStateException e) {
            throw new BotException("Invalid json");
        }
    }

    @Override
    public ScheduleUnit getFirstClassTime() {
        return scheduleContainer.getSchedule().stream()
                .filter(s -> s.getIndex() == 1)
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public Optional<ScheduleUnit> getNearest() {
        var now = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID));
        ScheduleUnit nearest = null;
        long min = Long.MAX_VALUE;
        List<ScheduleUnit> schedule = scheduleContainer.getSchedule();
        for (ScheduleUnit scheduleUnit : schedule) {
            long between = ChronoUnit.MILLIS.between(now, scheduleUnit.getStartLocalDateTime());
            if (between <= TEN_MINUTES_IN_MILLIS) {
                continue;
            }
            if (between < min) {
                min = between;
                nearest = scheduleUnit;
            }
        }
        return Optional.ofNullable(nearest);
    }
}
