package ua.khnu.util;

import ua.khnu.exception.BotException;

public final class MessageParser {
    private MessageParser() {
    }

    public static String getArgumentByPositionAndSeparator(int position, String separator, String message) {
        String[] args = message.split(separator);
        if (args.length < position + 1) {
            throw new BotException("This command should have at least " + position + " argument(s)");
        }
        return args[position];
    }
}
