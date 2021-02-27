package ua.khnu.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.service.PeriodService;
import ua.khnu.service.impl.DeadlineServiceImpl;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class DeadlineIntroCommand implements SafelyIBotCommand {
    private static final Logger LOG = LogManager.getLogger(DeadlineIntroCommand.class);
    private static final String COMMAND_IDENTIFIER = "deadline";

    private final PeriodService periodService;

    @Autowired
    public DeadlineIntroCommand(PeriodService periodService) {
        this.periodService = periodService;
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {
        LOG.info("Introduce deadline feature");
        var classNames = String.join("\n\n", periodService.getAllClassesNames());
        var messageText = "Напоминания по дедлайнам по умолчанию включены и будут приходить за 30, 7, 5, 4, 3, 2, 1  дней до его срока, " +
                "в каждом напоминании будет кнопка \"Mark as done\", чтобы отметить дедлайн как сделанный и не получать больше уведомления о нем. " +
                "Если вы не хотите вовсе пользоваться  этой функцией команда /" + SwitchDeadlineNotificationsSettingCommand.COMMAND_IDENTIFIER +
                " выключит все уведомления о дедлайнах.\nТак же каждый имеет возможность добавлять дедлайн по команде /" + AddDeadlineCommand.COMMAND_IDENTIFIER +
                " соблюдая определенный формат : /"+AddDeadlineCommand.COMMAND_IDENTIFIER+" {groupName};{className};{yyyy-MM-dd HH:mm};{description} " +
                "\n Вот реальный пример:\n\n /addDeadline KS41;Технологии распределенных систем и параллельного вычисления;2021-02-27 00:00;Лаба 2\n\n" +
                "Вот список валидных на данный названий пар:\n"+classNames+
                "\n\n Посмотреть уже добавленные дедлайны можно по команде /"+GetDeadlinesFileCommand.COMMAND_IDENTIFIER;
        sendMessage(absSender, message.getChatId(), messageText);
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return null;
    }

}
