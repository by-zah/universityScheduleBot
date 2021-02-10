package ua.khnu.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public final class KeyboardBuilder {
    private KeyboardBuilder() {
    }

    public static InlineKeyboardMarkup buildInlineKeyboard(String commandIdentifier, List<String> args) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String arg : args) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(arg);
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
}
