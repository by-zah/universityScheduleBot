package ua.khnu.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ua.khnu.util.Constants.TIME_ZONE_ID;

@Entity
@Table(name = "schedule")
@Getter
@Setter
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

    public LocalDateTime getStartLocalDateTime(LocalDateTime localDateTime){
        localDateTime = localDateTime.toLocalDate().atStartOfDay();
        localDateTime = localDateTime.plusHours(startHour);
        return localDateTime.plusMinutes(startMin);
    }

    public LocalDateTime getStartLocalDateTime() {
        return getStartLocalDateTime(false);
    }

    public LocalDateTime getStartLocalDateTime(boolean nextDay) {
        var localDateTime = LocalDate.now(ZoneId.of(TIME_ZONE_ID)).atStartOfDay();
        return  nextDay ? getStartLocalDateTime(localDateTime.plusDays(1)) : getStartLocalDateTime(localDateTime);
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
