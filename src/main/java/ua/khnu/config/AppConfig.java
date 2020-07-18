package ua.khnu.config;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import ua.khnu.Bot;
import ua.khnu.commands.NonCommandCommand;
import ua.khnu.commands.SetScheduleCommand;
import ua.khnu.commands.StartCommand;
import ua.khnu.commands.SubscribeCommand;
import ua.khnu.commands.UnSubscribeCommand;
import ua.khnu.demon.ScheduleSendMessageDemon;
import ua.khnu.service.ScheduleService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Configuration
public class AppConfig {
    @Bean
    public TelegramBotsApi botsApi() {
        return new TelegramBotsApi();
    }

    @Bean
    public List<Long> subscribers() {
        return new CopyOnWriteArrayList<>();
    }

    @Bean
    @Autowired
    public Bot bot(ApplicationContext context) {
        Bot bot = new Bot();
        bot.register(context.getBean(StartCommand.class));
        bot.register(context.getBean(SubscribeCommand.class));
        bot.register(context.getBean(UnSubscribeCommand.class));
        return bot;
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    @Autowired
    public Thread scheduleSendMessageDemon(List<Long> subscribers, Bot bot, ScheduleService scheduleService) {
        Thread thread = new Thread(new ScheduleSendMessageDemon(subscribers, bot, scheduleService));
        thread.setDaemon(true);
        return thread;
    }

    @Bean
    @Autowired
    public List<NonCommandCommand> nonCommandCommands(ApplicationContext context) {
        List<NonCommandCommand> commands = new ArrayList<>();
        commands.add(context.getBean(SetScheduleCommand.class));
        return commands;
    }
}
