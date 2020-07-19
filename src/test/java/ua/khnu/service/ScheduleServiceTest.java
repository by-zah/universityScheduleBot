package ua.khnu.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.khnu.dto.DayAndIndex;
import ua.khnu.entity.Day;
import ua.khnu.entity.Lesson;
import ua.khnu.entity.Schedule;
import ua.khnu.repository.ScheduleRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScheduleServiceTest {
    private ScheduleService service;
    private List<Lesson> lessons;
    private LocalDateTime now;

    @BeforeEach
    public void setUp() {
        ScheduleRepository repository = mock(ScheduleRepository.class);
        Schedule schedule = mock(Schedule.class);
        List<Day> days = new ArrayList<>();
        now = LocalDateTime.now();
        Day day = new Day(now.getDayOfWeek());
        lessons = new ArrayList<>();
        day.setLessons(lessons);
        days.add(day);
        when(schedule.getDays()).thenReturn(days);
        when(repository.getSchedule()).thenReturn(Optional.of(schedule));
        service = new ScheduleService(repository);
    }

    @Test
    public void shouldReturnNextLesson() {
        Lesson expected = new Lesson();
        expected.setStartMin(now.getMinute() + 10);
        expected.setStartHour(now.getHour());
        lessons.add(expected);

        DayAndIndex di = service.getCurrentDayWithIndex();
        Lesson actual = di.getDay().getLessons().get(di.getIndex());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnNextNextLesson() {
        Lesson lesson = new Lesson();
        lesson.setStartMin(now.getMinute() + 1);
        lesson.setStartHour(now.getHour());

        Lesson expected = new Lesson();
        expected.setStartMin(now.getMinute() + 3);
        expected.setStartHour(now.getHour());

        lessons.add(lesson);
        lessons.add(expected);


        DayAndIndex di = service.getCurrentDayWithIndex();
        Lesson actual = di.getDay().getLessons().get(di.getIndex() + 1);


        assertEquals(expected, actual);
    }

}
