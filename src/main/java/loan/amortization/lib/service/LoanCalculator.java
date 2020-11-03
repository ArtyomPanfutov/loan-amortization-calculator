package loan.amortization.lib.service;

import loan.amortization.lib.domain.*;
import loan.amortization.lib.exception.ExceptionType;
import loan.amortization.lib.exception.LoanAmortizationCalculatorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Implementation of loan amortization calculator
 *
 * Calculates ANNUAL amortization schedule
 *
 * @author Artyom Panfutov
 */
public class LoanCalculator implements Calculator {
    private static final Logger logger = LogManager.getLogger(LoanCalculator.class);

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

        BigDecimal overPaidInterestAmount = BigDecimal.ZERO;

        Map<Integer, EarlyPayment> earlyPayments = loan.getEarlyPayments() != null ? loan.getEarlyPayments() : Collections.EMPTY_MAP;
        BigDecimal loanBalance = loan.getAmount();

        BigDecimal monthlyInterestRate = loan.getRate()
                                                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP)
                                                .divide(BigDecimal.valueOf(12), 15, RoundingMode.HALF_UP);
        logger.info("Calculated monthly interest rate: {}", monthlyInterestRate);

        int term = loan.getTerm();
        BigDecimal monthlyPaymentAmount = getMonthlyPaymentAmount(loanBalance, monthlyInterestRate, term);

        LoanAmortization.LoanAmortizationBuilder amortizationBuilder = LoanAmortization.builder();
        amortizationBuilder.monthlyPaymentAmount(monthlyPaymentAmount);

        // Calculate amortization schedule
        List<MonthlyPayment> payments = new ArrayList<>();
        for (int i = 0; i < term; i++) {
            BigDecimal principalAmount;
            BigDecimal paymentAmount;
            BigDecimal additionalPaymentAmount = BigDecimal.ZERO;

            BigDecimal interestAmount = loanBalance
                    .multiply(monthlyInterestRate)
                    .setScale(2, RoundingMode.HALF_UP);

            // If something gets negative for some reason (because of early payments) we stop calculating and correct the amount in the last payment
            if (interestAmount.compareTo(BigDecimal.ZERO) < 0 || loanBalance.compareTo(BigDecimal.ZERO) < 0) {
                int lastPaymentNumber = i - 1;

                if (lastPaymentNumber >= 0) {
                    MonthlyPayment lastPayment = payments.get(lastPaymentNumber);

                    payments.set(lastPaymentNumber, new MonthlyPayment.MonthlyPaymentBuilder()
                                .monthNumber(lastPayment.getMonthNumber())
                                .additionalPaymentAmount(lastPayment.getAdditionalPaymentAmount())
                                .paymentAmount(lastPayment.getLoanBalanceAmount()
                                                    .add(lastPayment.getInterestPaymentAmount()))
                                .debtPaymentAmount(lastPayment.getLoanBalanceAmount())
                                .interestPaymentAmount(lastPayment.getInterestPaymentAmount())
                                .loanBalanceAmount(lastPayment.getLoanBalanceAmount())
                                .build());
                }

                break;
            }

            overPaidInterestAmount = overPaidInterestAmount.add(interestAmount);

            EarlyPayment earlyPayment = earlyPayments.get(i);
            if (earlyPayment != null) {
                additionalPaymentAmount = earlyPayment.getAmount();
            }

            if (i + 1 == loan.getTerm()) {
                principalAmount = loanBalance;
            } else {
                principalAmount = (monthlyPaymentAmount.subtract(interestAmount))
                                    .add(additionalPaymentAmount)
                                    .setScale(2, RoundingMode.HALF_UP);
            }

            paymentAmount = interestAmount.add(principalAmount);

            payments.add(MonthlyPayment.builder()
                            .interestPaymentAmount(interestAmount)
                            .debtPaymentAmount(principalAmount)
                            .paymentAmount(paymentAmount)
                            .loanBalanceAmount(loanBalance)
                            .monthNumber(i)
                            .additionalPaymentAmount(additionalPaymentAmount)
                            .build());

            loanBalance = loanBalance.subtract(principalAmount);

            if (earlyPayment != null && earlyPayment.getStrategy() == EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT) {
                    BigDecimal additionalPaymentsWithRemainingLoanBalance = getTotalAmountOfEarlyPaymentsWithLoanBalanceUntilPayment(loan, loanBalance, i);

                    monthlyPaymentAmount = getMonthlyPaymentAmount(additionalPaymentsWithRemainingLoanBalance, monthlyInterestRate, term - 1 - i );
            }
        }

        LoanAmortization result = amortizationBuilder
                .monthlyPayments(Collections.unmodifiableList(payments))
                .overPaymentAmount(overPaidInterestAmount)
                .earlyPayments(earlyPayments)
                .build();

        logger.info("Calculation result: " + result);

        return result;
    }

    /**
     * Implements early payment repeating strategy
     *
     * @param loan loan attributes
     */
    // TODO Refactor
    private Loan prepareRepeatableEarlyPayments(Loan loan) {
        Map<Integer, EarlyPayment> newEarlyPayments = new HashMap<>();
        if (loan.getEarlyPayments() != null) {
            Set<Map.Entry<Integer, EarlyPayment>> payments = loan.getEarlyPayments().entrySet();
            newEarlyPayments = new HashMap<>(loan.getEarlyPayments());

            for (Map.Entry<Integer, EarlyPayment> entry : payments) {
                EarlyPayment earlyPayment = entry.getValue();

                if (earlyPayment.getRepeatingStrategy() == EarlyPaymentRepeatingStrategy.TO_END) {
                    logger.info("Repeating strategy " + EarlyPaymentRepeatingStrategy.TO_END + "\n Repeating the payment: " + earlyPayment);

                    for (int i = entry.getKey() + 1; i < loan.getTerm(); i++) {
                        newEarlyPayments.put(i, new EarlyPayment(
                                earlyPayment.getAmount(),
                                earlyPayment.getStrategy(),
                                EarlyPaymentRepeatingStrategy.SINGLE,
                                null
                        ));
                    }
                    // Strategy implemented - stop the cycle
                    break;

                } else if (earlyPayment.getRepeatingStrategy() == EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH) {
                    logger.info("Repeating strategy " + EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH + "\n Repeating the payment: " + earlyPayment);

                    int repeatTo = Integer.parseInt(earlyPayment.getAdditionalParameters().get(EarlyPaymentAdditionalParameters.REPEAT_TO_MONTH_NUMBER));
                    for (int i = entry.getKey() + 1; i < repeatTo; i++) {
                        newEarlyPayments.put(i, new EarlyPayment(
                                earlyPayment.getAmount(),
                                earlyPayment.getStrategy(),
                                EarlyPaymentRepeatingStrategy.SINGLE,
                                null
                        ));
                    }
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
                .build();
    }

    /**
     * Calculates total amount of early payments with strategy {@link EarlyPaymentStrategy#DECREASE_TERM}
     * until certain payment number in the schedule + remaining loan balance
     *
     * This method is used for right calculation of amortization when there are different kinds of additional payments
     * and we need to include this amount in calculation of monthly payment amount
     *
     * @param loan loan attributes
     * @param loanBalance current loan balance
     * @param untilThisPayment current payment number
     *
     * @return total amount of early payments + remaining loan balance
     */
    private BigDecimal getTotalAmountOfEarlyPaymentsWithLoanBalanceUntilPayment(Loan loan, BigDecimal loanBalance, int untilThisPayment) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<Integer, EarlyPayment> entry : loan.getEarlyPayments().entrySet()) {
            if (entry.getKey() < untilThisPayment && entry.getValue().getStrategy() == EarlyPaymentStrategy.DECREASE_TERM)  {
                totalAmount = totalAmount.add(entry.getValue().getAmount());
            }
        }
        totalAmount = loanBalance.add(totalAmount);
        logger.info("Calculating total amount of early payments(decrease term strategy) with remaining loan balance:{}, until payment number: {}\n Result: {}",
                loanBalance, untilThisPayment, totalAmount);

        return totalAmount;
    }

    /**
     * Calculates monthly payment amount
     *
     * @param amount loan balance
     * @param rate monthly interest rate
     * @param term loan term in months
     *
     * @return monthly payment amount
     */
    private BigDecimal getMonthlyPaymentAmount(BigDecimal amount, BigDecimal rate, Integer term) {
        logger.info("Calculating monthly payment amount for: {}, {}, {}", amount, rate, term);

        BigDecimal monthlyPaymentAmount =
                amount.multiply(((rate
                                    .multiply(BigDecimal.ONE.add(rate).pow(term)))
                                .divide((BigDecimal.ONE.add(rate).pow(term).subtract(BigDecimal.ONE)), 15, RoundingMode.HALF_UP)
                        )
                ).setScale(2, RoundingMode.HALF_UP);

        logger.info("Calculate monthly payment amount: " + amount);
        return monthlyPaymentAmount;
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
}


