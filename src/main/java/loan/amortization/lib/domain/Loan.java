package loan.amortization.lib.domain;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * This class represent input data for loan amortization calculation
 *
 * @author Artyom Panfutov
 */
public final class Loan implements Serializable {
    private static final long serialVersionUID = 8435495249049946452L;

    /**
     * Loan amount
     */
    private final BigDecimal amount;

    /**
     * Interest rate
     */
    private final BigDecimal rate;

    /**
     * Loan term in months
     */
    private final Integer term;

    /**
     * Early payments
     *
     * Key: number of payment in payment schedule
     * Value: early payment data(amount, strategy)
     */
    private final Map<Integer, EarlyPayment> earlyPayments;

    @ConstructorProperties({"amount", "rate", "term", "earlyPayments"})
    public Loan(BigDecimal amount, BigDecimal rate, Integer term, Map<Integer,EarlyPayment> earlyPayments) {
        this.amount = amount;
        this.rate = rate;
        this.term = term;
        this.earlyPayments = earlyPayments;
    }

    /**
     * @return Loan amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return Interest rate
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * @return Loan term in months
     */
    public Integer getTerm() {
        return term;
    }

    /**
     * Early payments
     *
     * Key: number of payment in payment schedule
     * Value: early payment data(amount, strategy)
     *
     * @return Early payments
     */
    public Map<Integer, EarlyPayment> getEarlyPayments() {
        return earlyPayments;
    }

    public static LoanBuilder builder() {
        return new LoanBuilder();
    }

    /**
     * Builder class for Loan
     */
    public static final class LoanBuilder  {

        private BigDecimal amount;
        private BigDecimal rate;
        private Integer term;
        private Map<Integer, EarlyPayment> earlyPayments;

        public LoanBuilder() {
        }

        public LoanBuilder(BigDecimal amount, BigDecimal rate, Integer term, Map<Integer, EarlyPayment> earlyPayments) {
            this.amount = amount;
            this.rate = rate;
            this.term = term;
            this.earlyPayments = earlyPayments;
        }

        public LoanBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public LoanBuilder rate(BigDecimal rate) {
            this.rate = rate;
            return this;
        }

        public LoanBuilder term(Integer term) {
            this.term = term;
            return this;
        }

        public LoanBuilder earlyPayments(Map<Integer, EarlyPayment> earlyPayments) {
            this.earlyPayments = earlyPayments;
            return this;
        }

        public Loan build() {
            return new Loan(amount, rate, term, earlyPayments);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(amount, loan.amount) &&
                Objects.equals(rate, loan.rate) &&
                Objects.equals(term, loan.term) &&
                Objects.equals(earlyPayments, loan.earlyPayments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, rate, term, earlyPayments);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "amount=" + amount +
                ", rate=" + rate +
                ", term=" + term +
                ", earlyPayments=" + earlyPayments +
                '}';
    }
}
