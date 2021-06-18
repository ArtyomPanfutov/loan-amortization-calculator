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
package paqua.loan.amortization.api.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import paqua.loan.amortization.api.LoanAmortizationCalculator;
import paqua.loan.amortization.dto.Loan;
import paqua.loan.amortization.dto.LoanAmortization;
import paqua.loan.amortization.exception.LoanAmortizationCalculatorException;
import paqua.loan.amortization.utils.factory.LoanFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoanAmortizationCalculatorImplTest {
    private LoanAmortizationCalculator calculator;

    @BeforeEach
    void initTarget() {
        calculator = new LoanAmortizationCalculatorImpl();
    }

    @Test
    void shouldReturnNonNull() {
        LoanAmortization amortization = calculator.calculate(LoanFactory.createDefaultWithEarlyPayments());

        assertNotNull(amortization);
    }

    @Test
    void shouldFailWhenLoanIsNull() {
        Assertions.assertThrows(LoanAmortizationCalculatorException.class, () ->
                calculator.calculate(null));

    }

    @Test
    void shouldFailWhenLoanValuesAreNull() {
        Assertions.assertThrows(LoanAmortizationCalculatorException.class, () ->
                calculator.calculate(new Loan(null, null, null, null, null)));
    }
}