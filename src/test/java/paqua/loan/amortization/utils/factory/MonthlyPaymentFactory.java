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

import paqua.loan.amortization.dto.MonthlyPayment;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MonthlyPaymentFactory {
    public static MonthlyPayment createDefault() {
        BigDecimal debtPaymentAmount = BigDecimal.valueOf(5000.34);
        BigDecimal interestPaymentAmount = BigDecimal.valueOf(60544.34);

        return MonthlyPayment.builder()
                .monthNumber(0)
                .paymentDate(LocalDate.now())
                .additionalPaymentAmount(BigDecimal.valueOf(2340.33))
                .debtPaymentAmount(debtPaymentAmount)
                .interestPaymentAmount(interestPaymentAmount)
                .paymentAmount(debtPaymentAmount.add(interestPaymentAmount))
                .loanBalanceAmount(BigDecimal.valueOf(99545433.12))
                .build();
    }
}
