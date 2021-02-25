package ua.khnu.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public final class KeyboardBuilder {
    private KeyboardBuilder() {
    }

    public static InlineKeyboardMarkup buildInlineKeyboard(String commandIdentifier, List<String> args, List<String> buttonTexts) {
        if (buttonTexts == null || args.size() != buttonTexts.size()) {
            buttonTexts = args;
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            var arg = args.get(i);
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(buttonTexts.get(i));
            inlineKeyboardButton.setCallbackData(commandIdentifier + " " + arg);
            row.add(inlineKeyboardButton);
            if (row.size() > 2) {
                rowList.add(row);
                row = new ArrayList<>();
            }
        }
        rowList.add(row);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup buildInlineKeyboard(String commandIdentifier, List<String> args) {
        return buildInlineKeyboard(commandIdentifier, args, null);
    }
}
