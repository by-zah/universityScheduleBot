package ua.khnu.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.khnu.dto.ScheduleContainer;
import ua.khnu.entity.ScheduleUnit;
import ua.khnu.entity.User;
import ua.khnu.exception.BotException;
import ua.khnu.repository.ScheduleRepository;
import ua.khnu.repository.UserRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {
    private static final int USER_ID = 111;
    private static UserRepository userRepository;

    @BeforeAll
    static void setUp() {
        userRepository = mock(UserRepository.class);
        User user = new User();
        user.setSupper(true);
        when(userRepository.getById(USER_ID)).thenReturn(Optional.of(user));
    }

    @Test
    void shouldThrowExceptionIfJsonIsInvalid() {
        Gson gson = mock(Gson.class);
        when(gson.fromJson(any(String.class), any(Type.class))).thenThrow(IllegalStateException.class);
        ScheduleService scheduleService =
                new ScheduleService(null, gson, null, userRepository);

        Assertions.assertThrows(BotException.class, () -> scheduleService.updateScheduleFromJson("", USER_ID));
    }

    @Test
    void shouldThrowExceptionIfScheduleIsEmpty() {
        Gson gson = mock(Gson.class);
        when(gson.fromJson(any(String.class), any(Type.class))).thenReturn(new ArrayList<>());
        ScheduleService scheduleService = new ScheduleService(null, gson, null, userRepository);

        Assertions.assertThrows(BotException.class, () -> scheduleService.updateScheduleFromJson("", USER_ID));
    }

    @Test
    void shouldUpdateSchedule() {
        Gson gson = mock(Gson.class);
        List<ScheduleUnit> schedule = new ArrayList<>();
        schedule.add(new ScheduleUnit());
        when(gson.fromJson(any(String.class), any(Type.class))).thenReturn(schedule);
        ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);
        ScheduleContainer scheduleContainer = mock(ScheduleContainer.class);
        ScheduleService scheduleService = new ScheduleService(scheduleRepository, gson, scheduleContainer, userRepository);

        scheduleService.updateScheduleFromJson("", USER_ID);

        verify(scheduleRepository).clear();
        verify(scheduleRepository).createAll(schedule);
        verify(scheduleContainer).setSchedule(schedule);
    }

    @Test
    void shouldThrowExceptionIfUserIsNotSuper() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getById(USER_ID)).thenReturn(Optional.of(new User()));
        ScheduleService scheduleService = new ScheduleService(null, null, null, userRepository);

        Assertions.assertThrows(BotException.class, () -> scheduleService.updateScheduleFromJson("", USER_ID));
    }
}
