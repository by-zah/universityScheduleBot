package ua.khnu.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ua.khnu.entity.Deadline;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static ua.khnu.util.Constants.TIME_ZONE_ID;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DeadlineNotificationDto implements Comparable<DeadlineNotificationDto> {
    private final Deadline deadline;
    private final long millis;

    @Override
    public int compareTo(DeadlineNotificationDto o) {
        return Long.compare(getMillisToNotification(), o.getMillisToNotification());
    }

    public boolean isInFuture() {
        return getMillisToNotification() > 0;
    }

    private long getMillisToNotification() {
        var now = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID));
        return ChronoUnit.MILLIS.between(now, deadline.getDeadLineTime()) - millis;
    }
}
