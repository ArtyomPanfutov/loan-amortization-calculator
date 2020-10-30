package loan.amortization.lib.domain;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents detailed information about monthly payment in loan amortization
 *
 * @author Artyom Panfutov
 */
public final class MonthlyPayment implements Serializable {
    private static final long serialVersionUID = -8046672296117265073L;

    /**
     * Month number (starts with 0)
     */
    private final Integer monthNumber;

    /**
     * Amount of remaining debt (balance)
     */
    private final BigDecimal loanBalanceAmount;

    /**
     * Amount of debt in payment (principal piece in payment)
     */
    private final BigDecimal debtPaymentAmount;

    /**
     * Amount of interest in payment
     */
    private final BigDecimal interestPaymentAmount;

    /**
     * Payment amount
     */
    private final BigDecimal paymentAmount;

    /**
     * Additional payment
     */
    private final BigDecimal additionalPaymentAmount;

    @ConstructorProperties({"monthNumber", "loanBalanceAmount", "debtPaymentAmount", "interestPaymentAmount", "paymentAmount", "additionalPaymentAmount"})
    public MonthlyPayment(Integer monthNumber, BigDecimal loanBalanceAmount, BigDecimal debtPaymentAmount, BigDecimal interestPaymentAmount, BigDecimal paymentAmount, BigDecimal additionalPaymentAmount) {
        this.monthNumber = monthNumber;
        this.loanBalanceAmount = loanBalanceAmount;
        this.debtPaymentAmount = debtPaymentAmount;
        this.interestPaymentAmount = interestPaymentAmount;
        this.paymentAmount = paymentAmount;
        this.additionalPaymentAmount = additionalPaymentAmount;
    }

    /**
     * @return Month number (starts with 0)
     */
    public Integer getMonthNumber() {
        return monthNumber;
    }

    /**
     * @return Amount of remaining debt (loan balance)
     */
    public BigDecimal getLoanBalanceAmount() {
        return loanBalanceAmount;
    }

    /**
     * @return Amount of debt in payment (principal debt amount in payment)
     */
    public BigDecimal getDebtPaymentAmount() {
        return debtPaymentAmount;
    }

    /**
     * @return Amount of interest in payment
     */
    public BigDecimal getInterestPaymentAmount() {
        return interestPaymentAmount;
    }

    /**
     * @return Amount of payment
     */
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * @return Additional payment amount
     */
    public BigDecimal getAdditionalPaymentAmount() {
        return additionalPaymentAmount;
    }

    public static MonthlyPaymentBuilder builder() {
        return new MonthlyPaymentBuilder();
    }

    /**
     * Builder for MonthlyPayment
     */
    public static final class MonthlyPaymentBuilder {
        private Integer monthNumber;
        private BigDecimal loanBalanceAmount;
        private BigDecimal debtPaymentAmount;
        private BigDecimal interestPaymentAmount;
        private BigDecimal paymentAmount;
        private BigDecimal additionalPaymentAmount;

        public MonthlyPaymentBuilder() {
        }

        public MonthlyPaymentBuilder(Integer monthNumber, BigDecimal loanBalanceAmount, BigDecimal debtPaymentAmount, BigDecimal interestPaymentAmount, BigDecimal paymentAmount, BigDecimal additionalPaymentAmount) {
            this.monthNumber = monthNumber;
            this.loanBalanceAmount = loanBalanceAmount;
            this.debtPaymentAmount = debtPaymentAmount;
            this.interestPaymentAmount = interestPaymentAmount;
            this.paymentAmount = paymentAmount;
            this.additionalPaymentAmount = additionalPaymentAmount;
        }

        public MonthlyPaymentBuilder monthNumber(Integer monthNumber) {
            this.monthNumber = monthNumber;
            return this;
        }
        public MonthlyPaymentBuilder loanBalanceAmount(BigDecimal loanBalanceAmount) {
            this.loanBalanceAmount = loanBalanceAmount;
            return this;
        }
        public MonthlyPaymentBuilder debtPaymentAmount(BigDecimal debtPaymentAmount) {
            this.debtPaymentAmount = debtPaymentAmount;
            return this;
        }
        public MonthlyPaymentBuilder interestPaymentAmount(BigDecimal interestPaymentAmount) {
            this.interestPaymentAmount = interestPaymentAmount;
            return this;
        }
        public MonthlyPaymentBuilder paymentAmount(BigDecimal paymentAmount) {
            this.paymentAmount = paymentAmount;
            return this;
        }
        public MonthlyPaymentBuilder additionalPaymentAmount(BigDecimal additionalPaymentAmount) {
            this.additionalPaymentAmount = additionalPaymentAmount;
            return this;
        }

        public MonthlyPayment build() {
            return new MonthlyPayment(monthNumber, loanBalanceAmount, debtPaymentAmount, interestPaymentAmount, paymentAmount, additionalPaymentAmount);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonthlyPayment that = (MonthlyPayment) o;
        return Objects.equals(monthNumber, that.monthNumber) &&
                Objects.equals(loanBalanceAmount, that.loanBalanceAmount) &&
                Objects.equals(debtPaymentAmount, that.debtPaymentAmount) &&
                Objects.equals(interestPaymentAmount, that.interestPaymentAmount) &&
                Objects.equals(paymentAmount, that.paymentAmount) &&
                Objects.equals(additionalPaymentAmount, that.additionalPaymentAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthNumber, loanBalanceAmount, debtPaymentAmount, interestPaymentAmount, paymentAmount, additionalPaymentAmount);
    }

    @Override
    public String toString() {
        return "MonthlyPayment{" +
                "monthNumber=" + monthNumber +
                ", loanBalanceAmount=" + loanBalanceAmount +
                ", debtPaymentAmount=" + debtPaymentAmount +
                ", interestPaymentAmount=" + interestPaymentAmount +
                ", paymentAmount=" + paymentAmount +
                ", additionalPaymentAmount=" + additionalPaymentAmount +
                '}';
    }
}
