package ua.khnu.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public final class KeyboardBuilder {
    private KeyboardBuilder() {
    }

    public static InlineKeyboardMarkup buildInlineKeyboard(String commandIdentifier, List<String> args, List<String> buttonTexts, int buttonsInRow) {
        if (args.isEmpty()){
            return null;
        }
        if (buttonTexts == null || args.size() != buttonTexts.size()) {
            throw new IllegalArgumentException();
        }
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            var arg = args.get(i);
            var inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(buttonTexts.get(i));
            inlineKeyboardButton.setCallbackData(commandIdentifier + " " + arg);
            row.add(inlineKeyboardButton);
            if (row.size() >= buttonsInRow) {
                rowList.add(row);
                row = new ArrayList<>();
            }
        }
        rowList.add(row);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup buildInlineKeyboard(String commandIdentifier, List<String> args, int buttonsInRow) {
        return buildInlineKeyboard(commandIdentifier, args, List.copyOf(args), buttonsInRow);
    }
}
