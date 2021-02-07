package ua.khnu.entity;


import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ua.khnu.util.Constants.TIME_ZONE_ID;

@Entity
@Table(name = "schedule")
public class ScheduleUnit {

    private static final DateTimeFormatter SIMPLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Id
    @Column
    private int index;

    @Column(name = "start_hour")
    private int startHour;

    @Column(name = "start_min")
    private int startMin;

    @Column(name = "end_hour")
    private int endHour;

    @Column(name = "end_min")
    private int endMin;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "index", insertable = false, updatable = false)
    private List<Period> classes;

    public List<Period> getClasses() {
        return classes;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public LocalDateTime getStartLocalDateTime() {
        return getStartLocalDateTime(false);
    }

    public LocalDateTime getStartLocalDateTime(boolean nextDay) {
        LocalDateTime localDateTime = LocalDate.now(ZoneId.of(TIME_ZONE_ID)).atStartOfDay();
        localDateTime = localDateTime.plusHours(startHour);
        localDateTime = localDateTime.plusMinutes(startMin);
        localDateTime = nextDay ? localDateTime.plusDays(1) : localDateTime;
        return localDateTime;
    }

    private LocalDateTime getEndLocalDateTime() {
        LocalDateTime localDateTime = LocalDate.now(ZoneId.of(TIME_ZONE_ID)).atStartOfDay();
        localDateTime = localDateTime.plusHours(endHour);
        return localDateTime.plusMinutes(endMin);
    }

    @Override
    public String toString() {
        return SIMPLE_DATE_FORMATTER.format(getStartLocalDateTime()) + "-" + SIMPLE_DATE_FORMATTER.format(getEndLocalDateTime());
    }
}
