package domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Represents calculation result
 * @author Artyom Panfutov
 */
public final class LoanAmortization implements Serializable {
    private static final long serialVersionUID = 6600584767577828784L;

    /**
     * Amount of monthly payment
     */
    private final BigDecimal monthlyPaymentAmount;

    /**
     * Overpayment of interest
     */
    private final BigDecimal overPaymentAmount;

    /**
     * Calculated list of monthly payments
     */
    private final List<MonthlyPayment> monthlyPayments;

    public LoanAmortization(BigDecimal monthlyPaymentAmount, BigDecimal overPaymentAmount, List<MonthlyPayment> monthlyPayments) {
        this.monthlyPaymentAmount = monthlyPaymentAmount;
        this.overPaymentAmount = overPaymentAmount;
        this.monthlyPayments = monthlyPayments;
    }

    public static LoanAmortizationBuilder builder() {
        return new LoanAmortizationBuilder();
    }

    /**
     * Builder for LoanAmortization class
     */
    public static final class LoanAmortizationBuilder {
        private BigDecimal monthlyPaymentAmount;
        private BigDecimal overPaymentAmount;
        private List<MonthlyPayment> monthlyPayments;

        public LoanAmortizationBuilder() {
        }

        public LoanAmortizationBuilder(BigDecimal monthlyPaymentAmount, BigDecimal overPaymentAmount, List<MonthlyPayment> monthlyPayments) {
            this.monthlyPaymentAmount = monthlyPaymentAmount;
            this.overPaymentAmount = overPaymentAmount;
            this.monthlyPayments = monthlyPayments;
        }

        public LoanAmortizationBuilder monthlyPaymentAmount(BigDecimal monthlyPaymentAmount) {
            this.monthlyPaymentAmount = monthlyPaymentAmount;
            return this;
        }

        public LoanAmortizationBuilder overPaymentAmount(BigDecimal overPaymentAmount) {
            this.overPaymentAmount = overPaymentAmount;
            return this;
        }

        public LoanAmortizationBuilder monthlyPayments(List<MonthlyPayment> monthlyPayments) {
            this.monthlyPayments = monthlyPayments;
            return this;
        }

        public LoanAmortization build() {
            return new LoanAmortization(monthlyPaymentAmount, overPaymentAmount, monthlyPayments);
        }

        @Override
        public String toString() {
            return "LoanAmortizationBuilder{" +
                    "monthlyPaymentAmount=" + monthlyPaymentAmount +
                    ", overPaymentAmount=" + overPaymentAmount +
                    ", monthlyPayments=" + monthlyPayments +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanAmortization that = (LoanAmortization) o;
        return Objects.equals(monthlyPaymentAmount, that.monthlyPaymentAmount) &&
                Objects.equals(overPaymentAmount, that.overPaymentAmount) &&
                Objects.equals(monthlyPayments, that.monthlyPayments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthlyPaymentAmount, overPaymentAmount, monthlyPayments);
    }

    @Override
    public String toString() {
        return "LoanAmortization{" +
                "monthlyPaymentAmount=" + monthlyPaymentAmount +
                ", overPaymentAmount=" + overPaymentAmount +
                ", monthlyPayments=" + monthlyPayments +
                '}';
    }
}
