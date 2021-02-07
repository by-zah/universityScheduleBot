package ua.khnu.dto;

public class MessageForQueue {
    private final String message;
    private final long chatId;

    public MessageForQueue(String message, long chatId) {
        this.message = message;
        this.chatId = chatId;
    }

    public long getChatId() {
        return chatId;
    }

    public String getMessage() {
        return message;
    }
}
