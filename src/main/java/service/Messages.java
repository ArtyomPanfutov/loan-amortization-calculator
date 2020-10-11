package service;

public enum Messages {
    NULL("Input data can't be null!"),
    NEGATIVE_NUMBER("Input can't be negative!"),
    EARLY_PAYMENT_NUMBER_IS_NEGATIVE("Early payment number can't be negative!"),
    EARLY_PAYMENT_AMOUNT_IS_NEGATIVE("Early payment amount can't be negative!"),
    EARLY_PAYMENT_STRATEGY_IS_NULL("Early payment strategy can't be null")
    ;
    private final String messageText;

    public String getMessageText() {
        return messageText;
    }

    Messages(String messageText) {
        this.messageText = messageText;
    }
}
