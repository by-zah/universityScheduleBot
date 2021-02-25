package ua.khnu.service;

import ua.khnu.demon.DeadlineSendMessageDemon;
import ua.khnu.dto.DeadlineNotificationDto;
import ua.khnu.entity.Deadline;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DeadlineService {

    void setDeadlineSendMessageDemon(DeadlineSendMessageDemon deadlineSendMessageDemon);

    void createDeadline(String groupName, String className, LocalDateTime localDateTime, String description);

    String getAllDeadlinesCsv();

    Deadline getNearestDeadline();

    Optional<DeadlineNotificationDto> getNextDeadlineToNotification();
}
