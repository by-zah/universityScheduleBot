package ua.khnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.khnu.dto.DayAndIndex;
import ua.khnu.entity.Day;
import ua.khnu.entity.Lesson;
import ua.khnu.entity.Schedule;
import ua.khnu.repository.ScheduleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static ua.khnu.BotInitializer.TIME_ZONE_ID;

@Component
public class ScheduleService {
    private final ScheduleRepository repository;

    @Autowired
    public ScheduleService(ScheduleRepository repository) {
        this.repository = repository;
    }

    public boolean isSchedulePresent() {
        return repository.isSchedulePresent();
    }

    public DayAndIndex getCurrentDayWithIndex() {
        Schedule schedule = repository.getSchedule().orElseThrow(IllegalStateException::new);
        LocalDateTime date = LocalDate.now(ZoneId.of(TIME_ZONE_ID)).atStartOfDay();
        Optional<Day> dayOpt;
        do {
            dayOpt = getSpecifiedWorkingDay(date.getDayOfWeek(), schedule);
            date = date.plusDays(1);
        } while(!dayOpt.isPresent());
        Day day = dayOpt.get();
        Lesson lesson = getNextLesson(day,date,LocalDateTime.now(ZoneId.of(TIME_ZONE_ID)));
        return new DayAndIndex(day,day.getLessons().indexOf(lesson));
    }

    private Lesson getNextLesson(Day workingDay, LocalDateTime nextLessonDate, LocalDateTime now) {
        Lesson nextLesson = null;
        LocalDateTime nextLessonDateTime;
        for (Lesson lesson : workingDay.getLessons()) {
            nextLessonDateTime = nextLessonDate.plusHours(lesson.getStartHour());
            nextLessonDateTime = nextLessonDateTime.plusMinutes(lesson.getStartMin());
            if (nextLessonDateTime.compareTo(now) > 0) {
                nextLesson = lesson;
                break;
            }
        }
        return nextLesson;
    }

    private Optional<Day> getSpecifiedWorkingDay(DayOfWeek dayOfWeek, Schedule schedule) {
        return schedule.getDays()
                .stream()
                .filter(day -> day.getDayOfWeek().equals(dayOfWeek))
                .findAny();
    }
}
