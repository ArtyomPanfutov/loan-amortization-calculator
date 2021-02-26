package paqua.loan.amortization.api.impl.annual;

import paqua.loan.amortization.api.LoanAmortizationCalculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import paqua.loan.amortization.dto.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the annual payment loan amortization calculator
 *
 * @author Artyom Panfutov
 */
public class AnnualPaymentLoanCalculator implements LoanAmortizationCalculator {
    private static final Logger LOGGER = LogManager.getLogger(AnnualPaymentLoanCalculator.class);

    @Override
    public LoanAmortization calculate(Loan loan) {
        BigDecimal overPaidInterestAmount = BigDecimal.ZERO;

        Map<Integer, EarlyPayment> earlyPayments = loan.getEarlyPayments() != null ? loan.getEarlyPayments() : Collections.EMPTY_MAP;
        BigDecimal loanBalance = loan.getAmount();

        int term = loan.getTerm();

        BigDecimal monthlyInterestRate = getMonthlyInterestRate(loan.getRate());
        BigDecimal monthlyPaymentAmount = getMonthlyPaymentAmount(loanBalance, monthlyInterestRate, term);

        LoanAmortization.LoanAmortizationBuilder amortizationBuilder = LoanAmortization.builder()
                .monthlyPaymentAmount(monthlyPaymentAmount);

        LocalDate paymentDate = loan.getFirstPaymentDate();

        // Calculate amortization schedule
        List<MonthlyPayment> payments = new ArrayList<>();
        for (int i = 0; i < term; i++) {
            BigDecimal principalAmount;
            BigDecimal paymentAmount;
            BigDecimal additionalPaymentAmount = BigDecimal.ZERO;

            BigDecimal interestAmount = calculateInterestAmount(loan, loanBalance, monthlyInterestRate, paymentDate);

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
                            .paymentDate(paymentDate)
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
                    .paymentDate(paymentDate)
                    .build());

            loanBalance = loanBalance.subtract(principalAmount);

            if (earlyPayment != null && earlyPayment.getStrategy() == EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT) {
                BigDecimal additionalPaymentsWithRemainingLoanBalance = getTotalAmountOfEarlyPaymentsWithLoanBalanceUntilPayment(loan, loanBalance, i);

                if (term - 1 - i > 0) {
                    monthlyPaymentAmount = getMonthlyPaymentAmount(additionalPaymentsWithRemainingLoanBalance, monthlyInterestRate, term - 1 - i);
                }
            }

            if (loan.getFirstPaymentDate() != null && paymentDate != null) {
                paymentDate = getNextMonthPaymentDate(loan.getFirstPaymentDate(), paymentDate);
            }
        }

        LoanAmortization result = amortizationBuilder
                .monthlyPayments(Collections.unmodifiableList(payments))
                .overPaymentAmount(overPaidInterestAmount)
                .earlyPayments(earlyPayments)
                .build();

        LOGGER.info("Calculation result: " + result);

        return result;
    }

    private BigDecimal getMonthlyInterestRate(BigDecimal rate) {
        BigDecimal monthlyInterestRate = rate
                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 15, RoundingMode.HALF_UP);

        LOGGER.info("Calculated monthly interest rate: {}", monthlyInterestRate);
        return monthlyInterestRate;
    }

    /**
     * Calculates next payment date
     *
     * @param firstPaymentDate first payment date
     * @param paymentDate payment date
     *
     * @return next payment date
     */
    private LocalDate getNextMonthPaymentDate(LocalDate firstPaymentDate, LocalDate paymentDate) {
        LocalDate nextMonth = paymentDate.plusMonths(1);

        try {
            paymentDate = nextMonth.withDayOfMonth(firstPaymentDate.getDayOfMonth());
        } catch (DateTimeException e) {
            LOGGER.trace("Cannot construct next payment date with the requested day of month. The last month day will be used instead.");
            paymentDate = nextMonth.withDayOfMonth(nextMonth.lengthOfMonth());
        }
        return paymentDate;
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
        LOGGER.info("Calculating total amount of early payments(decrease term strategy) with remaining loan balance:{}, until payment number: {}\n Result: {}",
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
        LOGGER.info("Calculating monthly payment amount for: {}, {}, {}", amount, rate, term);

        BigDecimal monthlyPaymentAmount = getInterestAmountByBalanceAndMonthlyInterestRate(
                amount,
                (rate.multiply(BigDecimal.ONE.add(rate).pow(term)))
                        .divide((BigDecimal.ONE.add(rate).pow(term).subtract(BigDecimal.ONE)), 15, RoundingMode.HALF_UP)
        );

        LOGGER.info("Calculate monthly payment amount: " + amount);
        return monthlyPaymentAmount;
    }

    /**
     * Calculates interest amount
     *
     * @param currentLoanBalance current loan balance
     * @param annualInterestRate interest rate
     * @param daysInMonth days in current month
     * @param daysInYear days in current year
     *
     * @return interest amount
     */
    private BigDecimal getInterestAmountByBalanceRateAndDays(BigDecimal currentLoanBalance, BigDecimal annualInterestRate, int daysInMonth, int daysInYear) {
        return currentLoanBalance.multiply(
                (annualInterestRate.multiply(BigDecimal.valueOf(daysInMonth)))
                        .divide(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(daysInYear)), 15, RoundingMode.HALF_UP)
        ).setScale(2, RoundingMode.HALF_UP);
    }


    /**
     * Calculates interest amount
     *
     * @param currentLoanBalance current loan balance
     * @param monthlyInterestRate calculated monthly interest rate
     *
     * @return interest amount
     */
    private BigDecimal getInterestAmountByBalanceAndMonthlyInterestRate(BigDecimal currentLoanBalance, BigDecimal monthlyInterestRate) {
        return currentLoanBalance
                .multiply(monthlyInterestRate)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates loan balance
     *
     * @param loan loan
     * @param currentLoanBalance current loan balance
     * @param monthlyInterestRate monthly interest rate
     * @param paymentDate current payment date
     *
     * @return interest amount
     */
    private BigDecimal calculateInterestAmount(Loan loan, BigDecimal currentLoanBalance, BigDecimal monthlyInterestRate, LocalDate paymentDate) {
        return paymentDate == null
                ? getInterestAmountByBalanceAndMonthlyInterestRate(currentLoanBalance, monthlyInterestRate)
                : getInterestAmountByBalanceRateAndDays(
                    currentLoanBalance, loan.getRate(),
                    paymentDate.minusMonths(1).lengthOfMonth(),
                    paymentDate.minusMonths(1).lengthOfYear()
                );
    }
}
