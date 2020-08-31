package ua.khnu.config;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.khnu.Bot;
import ua.khnu.commands.*;
import ua.khnu.commands.processor.CallBackCommandProcessor;
import ua.khnu.commands.processor.FileCommandProcessor;
import ua.khnu.commands.processor.NonCommandProcessor;
import ua.khnu.demon.ScheduleSendMessageDemon;
import ua.khnu.service.SendMessageService;

@Configuration
public class AppConfig {

    @Bean
    @Autowired
    public Bot bot(ApplicationContext context) {
        Bot bot = new Bot();
        bot.register(context.getBean(StartCommand.class));
        bot.register(context.getBean(SubscribeCommand.class));
        bot.register(context.getBean(UnSubscribeCommand.class));
        bot.register(context.getBean(CreateNewGroupCommand.class));
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
        thread.start();
        return thread;
    }

    @Bean
    @Autowired
    public FileCommandProcessor fileCommandProcessor(ApplicationContext context) {
        FileCommandProcessor fileCommandProcessor = new FileCommandProcessor();
        fileCommandProcessor.registerCommand(context.getBean(UpdateScheduleCommand.class));
        fileCommandProcessor.registerCommand(context.getBean(AddClassesCommand.class));
        return fileCommandProcessor;
    }

    @Bean
    @Autowired
    public CallBackCommandProcessor callBackCommandProcessor(ApplicationContext context) {
        CallBackCommandProcessor callBackCommandProcessor = new CallBackCommandProcessor();
        callBackCommandProcessor.registerCommand(context.getBean(SubscribeCommand.class));
        callBackCommandProcessor.registerCommand(context.getBean(UnSubscribeCommand.class));
        return callBackCommandProcessor;
    }

    @Bean
    @Autowired
    public NonCommandProcessor nonCommandProcessor(FileCommandProcessor fileCommandProcessor
            , CallBackCommandProcessor callBackCommandProcessor,Bot bot) {
        NonCommandProcessor nonCommandProcessor = new NonCommandProcessor(bot);
        nonCommandProcessor.registerProcessor(fileCommandProcessor);
        nonCommandProcessor.registerProcessor(callBackCommandProcessor);
        return nonCommandProcessor;
    }
}
