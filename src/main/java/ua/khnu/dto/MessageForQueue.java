package ua.khnu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MessageForQueue {
    private final String message;
    private final long chatId;
}
