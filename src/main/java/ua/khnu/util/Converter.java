package ua.khnu.util;

import ua.khnu.entity.Lesson;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Converter {
    private Converter() {
        throw new UnsupportedOperationException();
    }

    public static LocalDateTime lessonToStartDateTime(Lesson lesson, DayOfWeek dayOfWeek) {
        LocalDateTime dateTime = LocalDate.now().atStartOfDay();
        while (!dateTime.getDayOfWeek().equals(dayOfWeek)) {
            dateTime = dateTime.plusDays(1);
        }
        dateTime = dateTime.plusHours(lesson.getStartHour());
        dateTime = dateTime.plusMinutes(lesson.getStartMin());
        return dateTime;
    }
}
