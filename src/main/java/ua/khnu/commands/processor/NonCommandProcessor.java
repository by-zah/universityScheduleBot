package ua.khnu.commands.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.exception.BotException;

import java.util.List;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class NonCommandProcessor {
    private static final Logger LOG = LogManager.getLogger(NonCommandProcessor.class);
    private final List<CommandProcessor> processors;

    @Autowired
    public NonCommandProcessor(List<CommandProcessor> processors) {
        this.processors = processors;
    }

    public void process(Update update, AbsSender absSender) {
        final var chatId = getChatId(update);
        processors.stream()
                .filter(p -> p.getCondition().test(update))
                .findAny().ifPresentOrElse(p -> {
                    try {
                        p.process(update, absSender);
                    } catch (BotException e) {
                        LOG.error(e);
                        sendMessage(absSender, chatId, e.getMessage());
                    } catch (Exception e) {
                        LOG.error(e);
                        sendMessage(absSender, chatId, "Problem happens while command processing");
                    }
                },
                () -> sendMessage(absSender, chatId, "Unsupported command"));
    }

    private Long getChatId(Update update) {
        //TODO find more common solution
        var message = update.hasMessage() ? update.getMessage() : update.getCallbackQuery().getMessage();
        return message.getChatId();
    }
}
