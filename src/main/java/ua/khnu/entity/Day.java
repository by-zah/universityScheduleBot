package ua.khnu.entity;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class Day {
    private DayOfWeek dayOfWeek;
    private List<Lesson> lessons;

    public Day() {
    }

    public Day(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        lessons = new ArrayList<>();
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }
}
