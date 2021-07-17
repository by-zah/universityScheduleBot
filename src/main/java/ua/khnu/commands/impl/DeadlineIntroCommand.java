package ua.khnu.commands.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.SafelyIBotCommand;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class DeadlineIntroCommand implements SafelyIBotCommand {
    private static final Logger LOG = LogManager.getLogger(DeadlineIntroCommand.class);
    private static final String COMMAND_IDENTIFIER = "deadline";

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {
        LOG.info("Introduce deadline feature");
        var messageText = "Напоминания по дедлайнам по умолчанию включены и будут приходить за 30, 7, 5, 4, 3, 2, 1  дней до его срока, " +
                "в каждом напоминании будет кнопка \"Mark as done\", чтобы отметить дедлайн как сделанный и не получать больше уведомления о нем. " +
                "Если вы не хотите вовсе пользоваться  этой функцией команда /" + UserSettingsCommand.COMMAND_IDENTIFIER +
                " выключит все уведомления о дедлайнах.\nТак же каждый имеет возможность добавлять дедлайн по команде /" + AddDeadlineCommand.COMMAND_IDENTIFIER +
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
