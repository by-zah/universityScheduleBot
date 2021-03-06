package ua.khnu.commands.processor;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.function.Predicate;

public interface CommandProcessor {
    Predicate<Update> getCondition();

    void process(Update update, AbsSender absSender);

}
