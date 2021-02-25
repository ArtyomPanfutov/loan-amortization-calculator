package loan.amortization.lib.api.impl;

import loan.amortization.lib.api.LoanAmortizationCalculator;
import loan.amortization.lib.api.impl.message.Messages;
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
    private Loan getLoanWithImplementedEarlyPaymentStrategy(Loan loan) {
        Map<Integer, EarlyPayment> allEarlyPayments = new HashMap<>();

        if (loan.getEarlyPayments() != null) {
            Set<Map.Entry<Integer, EarlyPayment>> payments = loan.getEarlyPayments().entrySet();

            allEarlyPayments.putAll(extractOnlySingleEarlyPayments(loan));

            for (Map.Entry<Integer, EarlyPayment> entry : payments) {
                EarlyPayment earlyPayment = entry.getValue();

                fillEarlyPaymentsAccordingToRepeatingStrategy(allEarlyPayments, loan, entry.getKey(), earlyPayment);

                // Strategy implemented - stop the cycle
                break;
            }
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

    private void fillEarlyPaymentsAccordingToRepeatingStrategy(Map<Integer, EarlyPayment> allEarlyPayments, Loan loan, int firstEarlyPaymentNumber, EarlyPayment earlyPayment) {
        if (earlyPayment.getRepeatingStrategy() == EarlyPaymentRepeatingStrategy.TO_END) {
            logger.info("Repeating strategy " + EarlyPaymentRepeatingStrategy.TO_END + "\n Repeating the payment: " + earlyPayment);

            allEarlyPayments.putAll(
                    repeatEarlyPayment(
                            earlyPayment,             // source
                            firstEarlyPaymentNumber,  // from number
                            loan.getTerm()            // to number
            ));

        } else if (earlyPayment.getRepeatingStrategy() == EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH) {
            logger.info("Repeating strategy " + EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH + "\n Repeating the payment: " + earlyPayment);

            int repeatTo = Integer.parseInt(earlyPayment.getAdditionalParameters().get(EarlyPaymentAdditionalParameters.REPEAT_TO_MONTH_NUMBER));

            allEarlyPayments.putAll(repeatEarlyPayment(
                    earlyPayment,              // source
                    firstEarlyPaymentNumber,   // from number
                    repeatTo                   // to number
            ));
        }
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

    /**
     * Copies early payment withing this range
     *
     * @param earlyPayment source payment to copy
     * @param fromPayment number of the payment from which to start copying
     * @param toPayment number of the payment to stop copying
     */
    private Map<Integer, EarlyPayment> repeatEarlyPayment(EarlyPayment earlyPayment, int fromPayment, int toPayment) {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();

        for (int i = fromPayment; i < toPayment; i++) {
            earlyPayments.put(i, new EarlyPayment(
                    earlyPayment.getAmount(),
                    earlyPayment.getStrategy(),
                    EarlyPaymentRepeatingStrategy.SINGLE,
                    null
            ));
        }

        return earlyPayments;
    }

    private Map<Integer, EarlyPayment> extractOnlySingleEarlyPayments(Loan loan) {
        return loan.getEarlyPayments().entrySet().stream()
                .filter(entry -> entry.getValue().getRepeatingStrategy().equals(EarlyPaymentRepeatingStrategy.SINGLE))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}


