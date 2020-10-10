package domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class AdditionalPayment implements Serializable {
    private static final long serialVersionUID = -4828623603301324540L;

    private final Integer number;
    private final BigDecimal amount;
    private final EarlyPaymentType type;

    public AdditionalPayment(Integer number, BigDecimal amount, EarlyPaymentType type) {
        this.number = number;
        this.amount = amount;
        this.type = type;
    }

    public Integer getNumber() {
        return number;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public EarlyPaymentType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdditionalPayment that = (AdditionalPayment) o;
        return Objects.equals(number, that.number) &&
                Objects.equals(amount, that.amount) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, amount, type);
    }
}
