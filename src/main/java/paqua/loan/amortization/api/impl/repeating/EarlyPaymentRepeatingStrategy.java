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
package paqua.loan.amortization.api.impl.repeating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import paqua.loan.amortization.dto.EarlyPayment;
import paqua.loan.amortization.dto.EarlyPaymentAdditionalParameters;
import paqua.loan.amortization.dto.Loan;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents strategies with implementation for repeating early payments
 *
 * @author Artyom Panfutov
 */
public enum EarlyPaymentRepeatingStrategy implements RepeatableEarlyPayment {

    /**
     * Single payment, without repeating
     */
    SINGLE {
        @Override
        public Map<Integer, EarlyPayment> getRepeated(final Loan loan, final int startNumber, final EarlyPayment earlyPayment) {
            log(earlyPayment, SINGLE);

            // This strategy must not repeat payments. It is the default strategy for all early payments
            return Collections.emptyMap();
        }
    },

    /**
     * Repeats early payment for each payment in the payment schedule to the end of the loan term
     */
    TO_END {
        @Override
        public Map<Integer, EarlyPayment> getRepeated(final Loan loan, final int startNumber, final EarlyPayment earlyPayment) {
            log(earlyPayment, TO_END);

            return repeat(
                        earlyPayment,             // source
                        startNumber,              // from number
                        loan.getTerm()            // to number
            );

        }

    },

    /**
     * Repeats early payment to the specified month number
     */
    TO_CERTAIN_MONTH {
        @Override
        public Map<Integer, EarlyPayment> getRepeated(final Loan loan, final int startNumber, final EarlyPayment earlyPayment) {
            log(earlyPayment, EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH);

            int repeatTo = Integer.parseInt(earlyPayment.getAdditionalParameters().get(EarlyPaymentAdditionalParameters.REPEAT_TO_MONTH_NUMBER));

            return repeat(
                        earlyPayment,              // source
                        startNumber,               // from number
                        repeatTo                   // to number
            );
        }
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(EarlyPaymentRepeatingStrategy.class);

    /**
     * Copies an early payment within this range
     *
     * @param earlyPayment source payment to copy
     * @param fromPayment number of the payment from which to start copying (left boundary, inclusive)
     * @param toPayment number of the payment to stop copying (right boundary, exclusive)
     */
    Map<Integer, EarlyPayment> repeat(EarlyPayment earlyPayment, int fromPayment, int toPayment) {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        for (int i = fromPayment; i < toPayment; i++) {
            earlyPayments.put(i, new EarlyPayment(
                    earlyPayment.getAmount(),
                    earlyPayment.getStrategy(),
                    EarlyPaymentRepeatingStrategy.SINGLE,
                    null));
        }

        return earlyPayments;
    }

    void log(EarlyPayment earlyPayment, EarlyPaymentRepeatingStrategy strategy) {
        LOGGER.debug("Repeating strategy {} \n Repeating the payment: {} ",  strategy, earlyPayment);
    }
}
