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

import com.fasterxml.jackson.annotation.JsonProperty;
import paqua.loan.amortization.dto.EarlyPayment;
import paqua.loan.amortization.dto.EarlyPaymentAdditionalParameters;
import paqua.loan.amortization.dto.Loan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    @JsonProperty("single")
    SINGLE {
        @Override
        public Map<Integer, EarlyPayment> getRepeated(final Loan loan, final int startNumber, final EarlyPayment earlyPayment) {
            // This strategy must not repeat payments. It is the default strategy for all early payments
            return Collections.emptyMap();
        }
    },

    /**
     * Repeats early payment for each payment in the payment schedule to the end of the loan term
     */
    @JsonProperty("to_end")
    TO_END {
        @Override
        public Map<Integer, EarlyPayment> getRepeated(final Loan loan, final int startNumber, final EarlyPayment earlyPayment) {
            LOGGER.info("Repeating strategy " + EarlyPaymentRepeatingStrategy.TO_END + "\n Repeating the payment: " + earlyPayment);

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
    @JsonProperty("to_certain_month")
    TO_CERTAIN_MONTH {
        @Override
        public Map<Integer, EarlyPayment> getRepeated(final Loan loan, final int startNumber, final EarlyPayment earlyPayment) {
            LOGGER.info("Repeating strategy " + EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH + "\n Repeating the payment: " + earlyPayment);

            int repeatTo = Integer.parseInt(earlyPayment.getAdditionalParameters().get(EarlyPaymentAdditionalParameters.REPEAT_TO_MONTH_NUMBER));

            return repeat(
                        earlyPayment,              // source
                        startNumber,               // from number
                        repeatTo                   // to number
            );
        }
    };

    private static final Logger LOGGER = LogManager.getLogger(EarlyPaymentRepeatingStrategy.class);

    /**
     * Copies early payment withing this range
     *
     * @param earlyPayment source payment to copy
     * @param fromPayment number of the payment from which to start copying
     * @param toPayment number of the payment to stop copying
     */
    Map<Integer, EarlyPayment> repeat(EarlyPayment earlyPayment, int fromPayment, int toPayment) {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        for (int i = fromPayment; i < toPayment; i++) {
            earlyPayments.put(i, new EarlyPayment(
                    earlyPayment.getAmount(),
                    earlyPayment.getStrategy(),
                    EarlyPaymentRepeatingStrategy.SINGLE,
                    null
            ));
        }

        return earlyPayments;
    }

}
