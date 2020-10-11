package domain;

/**
 * Represents strategies that can be applied to loan when exists overpayment
 *
 * @author Artyom Panfutov
 */
public enum EarlyPaymentStrategy {
    /**
     * Early payment decreases the term
     */
    DECREASE_TERM,

    /**
     * Early payment decreases the amount of monthly payments
     */
    DECREASE_MONTHLY_PAYMENT
}
