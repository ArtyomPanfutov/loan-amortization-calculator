package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents strategies for repeating early payments
 */
public enum EarlyPaymentRepeatingStrategy {

    /**
     * Single payment, without repeating
     */
    @JsonProperty("single")
    SINGLE,

    /**
     * Repeats early payment in each payment in schedule to end of loan term
     */
    @JsonProperty("to_end")
    TO_END,

    /**
     * Repeats early payment between this date
     */
    @JsonProperty("from_date_to_date")
    FROM_DATE_TO_DATE;

}
