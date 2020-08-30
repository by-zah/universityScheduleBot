package ua.khnu.commands.processor;

import org.telegram.telegrambots.meta.api.objects.Update;
import ua.khnu.Bot;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ua.khnu.util.MessageSender.sendMessage;

public class NonCommandProcessor {
    private final List<CommandProcessor<?>> processors;
    private final Bot bot;

    public NonCommandProcessor(Bot bot) {
        processors = new ArrayList<>();
        this.bot = bot;
    }

    @PostConstruct
    private void setToBot() {
        bot.setNonCommandProcessor(this);
    }

    public void registerProcessor(CommandProcessor<?> processor) {
        processors.add(processor);
    }


    public void process(Update update) {
        Optional<CommandProcessor<?>> processor = processors.stream()
                .filter(p -> p.getCondition().test(update))
                .findAny();
        if (processor.isPresent()) {
            processor.get().process(update, bot);
        } else {
            sendMessage(bot, "Unsupported command", update.getMessage().getChatId());
        }
    }
}
