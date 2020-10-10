package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.AdditionalPayment;
import domain.EarlyPaymentType;
import domain.Loan;
import domain.LoanAmortization;
import exception.LoanAmortizationCalculatorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    public void shouldCalculateWithAdditionalPayments() {
        List<AdditionalPayment> additionalPaymentList = new ArrayList<>();

        additionalPaymentList.add(new AdditionalPayment(5, BigDecimal.valueOf(50000), EarlyPaymentType.LOAN_TERM));
        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .additionalPayments(additionalPaymentList)
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
            calculator.calculate(new Loan(null, null, null, null));
        });
    }
}
