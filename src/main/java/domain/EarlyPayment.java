package domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents early pay off of the loan
 */
public class EarlyPayment implements Serializable {
    private static final long serialVersionUID = -4828623603301324540L;

    /**
     * Number of payment
     */
    private final Integer number;

    /**
     * Payment amount
     */
    private final BigDecimal amount;

    /**
     * Type of early payment
     */
    private final EarlyPaymentStrategy strategy;

    public EarlyPayment(Integer number, BigDecimal amount, EarlyPaymentStrategy type) {
        this.number = number;
        this.amount = amount;
        this.strategy = type;
    }

    /**
     * @return Number of payment in payment schedule
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * @return Amount of early payment
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return Strategy
     */
    public EarlyPaymentStrategy getStrategy() {
        return strategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EarlyPayment that = (EarlyPayment) o;
        return Objects.equals(number, that.number) &&
                Objects.equals(amount, that.amount) &&
                strategy == that.strategy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, amount, strategy);
    }

    @Override
    public String toString() {
        return "EarlyPayment{" +
                "number=" + number +
                ", amount=" + amount +
                ", strategy=" + strategy +
                '}';
    }
}
