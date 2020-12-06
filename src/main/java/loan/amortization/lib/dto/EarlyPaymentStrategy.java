package loan.amortization.lib.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents strategies that can be applied to loan additional payments
 *
 * @author Artyom Panfutov
 */
public enum EarlyPaymentStrategy {
    /**
     * Early payment decreases the loan term
     */
    @JsonProperty("decrease_term")
    DECREASE_TERM,

    /**
     * Early payment decreases the amount of monthly payments
     */
    @JsonProperty("decrease_monthly_payment")
    DECREASE_MONTHLY_PAYMENT;

}
