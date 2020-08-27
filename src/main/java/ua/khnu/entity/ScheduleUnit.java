package ua.khnu.entity;


import ua.khnu.BotInitializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "schedule")
public class ScheduleUnit {
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

    public LocalDateTime getStartLocalDateTime(){
        LocalDateTime localDateTime = LocalDate.now(ZoneId.of(BotInitializer.TIME_ZONE_ID)).atStartOfDay();
        localDateTime = localDateTime.plusHours(startHour);
        localDateTime = localDateTime.plusMinutes(startMin);
        return localDateTime;
    }
}
