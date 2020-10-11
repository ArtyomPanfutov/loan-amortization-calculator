package domain;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents early pay off of the loan
 */
public class EarlyPayment implements Serializable {
    private static final long serialVersionUID = -4828623603301324540L;

    /**
     * Payment amount
     */
    private final BigDecimal amount;

    /**
     * Type of early payment
     */
    private final EarlyPaymentStrategy strategy;

    @ConstructorProperties({"amount", "type"})
    public EarlyPayment(BigDecimal amount, EarlyPaymentStrategy type) {
        this.amount = amount;
        this.strategy = type;
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
        return Objects.equals(amount, that.amount) &&
                strategy == that.strategy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, strategy);
    }

    @Override
    public String toString() {
        return "EarlyPayment{" +
                "amount=" + amount +
                ", strategy=" + strategy +
                '}';
    }
}
