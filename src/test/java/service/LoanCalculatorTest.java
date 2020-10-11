package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.*;
import exception.LoanAmortizationCalculatorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for LoanCalculator
 * @author Artyom Panfutov
 */
public class LoanCalculatorTest {
    private LoanCalculator calculator;

    @BeforeEach
    public void initTarget() {
        calculator = new LoanCalculator();
    }

    @Test
    public void shouldCalculatePayments() throws IOException {
        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .term(32)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        ObjectMapper objectMapper = new ObjectMapper();
        LoanAmortization reference = objectMapper.readValue(new File("src/test/resources/reference-500000.32-4.56-32.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    public void shouldCalculateWithOneEarlyPaymentAndTermDecreasingStrategy() throws IOException {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        earlyPayments.put(5, new EarlyPayment(
                BigDecimal.valueOf(50000),
                EarlyPaymentStrategy.DECREASE_TERM,
                EarlyPaymentRepeatingStrategy.SINGLE));

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .earlyPayments(earlyPayments)
                .term(32)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        ObjectMapper objectMapper = new ObjectMapper();

        LoanAmortization reference = objectMapper.readValue(new File("src/test/resources/reference-one-early-payment(5th)-500000.32-4.56-32.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    public void shouldCalculateWithOneEarlyPaymentAndPaymentAmountDecreasingStrategy() throws IOException {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        earlyPayments.put(5, new EarlyPayment(
                BigDecimal.valueOf(50000),
                EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT,
                EarlyPaymentRepeatingStrategy.SINGLE));

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .earlyPayments(earlyPayments)
                .term(32)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);
        assertNotNull(amortization);

        ObjectMapper objectMapper = new ObjectMapper();
        LoanAmortization reference = objectMapper.readValue(new File("src/test/resources/reference-one-early-payment(5th)-payment-decrease-500000.32-4.56-32.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }


    @Test
    public void shouldFailWhenLoanIsNull() {
        Assertions.assertThrows(LoanAmortizationCalculatorException.class, () ->{
            calculator.calculate(null);
        });

        Assertions.assertThrows(LoanAmortizationCalculatorException.class, () ->{
            calculator.calculate(new Loan(null, null, null, null));
        });
    }
}
