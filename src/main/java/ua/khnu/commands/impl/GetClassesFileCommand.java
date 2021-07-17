package ua.khnu.commands.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.SafelyIBotCommand;
import ua.khnu.entity.Period;
import ua.khnu.service.PeriodService;
import ua.khnu.util.csv.Csv;

import java.util.Comparator;

import static ua.khnu.util.FileUtil.generateInputFile;
import static ua.khnu.util.MessageSender.sendFile;


@Component
public class GetClassesFileCommand implements SafelyIBotCommand {
    private static final Logger LOG = LogManager.getLogger(GetClassesFileCommand.class);
    private final PeriodService periodService;
    private final Csv csv;

    @Autowired
    public GetClassesFileCommand(PeriodService periodService, Csv csv) {
        this.periodService = periodService;
        this.csv = csv;
    }

    @Override
    public String getCommandIdentifier() {
        return "getClassesFile";
    }

    @Override
    public String getDescription() {
        return null;
    }


    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {
        var allPeriods = periodService.getAllPeriods();

        allPeriods.sort(Comparator.comparingInt(Period::getIndex));
        allPeriods.sort(Comparator.comparing(Period::getDay));
        var body = csv.createCsvFromObject(allPeriods, Period.class);
        var inputFile = generateInputFile(body, "Classes.csv");
        sendFile(absSender, inputFile, message.getChatId(), "Here is all classes csv");
    }
}
