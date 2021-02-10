package ua.khnu.commands.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

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
        processors.stream()
                .filter(p -> p.getCondition().test(update))
                .findAny().ifPresentOrElse(p -> p.process(update, absSender),
                () -> sendMessage(absSender, update.getMessage().getChatId(), "Unsupported command"));
    }
}
