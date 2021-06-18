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