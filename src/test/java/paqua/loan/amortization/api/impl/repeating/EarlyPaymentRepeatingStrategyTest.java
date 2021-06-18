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

import org.junit.jupiter.api.Test;
import paqua.loan.amortization.dto.EarlyPayment;
import paqua.loan.amortization.dto.EarlyPaymentAdditionalParameters;
import paqua.loan.amortization.dto.EarlyPaymentStrategy;
import paqua.loan.amortization.dto.Loan;
import paqua.loan.amortization.utils.factory.EarlyPaymentFactory;
import paqua.loan.amortization.utils.factory.LoanFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EarlyPaymentRepeatingStrategyTest {
    @Test
    void shouldNotRepeatForSingleStrategy() {
        Map<Integer, EarlyPayment> repeated = EarlyPaymentRepeatingStrategy.SINGLE.getRepeated(
                LoanFactory.createDefaultWithEarlyPayments(),
                1,
                EarlyPaymentFactory.createSingleWithDecreasePaymentAmountStrategy(100.00));

        assertTrue(repeated.isEmpty());
    }

    @Test
    void shouldRepeatToTheEndOfTheLoanTerm() {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();
        earlyPayments.put(0, new EarlyPayment(
                BigDecimal.valueOf(100.00),
                EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT,
                EarlyPaymentRepeatingStrategy.TO_END,
                Collections.emptyMap()
                ));

        Loan loan = LoanFactory.getBuilderWithDefaultLoan()
                .earlyPayments(earlyPayments)
                .build();

        Map<Integer, EarlyPayment> repeated = EarlyPaymentRepeatingStrategy.TO_END.getRepeated(
                loan,
                0,
                loan.getEarlyPayments().get(0)
        );

        assertEquals(loan.getTerm(), repeated.size());
    }

    @Test
    void shouldRepeatToCertainMonth() {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();
        Map<EarlyPaymentAdditionalParameters, String> parameters = new HashMap<>();
        int monthNumber = 3;
        parameters.put(EarlyPaymentAdditionalParameters.REPEAT_TO_MONTH_NUMBER, String.valueOf(monthNumber));

        earlyPayments.put(0, new EarlyPayment(
                BigDecimal.valueOf(100.00),
                EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT,
                EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH,
                parameters
        ));

        Loan loan = LoanFactory.getBuilderWithDefaultLoan()
                .earlyPayments(earlyPayments)
                .build();

        Map<Integer, EarlyPayment> repeated = EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH.getRepeated(
                loan,
                0,
                loan.getEarlyPayments().get(0)
        );

        assertEquals(monthNumber, repeated.size());
    }
}