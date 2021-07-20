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
package paqua.loan.amortization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import paqua.loan.amortization.api.LoanAmortizationCalculator;
import paqua.loan.amortization.api.impl.LoanAmortizationCalculatorFactory;
import paqua.loan.amortization.api.impl.repeating.EarlyPaymentRepeatingStrategy;
import paqua.loan.amortization.dto.*;
import paqua.loan.amortization.utils.factory.ObjectMapperFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for Loan Calculator
 *
 * @author Artyom Panfutov
 */
class LoanAmortizationCalculatorTest {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.create();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private LoanAmortizationCalculator calculator;

    @BeforeEach
    public void initTarget() {
        calculator = LoanAmortizationCalculatorFactory.create();
    }

    @Test
    void shouldImplementRepeatingStrategyToEnd() throws IOException {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        earlyPayments.put(5, new EarlyPayment(
                BigDecimal.valueOf(50000),
                EarlyPaymentStrategy.DECREASE_TERM,
                EarlyPaymentRepeatingStrategy.TO_END,
                null
                ));

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .earlyPayments(earlyPayments)
                .term(32)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        LoanAmortization reference = OBJECT_MAPPER.readValue(new File("src/test/resources/reference-repeating-strategy-to-end.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    void shouldCalculateWithFirstPaymentDate() throws IOException {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(1500000))
                .rate(BigDecimal.valueOf(5.32))
                .earlyPayments(earlyPayments)
                .firstPaymentDate(LocalDate.parse("2014-07-02", DATE_TIME_FORMATTER))
                .term(96)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        LoanAmortization reference = OBJECT_MAPPER.readValue(new File("src/test/resources/reference-with-first-payment-date.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    void shouldCalculateWhenFirstPaymentDateIsLastDayInMonth() throws IOException {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(1500000))
                .rate(BigDecimal.valueOf(5.32))
                .earlyPayments(earlyPayments)
                .firstPaymentDate(LocalDate.parse("2014-07-31", DATE_TIME_FORMATTER))
                .term(96)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        LoanAmortization reference = OBJECT_MAPPER.readValue(new File("src/test/resources/reference-when-first-payment-date-is-last-day.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    void shouldCalculateWithRepeatingStrategy() throws IOException {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        earlyPayments.put(5, new EarlyPayment(
                BigDecimal.valueOf(50000),
                EarlyPaymentStrategy.DECREASE_TERM,
                EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH,
                new HashMap<EarlyPaymentAdditionalParameters, String>() {{
                    put(EarlyPaymentAdditionalParameters.REPEAT_TO_MONTH_NUMBER, "7");
                }}
                ));

        earlyPayments.put(15, new EarlyPayment(
                BigDecimal.valueOf(50000),
                EarlyPaymentStrategy.DECREASE_TERM,
                EarlyPaymentRepeatingStrategy.SINGLE,
                null
        ));

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .earlyPayments(earlyPayments)
                .term(32)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        LoanAmortization reference = OBJECT_MAPPER.readValue(new File("src/test/resources/reference-repeating-strategy-to-certain-month-and-single.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    void shouldCalculateWithRepeatingStrategyToEnd() throws IOException {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        earlyPayments.put(0, new EarlyPayment(
                BigDecimal.valueOf(60000),
                EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT,
                EarlyPaymentRepeatingStrategy.TO_END,
                null
                ));

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(6535366))
                .rate(BigDecimal.valueOf(3))
                .earlyPayments(earlyPayments)
                .term(12)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        LoanAmortization reference = OBJECT_MAPPER.readValue(new File("src/test/resources/reference-repeating-strategy-to-end-from-first.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    void shouldImplementRepeatingStrategyToCertainMonth() throws IOException {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        Map<EarlyPaymentAdditionalParameters, String> parameters = new HashMap<>();
        parameters.put(EarlyPaymentAdditionalParameters.REPEAT_TO_MONTH_NUMBER, "10");

        earlyPayments.put(5, new EarlyPayment(
                BigDecimal.valueOf(50000),
                EarlyPaymentStrategy.DECREASE_TERM,
                EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH,
                parameters
        ));

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .earlyPayments(earlyPayments)
                .term(32)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        LoanAmortization reference = OBJECT_MAPPER.readValue(new File("src/test/resources/reference-repeating-strategy-to-certain-month.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    void shouldCalculatePayments() throws IOException {
        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .term(32)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        LoanAmortization reference = OBJECT_MAPPER.readValue(new File("src/test/resources/reference-500000.32-4.56-32.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    void shouldCalculateWithOneEarlyPaymentAndTermDecreasingStrategy() throws IOException {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        earlyPayments.put(5, new EarlyPayment(
                BigDecimal.valueOf(50000),
                EarlyPaymentStrategy.DECREASE_TERM,
                EarlyPaymentRepeatingStrategy.SINGLE,
                null));

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .earlyPayments(earlyPayments)
                .term(32)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        LoanAmortization reference = OBJECT_MAPPER.readValue(new File("src/test/resources/reference-one-early-payment(5th)-500000.32-4.56-32.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    void shouldCalculateWithOneEarlyPaymentAndPaymentAmountDecreasingStrategy() throws IOException {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        earlyPayments.put(5, new EarlyPayment(
                BigDecimal.valueOf(50000),
                EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT,
                EarlyPaymentRepeatingStrategy.SINGLE,
                null));

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .earlyPayments(earlyPayments)
                .term(32)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        LoanAmortization reference = OBJECT_MAPPER.readValue(new File("src/test/resources/reference-one-early-payment(5th)-payment-decrease-500000.32-4.56-32.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    void shouldCalculateWithAllKindsOfEarlyPayment() throws IOException {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        earlyPayments.put(0, new EarlyPayment(
                BigDecimal.valueOf(5000),
                EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT,
                EarlyPaymentRepeatingStrategy.SINGLE,
                null));

        earlyPayments.put(1, new EarlyPayment(
                BigDecimal.valueOf(50000),
                EarlyPaymentStrategy.DECREASE_TERM,
                EarlyPaymentRepeatingStrategy.SINGLE,
                null));

        earlyPayments.put(3, new EarlyPayment(
                BigDecimal.valueOf(30000),
                EarlyPaymentStrategy.DECREASE_TERM,
                EarlyPaymentRepeatingStrategy.SINGLE,
                null));

        earlyPayments.put(5, new EarlyPayment(
                BigDecimal.valueOf(50000),
                EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT,
                EarlyPaymentRepeatingStrategy.SINGLE,
                null));

        earlyPayments.put(6, new EarlyPayment(
                BigDecimal.valueOf(10000),
                EarlyPaymentStrategy.DECREASE_TERM,
                EarlyPaymentRepeatingStrategy.SINGLE,
                null));

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .earlyPayments(earlyPayments)
                .term(10)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        LoanAmortization reference = OBJECT_MAPPER.readValue(new File("src/test/resources/reference-different-early-payments-500000.32-4.56-32.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }
}
