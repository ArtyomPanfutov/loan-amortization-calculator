package service;

public enum Messages {
    NULL("Input data can't be null!")
    ;
    private final String messageText;

    public String getMessageText() {
        return messageText;
    }

    Messages(String messageText) {
        this.messageText = messageText;
    }
}
