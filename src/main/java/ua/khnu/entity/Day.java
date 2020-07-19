package ua.khnu.entity;

import java.time.DayOfWeek;
import java.util.List;

public class Day {
    private DayOfWeek dayOfWeek;
    private List<Lesson> lessons;

    public Day() {
    }

    public Day(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Override
    public String toString() {
        return "Day{" +
                "dayOfWeek=" + dayOfWeek +
                ", lessons=" + lessons +
                '}';
    }
}
