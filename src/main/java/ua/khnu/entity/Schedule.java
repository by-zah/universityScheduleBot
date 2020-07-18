package ua.khnu.entity;

import java.util.List;

public class Schedule {
    private List<Day> days;

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "days=" + days +
                '}';
    }
}
