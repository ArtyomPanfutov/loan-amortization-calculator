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
 * Loan amortization calculator
 *
 * @author Artyom Panfutov
 */
public class LoanCalculator implements Calculator {
    private static final Logger logger = LogManager.getLogger(LoanCalculator.class);

    /**
     * Calculate payment
     * @param loan
     * @return Calculated loan amortization
     */
    // TODO Refactor
    @Override
    public LoanAmortization calculate(Loan loan) {
        validate(loan);

        LoanAmortization.LoanAmortizationBuilder builder = LoanAmortization.builder();

        BigDecimal overPaidInterestAmount = BigDecimal.ZERO;
        List<MonthlyPayment> payments = new ArrayList<>();
        Map<Integer, EarlyPayment> earlyPayments = loan.getEarlyPayments() != null ? loan.getEarlyPayments() : Collections.EMPTY_MAP;
        BigDecimal loanBalance = loan.getAmount();

        BigDecimal monthlyInterestRate = loan.getRate()
                                                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP)
                                                .divide(BigDecimal.valueOf(12), 15, RoundingMode.HALF_UP);

        BigDecimal monthlyPaymentAmount = getMonthlyPaymentAmount(loan.getAmount(), monthlyInterestRate, loan.getTerm());

        builder.monthlyPaymentAmount(monthlyPaymentAmount);

        int term = loan.getTerm();

        // Calculate payments schedule
        for (int i = 0; i < term; i++) {
            BigDecimal principalAmount;
            BigDecimal paymentAmount;
            BigDecimal additionalPaymentAmount = BigDecimal.ZERO;

            BigDecimal interestAmount = loanBalance.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);

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
                    BigDecimal decreaseTermEarlyPaymentSum = getEarlyPaymentSumBefore(loan, loanBalance, i, EarlyPaymentStrategy.DECREASE_TERM);
                    logger.info("Previous early payments(decrease term strategy): " + decreaseTermEarlyPaymentSum);

                    monthlyPaymentAmount = getMonthlyPaymentAmount(decreaseTermEarlyPaymentSum, monthlyInterestRate, term - 1 - i );
            }
        }

        LoanAmortization result = builder
                .monthlyPayments(Collections.unmodifiableList(payments))
                .overPaymentAmount(overPaidInterestAmount)
                .earlyPayments(earlyPayments)
                .build();

        logger.info("Calculation result: " + result);

        return result;
    }

    private BigDecimal getEarlyPaymentSumBefore(Loan loan, BigDecimal loanBalance, int beforePaymentNumber, EarlyPaymentStrategy strategy) {
        BigDecimal previousEarlyPaymentSum = BigDecimal.ZERO;

        for (Map.Entry<Integer, EarlyPayment> entry : loan.getEarlyPayments().entrySet()) {
            if (entry.getKey() < beforePaymentNumber && entry.getValue().getStrategy() == strategy)  {
                previousEarlyPaymentSum = previousEarlyPaymentSum.add(entry.getValue().getAmount());
            }
        }
        previousEarlyPaymentSum = loanBalance.add(previousEarlyPaymentSum);
        return previousEarlyPaymentSum;
    }

    /**
     * Calculate monthly payment amount
     * @param amount
     * @param rate
     * @param term
     * @return
     */
    private BigDecimal getMonthlyPaymentAmount(BigDecimal amount, BigDecimal rate, Integer term) {
        logger.info("Calculating monthly payment amount for: {}, {}, {}", amount, rate, term);

        BigDecimal monthlyPaymentAmount = amount
                .multiply(((rate
                                .multiply(BigDecimal.ONE.add(rate).pow(term))
                        ).divide((BigDecimal.ONE.add(rate).pow(term).subtract(BigDecimal.ONE)), 15, RoundingMode.HALF_UP)
                        )
                ).setScale(2, RoundingMode.HALF_UP);

        logger.info("Calculate monthly payment amount: " + amount);
        return monthlyPaymentAmount;
    }

    /**
     * Validates input loan
     * @param loan
     */
    // TODO Need to make message more clear about what property is not valid
    //      Also this method is not readable :(
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


