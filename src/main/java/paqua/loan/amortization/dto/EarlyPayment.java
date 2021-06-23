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

import paqua.loan.amortization.api.impl.repeating.EarlyPaymentRepeatingStrategy;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an early pay off of the loan
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

    /**
     * Early payment builder
     */
    public static final class EarlyPaymentBuilder {
        private BigDecimal amount;
        private EarlyPaymentStrategy strategy;
        private EarlyPaymentRepeatingStrategy repeatingStrategy;
        private Map<EarlyPaymentAdditionalParameters, String> additionalParameters;

        public EarlyPaymentBuilder() {
        }

        /**
         * Builds an immutable early payment
         * @return early poyment
         */
        public EarlyPayment build() {
            return new EarlyPayment(
                    amount,
                    strategy,
                    repeatingStrategy,
                    additionalParameters
            );
        }

        @Override
        public String toString() {
            return "EarlyPaymentBuilder{" +
                    "amount=" + amount +
                    ", strategy=" + strategy +
                    ", repeatingStrategy=" + repeatingStrategy +
                    ", additionalParameters=" + additionalParameters +
                    '}';
        }
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
