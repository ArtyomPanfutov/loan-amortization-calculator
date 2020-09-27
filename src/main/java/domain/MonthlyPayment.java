package domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents detailed information about monthly payment
 * @author Artyom Panfutov
 */
public final class MonthlyPayment implements Serializable {
    private static final long serialVersionUID = -8046672296117265073L;

    /**
     * Month
     */
    private final Integer monthNumber;

    /**
     * Amount of remaining debt (Balance)
     */
    private final BigDecimal mainDebtAmount;

    /**
     * Amount of debt in payment (Principal)
     */
    private final BigDecimal debtPaymentAmount;

    /**
     * Amount of interest in payment
     */
    private final BigDecimal interestPaymentAmount;

    public MonthlyPayment(Integer monthNumber, BigDecimal mainDebtAmount, BigDecimal debtPaymentAmount, BigDecimal interestPaymentAmount) {
        this.monthNumber = monthNumber;
        this.mainDebtAmount = mainDebtAmount;
        this.debtPaymentAmount = debtPaymentAmount;
        this.interestPaymentAmount = interestPaymentAmount;
    }

    /**
     * @return Month number
     */
    public Integer getMonthNumber() {
        return monthNumber;
    }

    /**
     * @return Amount of remaining debt (Balance)
     */
    public BigDecimal getMainDebtAmount() {
        return mainDebtAmount;
    }

    /**
     * @return Amount of debt in payment (Principal)
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
     * Builder for MonthlyPayment
     */
    public static final class MonthlyPaymentBuilder {
        private Integer monthNumber;
        private BigDecimal mainDebtAmount;
        private BigDecimal debtPaymentAmount;
        private BigDecimal interestPaymentAmount;

        public MonthlyPaymentBuilder() {
        }

        public MonthlyPaymentBuilder(Integer monthNumber, BigDecimal mainDebtAmount, BigDecimal debtPaymentAmount, BigDecimal interestPaymentAmount) {
            this.monthNumber = monthNumber;
            this.mainDebtAmount = mainDebtAmount;
            this.debtPaymentAmount = debtPaymentAmount;
            this.interestPaymentAmount = interestPaymentAmount;
        }

        public MonthlyPaymentBuilder monthNumber(Integer monthNumber) {
            this.monthNumber = monthNumber;
            return this;
        }
        public MonthlyPaymentBuilder mainDebtAmount(BigDecimal mainDebtAmount) {
            this.mainDebtAmount = mainDebtAmount;
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
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonthlyPayment that = (MonthlyPayment) o;
        return Objects.equals(monthNumber, that.monthNumber) &&
                Objects.equals(mainDebtAmount, that.mainDebtAmount) &&
                Objects.equals(debtPaymentAmount, that.debtPaymentAmount) &&
                Objects.equals(interestPaymentAmount, that.interestPaymentAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthNumber, mainDebtAmount, debtPaymentAmount, interestPaymentAmount);
    }

    @Override
    public String toString() {
        return "MonthlyPayment{" +
                "monthNumber=" + monthNumber +
                ", maidDebtAmount=" + mainDebtAmount +
                ", debtPaymentAmount=" + debtPaymentAmount +
                ", percentPaymentAmount=" + interestPaymentAmount +
                '}';
    }
}
