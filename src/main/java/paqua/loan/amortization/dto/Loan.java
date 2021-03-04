package paqua.loan.amortization.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * This class represent input attributes of loan
 *
 * @author Artyom Panfutov
 */
public final class Loan implements Serializable {
    private static final long serialVersionUID = 8435495249049946452L;

    /**
     * Debt amount (principal)
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
     * Early payments (or additional payments)
     *
     * Key: number of payment in payment schedule (starts with 0)
     * Value: early payment data(amount, strategy)
     */
    private final Map<Integer, EarlyPayment> earlyPayments;

    /**
     * First payment date (optional)
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate firstPaymentDate;

    @ConstructorProperties({"amount", "rate", "term", "earlyPayments", "firstPaymentDate"})
    public Loan(BigDecimal amount, BigDecimal rate, Integer term, Map<Integer, EarlyPayment> earlyPayments, LocalDate firstPaymentDate) {
        this.amount = amount;
        this.rate = rate;
        this.term = term;
        this.earlyPayments = earlyPayments;
        this.firstPaymentDate = firstPaymentDate;
    }

    /**
     * @return Debt amount (principal)
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
     * Early payments (or additional payments)
     *
     * Key: number of payment in payment schedule
     * Value: early payment data(amount, strategy)
     *
     * @return Early payments
     */
    public Map<Integer, EarlyPayment> getEarlyPayments() {
        return earlyPayments;
    }

    /**
     * @return First payment date
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate getFirstPaymentDate() {
        return firstPaymentDate;
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
        private LocalDate firstPaymentDate;

        public LoanBuilder() {
        }

        public LoanBuilder(BigDecimal amount, BigDecimal rate, Integer term, Map<Integer, EarlyPayment> earlyPayments, LocalDate firstPaymentDate) {
            this.amount = amount;
            this.rate = rate;
            this.term = term;
            this.earlyPayments = earlyPayments;
            this.firstPaymentDate = firstPaymentDate;
        }

        /**
         * Dept amount (principal)
         *
         * @param amount loan amount
         * @return loan builder
         */
        public LoanBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Interest rate
         *
         * @param rate interest rate
         * @return loan builder
         */
        public LoanBuilder rate(BigDecimal rate) {
            this.rate = rate;
            return this;
        }

        /**
         * Loan term in months
         *
         * @param term (months)
         * @return loan builder
         */
        public LoanBuilder term(Integer term) {
            this.term = term;
            return this;
        }

        public LoanBuilder earlyPayments(Map<Integer, EarlyPayment> earlyPayments) {
            this.earlyPayments = earlyPayments;
            return this;
        }

        public LoanBuilder firstPaymentDate(LocalDate firstPaymentDate) {
            this.firstPaymentDate = firstPaymentDate;
            return this;
        }

        public Loan build() {
            return new Loan(amount, rate, term, earlyPayments, firstPaymentDate);
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
                Objects.equals(firstPaymentDate, loan.firstPaymentDate) &&
                Objects.equals(earlyPayments, loan.earlyPayments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, rate, term, firstPaymentDate, earlyPayments);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "amount=" + amount +
                ", rate=" + rate +
                ", term=" + term +
                ", firstPaymentDate=" + firstPaymentDate +
                ", earlyPayments=" + earlyPayments +
                '}';
    }
}
