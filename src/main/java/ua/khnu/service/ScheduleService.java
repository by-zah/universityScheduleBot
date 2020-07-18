package ua.khnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.khnu.dto.LessonAndDateTime;
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

    public LessonAndDateTime getNearestLesson() {
        Schedule schedule = repository.getSchedule().orElseThrow(IllegalStateException::new);
        LocalDateTime now = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID));
        LocalDate nextLessonDay = now.toLocalDate();
        LessonAndDateTime nextLesson = null;
        while (nextLesson == null || nextLesson.getLesson() == null) {
            Optional<Day> workingDayOpt = getSpecifiedWorkingDay(nextLessonDay.getDayOfWeek(), schedule);
            while (!workingDayOpt.isPresent()) {
                nextLessonDay = nextLessonDay.plusDays(1);
                workingDayOpt = getSpecifiedWorkingDay(nextLessonDay.getDayOfWeek(), schedule);
            }
            Day workingDay = workingDayOpt.get();
            LocalDateTime nextLessonDateTime = nextLessonDay.atStartOfDay();
            nextLesson = getNextLesson(workingDay, nextLessonDateTime, now);
            nextLessonDay = nextLessonDay.plusDays(1);
        }
        return nextLesson;
    }

    private LessonAndDateTime getNextLesson(Day workingDay, LocalDateTime nextLessonDate, LocalDateTime now) {
        Lesson nextLesson = null;
        LocalDateTime nextLessonDateTime = null;
        for (Lesson lesson : workingDay.getLessons()) {
            nextLessonDateTime = nextLessonDate.plusHours(lesson.getStartHour());
            nextLessonDateTime = nextLessonDateTime.plusMinutes(lesson.getStartMin());
            if (nextLessonDateTime.compareTo(now) > 0) {
                nextLesson = lesson;
                break;
            }
        }
        return new LessonAndDateTime(nextLesson, nextLessonDateTime);
    }

    private Optional<Day> getSpecifiedWorkingDay(DayOfWeek dayOfWeek, Schedule schedule) {
        return schedule.getDays()
                .stream()
                .filter(day -> day.getDayOfWeek().equals(dayOfWeek))
                .findAny();
    }
}
