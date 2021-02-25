package loan.amortization.lib.api.impl;

import loan.amortization.lib.api.LoanAmortizationCalculator;
import loan.amortization.lib.api.impl.message.Messages;
import loan.amortization.lib.api.impl.repeating.EarlyPaymentRepeatingStrategy;
import loan.amortization.lib.dto.*;
import loan.amortization.lib.exception.ExceptionType;
import loan.amortization.lib.exception.LoanAmortizationCalculatorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of loan amortization calculator
 *
 * Calculates ANNUAL amortization schedule
 *
 * @author Artyom Panfutov
 */
public class LoanAmortizationCalculatorImpl implements LoanAmortizationCalculator {
    private static final Logger logger = LogManager.getLogger(LoanAmortizationCalculatorImpl.class);
    private static final LoanAmortizationCalculator ANNUAL_PAYMENT_LOAN_AMORTIZATION_CALCULATOR = new AnnualPaymentLoanCalculator();

    /**
     * Calculates annual loan amortization schedule
     *
     * @return Calculated loan amortization schedule {@link LoanAmortization}
     */
    @Override
    public LoanAmortization calculate(Loan inputLoan) {
        validate(inputLoan);

        return ANNUAL_PAYMENT_LOAN_AMORTIZATION_CALCULATOR.calculate(
                getLoanWithImplementedEarlyPaymentStrategy(inputLoan)
        );
    }

    /**
     * Implements early payment repeating strategy
     *
     * Iterates through early payments entries and implements first found repeating strategy
     * @return new loan with filled early payment list (according to a repeating strategy)
     */
    private Loan getLoanWithImplementedEarlyPaymentStrategy(final Loan loan) {
        final Map<Integer, EarlyPayment> allEarlyPayments = new HashMap<>();

        if (loan.getEarlyPayments() != null) {
            final Set<Map.Entry<Integer, EarlyPayment>> payments = loan.getEarlyPayments().entrySet();

            allEarlyPayments.putAll(extractOnlySingleEarlyPayments(loan));

            payments.stream()
                    .filter(p -> p.getValue().getRepeatingStrategy() != EarlyPaymentRepeatingStrategy.SINGLE)
                    .findFirst()
                    // If there are more than one early payments with repeating strategy - we ignore them
                    // This case cannot be supported because it is contradictory - such payments could intersect with each other
                    .ifPresent(p -> p.getValue()
                            .getRepeatingStrategy()
                            .fillEarlyPayments(allEarlyPayments, loan, p.getKey(), p.getValue()));
        }


        logger.info("After applying repeating strategy: " + allEarlyPayments);
        return Loan.builder()
                .amount(loan.getAmount())
                .earlyPayments(allEarlyPayments)
                .rate(loan.getRate())
                .term(loan.getTerm())
                .firstPaymentDate(loan.getFirstPaymentDate())
                .build();
    }

    private void validate(Loan loan) {
        logger.info("Validating input. Loan: " + loan);

        if (loan == null || loan.getAmount() == null || loan.getRate() == null || loan.getTerm() == null) {
            throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.NULL.getMessageText());
        }

        if (loan.getAmount().compareTo(BigDecimal.ZERO) <= 0 || loan.getTerm() <= 0 || loan.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.NEGATIVE_NUMBER.getMessageText());
        }

        // Early payments
        if (loan.getEarlyPayments() != null) {
            for (Map.Entry<Integer, EarlyPayment> entry :loan.getEarlyPayments().entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.NULL.getMessageText());
                }

                if (entry.getKey() < 0) {
                   throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.EARLY_PAYMENT_NUMBER_IS_NEGATIVE.getMessageText());
                }

                if (entry.getValue().getAmount().compareTo(BigDecimal.ZERO) < 0) {
                    throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.EARLY_PAYMENT_AMOUNT_IS_NEGATIVE.getMessageText());
                }

                if (entry.getValue().getStrategy() == null) {
                    throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.EARLY_PAYMENT_STRATEGY_IS_NULL.getMessageText());
                }
            }
        }
        logger.info("Successful validation!");
    }


    private Map<Integer, EarlyPayment> extractOnlySingleEarlyPayments(Loan loan) {
        return loan.getEarlyPayments().entrySet().stream()
                .filter(entry -> entry.getValue().getRepeatingStrategy().equals(EarlyPaymentRepeatingStrategy.SINGLE))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}


