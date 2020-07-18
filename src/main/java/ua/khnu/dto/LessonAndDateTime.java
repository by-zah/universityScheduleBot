package ua.khnu.dto;

import ua.khnu.entity.Lesson;

import java.time.LocalDateTime;

public class LessonAndDateTime {
    private final Lesson lesson;
    private final LocalDateTime dateTime;

    public LessonAndDateTime(Lesson lesson, LocalDateTime dateTime) {
        this.lesson = lesson;
        this.dateTime = dateTime;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
