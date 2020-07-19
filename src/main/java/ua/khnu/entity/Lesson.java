package ua.khnu.entity;

import java.util.Objects;

public class Lesson implements Comparable<Lesson> {
    private String name;
    private int startHour;
    private int endHour;
    private int startMin;
    private int endMin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "name='" + name + '\'' +
                ", startHour=" + startHour +
                ", endHour=" + endHour +
                ", startMin=" + startMin +
                ", endMin=" + endMin +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return startHour == lesson.startHour &&
                endHour == lesson.endHour &&
                startMin == lesson.startMin &&
                endMin == lesson.endMin &&
                Objects.equals(name, lesson.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startHour, endHour, startMin, endMin);
    }

    @Override
    public int compareTo(Lesson o) {
        int i = startHour - o.startHour;
        return i == 0?i:startMin - o.startMin;
    }
}
