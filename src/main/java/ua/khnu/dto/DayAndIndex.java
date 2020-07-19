package ua.khnu.dto;

import ua.khnu.entity.Day;

public class DayAndIndex {
    private final Day day;
    private final int index;

    public DayAndIndex(Day day, int index) {
        this.day = day;
        this.index = index;
    }

    public Day getDay() {
        return day;
    }

    public int getIndex() {
        return index;
    }
}
