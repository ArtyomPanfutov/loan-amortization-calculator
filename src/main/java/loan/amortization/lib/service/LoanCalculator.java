package loan.amortization.lib.service;

import loan.amortization.lib.domain.*;
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
public class LoanCalculator implements Calculator {
    private static final Logger logger = LogManager.getLogger(LoanCalculator.class);
    private static final Calculator ANNUAL_PAYMENT_CALCULATOR = new AnnualPaymentCalculator();
    private static final Calculator ANNUAL_PAYMENT_CALCULATOR_WITH_PAYMENT_DATE = new AnnualPaymentCalculatorWithPaymentDate();

    /**
     * Calculates annual loan amortization schedule
     *
     * @param loan Loan attributes
     *
     * @return Calculated loan amortization schedule {@link LoanAmortization}
     */
    @Override
    public LoanAmortization calculate(Loan loan) {
        validate(loan);

        loan = prepareRepeatableEarlyPayments(loan);

        Calculator calculator = getCalculator(loan);
        return calculator.calculate(loan);
    }

    /**
     * Implements early payment repeating strategy
     *
     * Iterates through early payments entries and implements first found repeating strategy
     * @param loan loan attributes
     */
    // TODO Refactor !!!
    private Loan prepareRepeatableEarlyPayments(Loan loan) {
        Map<Integer, EarlyPayment> newEarlyPayments = new HashMap<>();

        if (loan.getEarlyPayments() != null) {
            Set<Map.Entry<Integer, EarlyPayment>> payments = loan.getEarlyPayments().entrySet();
            newEarlyPayments = loan.getEarlyPayments().entrySet().stream()
                    .filter(entry -> entry.getValue().getRepeatingStrategy().equals(EarlyPaymentRepeatingStrategy.SINGLE))
                    .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));

            for (Map.Entry<Integer, EarlyPayment> entry : payments) {
                EarlyPayment earlyPayment = entry.getValue();

                if (earlyPayment.getRepeatingStrategy() == EarlyPaymentRepeatingStrategy.TO_END) {
                    logger.info("Repeating strategy " + EarlyPaymentRepeatingStrategy.TO_END + "\n Repeating the payment: " + earlyPayment);

                    repeatEarlyPayment(
                            entry.getKey(),         // from
                            loan.getTerm(),         // to
                            newEarlyPayments,       // destination
                            earlyPayment            // source
                    );

                    // Strategy implemented - stop the cycle
                    break;

                } else if (earlyPayment.getRepeatingStrategy() == EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH) {
                    logger.info("Repeating strategy " + EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH + "\n Repeating the payment: " + earlyPayment);

                    int repeatTo = Integer.parseInt(earlyPayment.getAdditionalParameters().get(EarlyPaymentAdditionalParameters.REPEAT_TO_MONTH_NUMBER));
                    repeatEarlyPayment(
                            entry.getKey(),         // from
                            repeatTo,               // to
                            newEarlyPayments,       // destination
                            earlyPayment            // source
                    );

                    // Strategy implemented - stop the cycle
                    break;
                }
            }
        }


        logger.info("After applying repeating strategy: " + newEarlyPayments);
        return Loan.builder()
                .amount(loan.getAmount())
                .earlyPayments(newEarlyPayments)
                .rate(loan.getRate())
                .term(loan.getTerm())
                .firstPaymentDate(loan.getFirstPaymentDate())
                .build();
    }


    /**
     * Validates loan attributes
     *
     * @param loan loan attributes
     * @throws LoanAmortizationCalculatorException
     */
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
     * Copies early payment to end from
     *
     * @param fromPayment number of payment from which to start copying
     * @param toPayment number of payment to stop copying
     * @param newEarlyPayments destination map for copying
     * @param earlyPayment source payment
     */
    private void repeatEarlyPayment(int fromPayment, int toPayment, Map<Integer, EarlyPayment> newEarlyPayments, EarlyPayment earlyPayment) {
        for (int i = fromPayment; i < toPayment; i++) {
            newEarlyPayments.put(i, new EarlyPayment(
                    earlyPayment.getAmount(),
                    earlyPayment.getStrategy(),
                    EarlyPaymentRepeatingStrategy.SINGLE,
                    null
            ));
        }
    }

    private Calculator getCalculator(Loan loan) {
        return loan.getFirstPaymentDate() == null ? ANNUAL_PAYMENT_CALCULATOR : ANNUAL_PAYMENT_CALCULATOR_WITH_PAYMENT_DATE;
    }

}


