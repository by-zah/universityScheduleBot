package ua.khnu.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.AbstractCommand;
import ua.khnu.commands.SafelyIBotCommand;
import ua.khnu.entity.PeriodType;
import ua.khnu.entity.pk.PeriodPK;
import ua.khnu.service.PeriodService;

import java.time.DayOfWeek;

@Component
public class UpdateRoomCommand extends AbstractCommand implements SafelyIBotCommand {
    private final PeriodService periodService;

    @Autowired
    public UpdateRoomCommand(PeriodService periodService) {
        this.periodService = periodService;
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {
        var chatId = message.getChatId();
        if (strings.length < 5) {
            sendMessage(absSender, chatId, "Command should have 3 arguments: class index, groupName, new room");
            return;
        }
        try {
            var periodIndex = Integer.parseInt(strings[0]);
            var groupName = strings[1];
            var periodType = PeriodType.valueOf(strings[2]);
            var day = DayOfWeek.valueOf(strings[3]);
            var room = strings[4];
            var periodId = new PeriodPK();
            periodId.setDay(day);
            periodId.setGroupName(groupName);
            periodId.setIndex(periodIndex);
            periodId.setPeriodType(periodType);
            periodService.updateClassRoom(periodId, room);
        } catch (NumberFormatException e) {
            sendMessage(absSender, chatId, "1 argument \"class index\" should be a number");
        } catch (IllegalArgumentException e) {
            sendMessage(absSender, chatId, "Error while arguments parsing, double check them and try again");
        }

    }

    @Override
    public String getCommandIdentifier() {
        return "updateRoom";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
