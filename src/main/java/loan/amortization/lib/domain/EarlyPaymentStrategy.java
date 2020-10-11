package loan.amortization.lib.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents strategies that can be applied to loan when exists overpayment
 *
 * @author Artyom Panfutov
 */
public enum EarlyPaymentStrategy {
    /**
     * Early payment decreases the term
     */
    @JsonProperty("decrease_term")
    DECREASE_TERM,

    /**
     * Early payment decreases the amount of monthly payments
     */
    @JsonProperty("decrease_monthly_payment")
    DECREASE_MONTHLY_PAYMENT;

}
