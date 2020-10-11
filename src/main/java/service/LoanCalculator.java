package service;

import domain.*;
import exception.ExceptionType;
import exception.LoanAmortizationCalculatorException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Loan amortization calculator
 * @author Artyom Panfutov
 */
public class LoanCalculator implements Calculator {
    /**
     * Calculate payment
     * @param loan
     * @return Calculated loan amortization
     */
    // TODO Refactor math context
    @Override
    public LoanAmortization calculate(Loan loan) {
        validate(loan);

        LoanAmortization.LoanAmortizationBuilder builder = LoanAmortization.builder();
        List<MonthlyPayment> payments = new ArrayList<>();

        BigDecimal monthlyInterestRate = loan.getRate()
                                                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP)
                                                .divide(BigDecimal.valueOf(12), 15, RoundingMode.HALF_UP);

        BigDecimal monthlyPaymentAmount = loan.getAmount().multiply(
                                          ((monthlyInterestRate.multiply(BigDecimal.ONE.add(monthlyInterestRate).pow(loan.getTerm())))
                                                  .divide((BigDecimal.ONE.add(monthlyInterestRate).pow(loan.getTerm()).subtract(BigDecimal.ONE)), 15, RoundingMode.HALF_UP)
                                          )
                                    );
        builder.monthlyPaymentAmount(monthlyPaymentAmount);

        BigDecimal loanBalance = loan.getAmount();
        BigDecimal overPaidInterestAmount = BigDecimal.ZERO;

        List<EarlyPayment> earlyPayments = loan.getAdditionalPayments() != null ? loan.getAdditionalPayments() : Collections.EMPTY_LIST;
        for (int i = 0; i < loan.getTerm(); i++) {
            BigDecimal interestAmount = loanBalance.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);

            if (interestAmount.compareTo(BigDecimal.ZERO) < 0) {
                int index = i - 1;

                MonthlyPayment payment = payments.get(index);

                payments.set(index, new MonthlyPayment.MonthlyPaymentBuilder()
                        .additionalPaymentAmount(payment.getAdditionalPaymentAmount())
                        .paymentAmount(loanBalance)
                        .debtPaymentAmount(loanBalance.subtract(payment.getInterestPaymentAmount()))
                        .interestPaymentAmount(payment.getInterestPaymentAmount())
                        .loanBalanceAmount(loanBalance.subtract(payment.getInterestPaymentAmount()))
                        .build());

                break;
            }

            overPaidInterestAmount = overPaidInterestAmount.add(interestAmount);
            BigDecimal principalAmount;

            BigDecimal additionalPaymentAmount = BigDecimal.ZERO;
            AtomicReference<EarlyPayment> additionalPayment = new AtomicReference<>();

            final Integer paymentNumber = i;
            // TODO temporary not optimal
            earlyPayments.stream()
                    .filter(payment -> payment.getNumber().equals(paymentNumber))
                    .findFirst()
                    .ifPresent(additionalPayment::set);


            if (additionalPayment.get() != null) {
               additionalPaymentAmount = additionalPayment.get().getAmount();
            }

            BigDecimal paymentAmount;
            if (i + 1 == loan.getTerm()) {
                principalAmount = loanBalance;
                paymentAmount = loanBalance;
            } else {
                principalAmount = (monthlyPaymentAmount.subtract(interestAmount))
                                    .add(additionalPaymentAmount)
                                    .setScale(2, RoundingMode.HALF_UP);

                paymentAmount = interestAmount.add(principalAmount);
            }

            payments.add(MonthlyPayment.builder()
                            .interestPaymentAmount(interestAmount)
                            .debtPaymentAmount(principalAmount)
                            .paymentAmount(paymentAmount)
                            .loanBalanceAmount(loanBalance)
                            .monthNumber(i)
                            .additionalPaymentAmount(additionalPaymentAmount)
                            .build());

            loanBalance = loanBalance.subtract(principalAmount);
        }

        return builder
                .monthlyPayments(Collections.unmodifiableList(payments))
                .overPaymentAmount(overPaidInterestAmount)
                .build();
    }

    /**
     * Validates input loan
     * @param loan
     */
    private void validate(Loan loan) {
        if (loan == null || loan.getAmount() == null || loan.getRate() == null || loan.getTerm() == null) {
            throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.NULL.getMessageText());
        }
    }
}


