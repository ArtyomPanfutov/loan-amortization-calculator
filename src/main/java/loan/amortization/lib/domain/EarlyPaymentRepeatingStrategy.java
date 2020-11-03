package loan.amortization.lib.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents strategies for repeating early payments
 *
 * @author Artyom Panfutov
 */
public enum EarlyPaymentRepeatingStrategy {

    /**
     * Single payment, without repeating
     */
    @JsonProperty("single")
    SINGLE,

    /**
     * Repeats early payment for each payment in the payment schedule to the end of the loan term
     */
    @JsonProperty("to_end")
    TO_END,

    /**
     * Repeats early payment between this date
     */
    @JsonProperty("to_certain_month")
    TO_CERTAIN_MONTH;
}
