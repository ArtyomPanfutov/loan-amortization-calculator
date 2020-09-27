package service;

import domain.Loan;
import domain.LoanAmortization;
import exception.LoanAmortizationCalculatorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
    public void shouldShouldCalculatePayments() {
        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .term(32)
                .build();

        LoanAmortization amortization = calculator.calculate(loan);

        assertNotNull(amortization);
    }


    @Test
    public void shouldFailWhenLoanIsNull() {
        Assertions.assertThrows(LoanAmortizationCalculatorException.class, () ->{
            calculator.calculate(null);
        });

        Assertions.assertThrows(LoanAmortizationCalculatorException.class, () ->{
            calculator.calculate(new Loan(null, null, null));
        });
    }
}
