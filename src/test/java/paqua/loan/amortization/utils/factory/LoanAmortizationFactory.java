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
package paqua.loan.amortization.utils.factory;

import paqua.loan.amortization.dto.EarlyPayment;
import paqua.loan.amortization.dto.LoanAmortization;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoanAmortizationFactory {
    public static LoanAmortization create() {
        EarlyPayment earlyPayment = EarlyPaymentFactory.createSingleWithDecreasePaymentAmountStrategy(100.33);
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        earlyPayments.put(1, earlyPayment);

        return LoanAmortization.builder()
                .earlyPayments(earlyPayments)
                .monthlyPaymentAmount(BigDecimal.valueOf(100))
                .overPaymentAmount(BigDecimal.valueOf(23))
                .monthlyPayments(Collections.singletonList(MonthlyPaymentFactory.createDefault()))
                .build();
    }
}
