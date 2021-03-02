package ua.khnu.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ua.khnu.entity.Deadline;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class DeadlineNotificationDto {
    private final Deadline deadline;
    private final long millis;

    public int compareTo(DeadlineNotificationDto o, LocalDateTime now) {
        return Long.compare(getMillisToNotification(now), o.getMillisToNotification(now));
    }

    public boolean isInFuture(LocalDateTime now) {
        return getMillisToNotification(now) > 0;
    }

    public long getMillisToNotification(LocalDateTime now) {
        return ChronoUnit.MILLIS.between(now, deadline.getDeadLineTime()) - millis;
    }
}
