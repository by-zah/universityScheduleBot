package ua.khnu.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.khnu.dto.ScheduleContainer;
import ua.khnu.entity.ScheduleUnit;
import ua.khnu.exception.BotException;
import ua.khnu.repository.ScheduleRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ScheduleServiceTest {


    @Test
    void shouldThrowExceptionIfJsonIsInvalid() {
        Gson gson = mock(Gson.class);
        when(gson.fromJson(any(String.class), any(Type.class))).thenThrow(IllegalStateException.class);
        ScheduleService scheduleService = new ScheduleService(null,gson,null);

        Assertions.assertThrows(BotException.class, () -> scheduleService.updateScheduleFromJson(""));
    }

    @Test
    void shouldThrowExceptionIfScheduleIsEmpty(){
        Gson gson = mock(Gson.class);
        when(gson.fromJson(any(String.class), any(Type.class))).thenReturn(new ArrayList<>());
        ScheduleService scheduleService = new ScheduleService(null,gson,null);

        Assertions.assertThrows(BotException.class,()->scheduleService.updateScheduleFromJson(""));
    }

    @Test
    void shouldUpdateSchedule(){
        Gson gson = mock(Gson.class);
        List<ScheduleUnit> schedule = new ArrayList<>();
        schedule.add(new ScheduleUnit());
        when(gson.fromJson(any(String.class), any(Type.class))).thenReturn(schedule);
        ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);
        ScheduleContainer scheduleContainer = mock(ScheduleContainer.class);
        ScheduleService scheduleService = new ScheduleService(scheduleRepository,gson,scheduleContainer);

        scheduleService.updateScheduleFromJson("");

        verify(scheduleRepository).clear();
        verify(scheduleRepository).createAll(schedule);
        verify(scheduleContainer).setSchedule(schedule);
    }
}
