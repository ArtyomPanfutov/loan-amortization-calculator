package loan.amortization.lib.domain;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
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

    /**
     * Repeating strategy
     */
    private final EarlyPaymentRepeatingStrategy repeatingStrategy;

    /**
     * Additional parameters
     */
    private final Map<EarlyPaymentAdditionalParameters, String> additionalParameters;

    @ConstructorProperties({"amount", "type", "repeatingStrategy", "additionalParameters"})
    public EarlyPayment(BigDecimal amount, EarlyPaymentStrategy type, EarlyPaymentRepeatingStrategy repeatingStrategy, Map<EarlyPaymentAdditionalParameters, String> additionalParameters) {
        this.amount = amount;
        this.strategy = type;
        this.repeatingStrategy = repeatingStrategy;
        this.additionalParameters = additionalParameters;
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

    /**
     * @return Repeating strategy
     */
    public EarlyPaymentRepeatingStrategy getRepeatingStrategy() {
        return repeatingStrategy;
    }

    /**
     * @return Additional parameters
     */
    public Map<EarlyPaymentAdditionalParameters, String> getAdditionalParameters() {
        return additionalParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EarlyPayment that = (EarlyPayment) o;
        return Objects.equals(amount, that.amount) &&
                strategy == that.strategy &&
                repeatingStrategy == that.repeatingStrategy &&
                Objects.equals(additionalParameters, that.additionalParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, strategy, repeatingStrategy, additionalParameters);
    }

    @Override
    public String toString() {
        return "EarlyPayment{" +
                "amount=" + amount +
                ", strategy=" + strategy +
                ", repeatingStrategy=" + repeatingStrategy +
                ", additionalParameters=" + additionalParameters +
                '}';
    }
}
