package ua.khnu.config;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import ua.khnu.Bot;
import ua.khnu.commands.*;
import ua.khnu.demon.ScheduleSendMessageDemon;
import ua.khnu.service.SendMessageService;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public TelegramBotsApi botsApi() {
        return new TelegramBotsApi();
    }

    @Bean
    @Autowired
    public Bot bot(ApplicationContext context) {
        Bot bot = new Bot();
        bot.register(context.getBean(StartCommand.class));
        bot.register(context.getBean(SubscribeCommand.class));
        bot.register(context.getBean(UnSubscribeCommand.class));
        bot.register(context.getBean(CreateNewGroupCommand.class));
        bot.register(context.getBean(AddClassesCommand.class));
        return bot;
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    @Autowired
    public Thread scheduleSendMessageDemon(ApplicationContext context) {
        SendMessageService sendMessageService = context.getBean(SendMessageService.class);
        Thread thread = new Thread(
                new ScheduleSendMessageDemon(sendMessageService));
        thread.setDaemon(true);
        return thread;
    }

    @Bean
    @Autowired
    public List<IBotCommand> nonCommandCommands(ApplicationContext context) {
        List<IBotCommand> commands = new ArrayList<>();
        commands.add(context.getBean(UpdateScheduleCommand.class));
        return commands;
    }
}
