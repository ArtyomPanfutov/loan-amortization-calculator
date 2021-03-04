package paqua.loan.amortization.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import paqua.loan.amortization.api.LoanAmortizationCalculator;
import paqua.loan.amortization.api.impl.LoanAmortizationCalculatorImpl;
import paqua.loan.amortization.api.impl.repeating.EarlyPaymentRepeatingStrategy;
import paqua.loan.amortization.exception.LoanAmortizationCalculatorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import paqua.loan.amortization.dto.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for LoanCalculator
 *
 * @author Artyom Panfutov
 */
public class LoanLoanAmortizationCalculatorTest {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LoanAmortizationCalculator calculator;

    @BeforeEach
    public void initTarget() {
        calculator = new LoanAmortizationCalculatorImpl();
    }

    @Test
    public void shouldImplementRepeatingStrategyToEnd() throws IOException {
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

        ObjectMapper objectMapper = new ObjectMapper();
        LoanAmortization reference = objectMapper.readValue(new File("src/test/resources/reference-repeating-strategy-to-end.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    public void shouldCalculateWithFirstPaymentDate() throws IOException, ParseException {
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

        ObjectMapper objectMapper = new ObjectMapper();
        LoanAmortization reference = objectMapper.readValue(new File("src/test/resources/reference-with-first-payment-date.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    public void shouldCalculateWhenFirstPaymentDateIsLastDayInMonth() throws IOException, ParseException {
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

        ObjectMapper objectMapper = new ObjectMapper();
        LoanAmortization reference = objectMapper.readValue(new File("src/test/resources/reference-when-first-payment-date-is-last-day.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    public void shouldCalculateWithRepeatingStrategy() throws IOException {
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

        ObjectMapper objectMapper = new ObjectMapper();
        LoanAmortization reference = objectMapper.readValue(new File("src/test/resources/reference-repeating-strategy-to-certain-month-and-single.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    public void shouldCalculateWithRepeatingStrategyToEnd() throws IOException {
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

        ObjectMapper objectMapper = new ObjectMapper();
        LoanAmortization reference = objectMapper.readValue(new File("src/test/resources/reference-repeating-strategy-to-end-from-first.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    public void shouldImplementRepeatingStrategyToCertainMonth() throws IOException {
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

        ObjectMapper objectMapper = new ObjectMapper();
        LoanAmortization reference = objectMapper.readValue(new File("src/test/resources/reference-repeating-strategy-to-certain-month.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
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

        ObjectMapper objectMapper = new ObjectMapper();
        LoanAmortization reference = objectMapper.readValue(new File("src/test/resources/reference-one-early-payment(5th)-payment-decrease-500000.32-4.56-32.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }

    @Test
    public void shouldCalculateWithAllKindsOfEarlyPayment() throws IOException {
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

        ObjectMapper objectMapper = new ObjectMapper();
        LoanAmortization reference = objectMapper.readValue(new File("src/test/resources/reference-different-early-payments-500000.32-4.56-32.json"), LoanAmortization.class);

        assertEquals(reference, amortization);
    }


    @Test
    public void shouldFailWhenLoanIsNull() {
        Assertions.assertThrows(LoanAmortizationCalculatorException.class, () ->{
            calculator.calculate(null);
        });

        Assertions.assertThrows(LoanAmortizationCalculatorException.class, () ->{
            calculator.calculate(new Loan(null, null, null, null, null));
        });
    }
}
