package domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * This class represent input data for loan amortization calculation
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
     * Additional payments
     */
    private final List<AdditionalPayment> additionalPayments;

    public Loan(BigDecimal amount, BigDecimal rate, Integer term, List<AdditionalPayment> additionalPayments) {
        this.amount = amount;
        this.rate = rate;
        this.term = term;
        this.additionalPayments = additionalPayments;
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
     * @return Additional payments
     */
    public List<AdditionalPayment> getAdditionalPayments() {
        return additionalPayments;
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
        private List<AdditionalPayment> additionalPayments;

        public LoanBuilder() {
        }

        public LoanBuilder(BigDecimal amount, BigDecimal rate, Integer term, List<AdditionalPayment> additionalPayments) {
            this.amount = amount;
            this.rate = rate;
            this.term = term;
            this.additionalPayments = additionalPayments;
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

        public LoanBuilder additionalPayments(List<AdditionalPayment> additionalPayments) {
            this.additionalPayments = additionalPayments;
            return this;
        }

        public Loan build() {
            return new Loan(amount, rate, term, additionalPayments);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return amount.equals(loan.amount) &&
                rate.equals(loan.rate) &&
                term.equals(loan.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, rate, term);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "amount=" + amount +
                ", rate=" + rate +
                ", term=" + term +
                '}';
    }
}
