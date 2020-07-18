package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.Bot;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Component
public class NonCommandProcessor extends SimpleAnswerCommand {
    private final List<NonCommandCommand> commands;
    private final Bot bot;

    @Autowired
    public NonCommandProcessor(Bot bot, List<NonCommandCommand> commands) {
        this.commands = commands;
        this.bot = bot;
    }

    @PostConstruct
    public void setProcessorToBot() {
        bot.setNonCommandProcessor(this);
    }

    @Override
    public String getCommandIdentifier() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Optional<NonCommandCommand> command = commands.stream()
                .filter(c -> c.getCondition().test(message))
                .findAny();
        if (command.isPresent()) {
            command.get().processMessage(absSender, message, arguments);
        } else {
            sendMessage(absSender, "Unsupported operation", message.getChatId());
        }
    }
}
