/*
 * MIT License
 *
 * Copyright (c) 2021 Artyom Panfutov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package paqua.loan.amortization.dto;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents loan amortization attributes
 *
 * @author Artyom Panfutov
 */
public final class LoanAmortization implements Serializable {
    private static final long serialVersionUID = 6600584767577828784L;

    /**
     * Amount of calculated monthly payment
     */
    private final BigDecimal monthlyPaymentAmount;

    /**
     * Total amount of overpayment of interests
     */
    private final BigDecimal overPaymentAmount;

    /**
     * Calculated list of monthly payments (amortization)
     */
    private final List<MonthlyPayment> monthlyPayments;

    /**
     * Early payments
     *
     * Key: number of payment in payment schedule (starts with 0)
     * Value: early payment data(amount, strategy)
     *
     * @return Early payments
     */
    private final Map<Integer, EarlyPayment> earlyPayments;

    @ConstructorProperties({"monthlyPaymentAmount", "overPaymentAmount", "monthlyPayments", "earlyPayments"})
    public LoanAmortization(BigDecimal monthlyPaymentAmount, BigDecimal overPaymentAmount, List<MonthlyPayment> monthlyPayments, Map<Integer, EarlyPayment> earlyPayments) {
        this.monthlyPaymentAmount = monthlyPaymentAmount;
        this.overPaymentAmount = overPaymentAmount;
        this.monthlyPayments = monthlyPayments;
        this.earlyPayments = earlyPayments;
    }

    /**
     * @return Amount of calculated monthly payment
     */
    public BigDecimal getMonthlyPaymentAmount() {
        return monthlyPaymentAmount;
    }

    /**
     * @return Total amount of overpayment of interests
     */
    public BigDecimal getOverPaymentAmount() {
        return overPaymentAmount;
    }

    /**
     * @return Calculated list of monthly payments (amortization)
     */
    public List<MonthlyPayment> getMonthlyPayments() {
        return monthlyPayments;
    }

    /**
     * Key: number of payment in payment schedule (starts with 0)
     * Value: early payment data(amount, strategy)
     *
     * @return Early payments (additional payments to monthly payments)
     */
    public Map<Integer, EarlyPayment> getEarlyPayments() {
        return earlyPayments;
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
        private Map<Integer, EarlyPayment> earlyPayments;

        public LoanAmortizationBuilder() {
        }

        public LoanAmortizationBuilder(BigDecimal monthlyPaymentAmount, BigDecimal overPaymentAmount, List<MonthlyPayment> monthlyPayments, Map<Integer, EarlyPayment> earlyPayments) {
            this.monthlyPaymentAmount = monthlyPaymentAmount;
            this.overPaymentAmount = overPaymentAmount;
            this.monthlyPayments = monthlyPayments;
            this.earlyPayments = earlyPayments;
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

        public LoanAmortizationBuilder earlyPayments(Map<Integer, EarlyPayment> earlyPayments) {
            this.earlyPayments = earlyPayments;
            return this;
        }

        public LoanAmortization build() {
            return new LoanAmortization(monthlyPaymentAmount, overPaymentAmount, monthlyPayments, earlyPayments);
        }

        @Override
        public String toString() {
            return "LoanAmortizationBuilder{" +
                    "monthlyPaymentAmount=" + monthlyPaymentAmount +
                    ", overPaymentAmount=" + overPaymentAmount +
                    ", monthlyPayments=" + monthlyPayments +
                    ", earlyPayments=" + earlyPayments +
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
                Objects.equals(monthlyPayments, that.monthlyPayments) &&
                Objects.equals(earlyPayments, that.earlyPayments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthlyPaymentAmount, overPaymentAmount, monthlyPayments, earlyPayments);
    }

    @Override
    public String toString() {
        return "LoanAmortization{" +
                "monthlyPaymentAmount=" + monthlyPaymentAmount +
                ", overPaymentAmount=" + overPaymentAmount +
                ", monthlyPayments=" + monthlyPayments +
                ", earlyPayments=" + earlyPayments +
                '}';
    }
}
