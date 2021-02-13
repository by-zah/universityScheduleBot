package ua.khnu.commands.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.exception.BotException;

import java.util.List;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class NonCommandProcessor {
    private final List<CommandProcessor> processors;

    @Autowired
    public NonCommandProcessor(List<CommandProcessor> processors) {
        this.processors = processors;
    }

    public void process(Update update, AbsSender absSender) {
        final var chatId = update.getMessage().getChatId();
        processors.stream()
                .filter(p -> p.getCondition().test(update))
                .findAny().ifPresentOrElse(p -> {
                    try {
                        p.process(update, absSender);
                    } catch (BotException e) {
                        sendMessage(absSender, chatId, e.getMessage());
                    }
                },
                () -> sendMessage(absSender, chatId, "Unsupported command"));
    }
}
