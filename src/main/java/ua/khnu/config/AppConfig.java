package ua.khnu.config;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.khnu.demon.ScheduleSendMessageDemon;
import ua.khnu.service.IsDayOffService;
import ua.khnu.service.MailingService;
import ua.khnu.service.ScheduleService;

@Configuration
public class AppConfig {

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    @Autowired
    public Thread scheduleSendMessageDemon(MailingService mailingService, ScheduleService scheduleService,
                                           IsDayOffService isDayOffService) {
        Thread thread = new Thread(
                new ScheduleSendMessageDemon(mailingService, scheduleService, isDayOffService));
        thread.setDaemon(true);
        thread.start();
        return thread;
    }


}
