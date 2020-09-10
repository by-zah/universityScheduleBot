package ua.khnu.demon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.khnu.entity.Period;
import ua.khnu.service.SendMessageService;

import java.util.List;


public class ScheduleSendMessageDemon implements Runnable {
    private static final Logger LOG = LogManager.getLogger(ScheduleSendMessageDemon.class);
    private final SendMessageService sendMessageService;

    public ScheduleSendMessageDemon(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!sendMessageService.isReady()) {
                    Thread.sleep(Long.MAX_VALUE);
                    continue;
                }
                List<Period> currentClasses = sendMessageService.getCurrentClasses();
                sendMessageService.sendNotifications(currentClasses);
                LOG.info("send notification to all students");
                Thread.sleep(90000);
            } catch (InterruptedException e) {
                LOG.info("new schedule set");
            }
        }
    }


}
