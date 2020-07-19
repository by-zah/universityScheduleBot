package ua.khnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.khnu.dto.DayAndIndex;
import ua.khnu.entity.Day;
import ua.khnu.entity.Lesson;
import ua.khnu.entity.Schedule;
import ua.khnu.repository.ScheduleRepository;
import ua.khnu.util.Converter;

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

        Optional<Lesson> lessonOpt = Optional.empty();
        Day day = null;
        while (!lessonOpt.isPresent()) {
            Optional<Day> dayOpt = getSpecifiedWorkingDay(date.getDayOfWeek(), schedule);
            while (!dayOpt.isPresent()) {
                dayOpt = getSpecifiedWorkingDay(date.getDayOfWeek(), schedule);
                date = date.plusDays(1);
            }
            day = dayOpt.get();
            day.getLessons().sort(Lesson::compareTo);
            lessonOpt = getNextLesson(day, date, LocalDateTime.now(ZoneId.of(TIME_ZONE_ID)));
            date = date.plusDays(1);
        }
        return new DayAndIndex(day, day.getLessons().indexOf(lessonOpt.get()));
    }

    private Optional<Lesson> getNextLesson(Day workingDay, LocalDateTime nextLessonDate, LocalDateTime now) {
        return workingDay.getLessons().stream()
                .filter(lesson -> Converter.lessonToStartDateTime(lesson, nextLessonDate.getDayOfWeek()).compareTo(now) > 0)
                .findFirst();
    }

    private Optional<Day> getSpecifiedWorkingDay(DayOfWeek dayOfWeek, Schedule schedule) {
        return schedule.getDays()
                .stream()
                .filter(day -> day.getDayOfWeek().equals(dayOfWeek))
                .findAny();
    }
}
