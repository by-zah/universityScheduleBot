package ua.khnu.service;

import ua.khnu.demon.DeadlineSendMessageDemon;
import ua.khnu.dto.DeadlineNotificationDto;
import ua.khnu.entity.Deadline;
import ua.khnu.entity.pk.UserDeadlinePK;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeadlineService {

    void setDeadlineSendMessageDemon(DeadlineSendMessageDemon deadlineSendMessageDemon);

    void createDeadline(String groupName, String className, LocalDateTime localDateTime, String description);

    void createDeadline(Deadline deadline);

    String getAllDeadlinesCsv();

    Deadline getNearestDeadline();

    List<DeadlineNotificationDto> getNextDeadlineToNotification();

    void markUserDeadlineAsDone(UserDeadlinePK userDeadlineId);
}
