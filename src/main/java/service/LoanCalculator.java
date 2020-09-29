package service;

import domain.Loan;
import domain.LoanAmortization;
import domain.MonthlyPayment;
import exception.ExceptionType;
import exception.LoanAmortizationCalculatorException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    // TODO Refactor
    @Override
    public LoanAmortization calculate(Loan loan) {
        verify(loan);

        LoanAmortization.LoanAmortizationBuilder builder = LoanAmortization.builder();
        List<MonthlyPayment> payments = new ArrayList<>();

        BigDecimal monthInterestRate = loan.getRate()
                                                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                                                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        BigDecimal paymentAmount = loan.getAmount().multiply(
                ((monthInterestRate.multiply(BigDecimal.ONE.add(monthInterestRate).pow(loan.getTerm())))
                        .divide((BigDecimal.ONE.add(monthInterestRate).pow(loan.getTerm()).subtract(BigDecimal.ONE)), 6, RoundingMode.HALF_UP)
                )
        );
        builder.monthlyPaymentAmount(paymentAmount);

        BigDecimal loanBalance = loan.getAmount();
        BigDecimal interestRate = loan.getRate();

        for (int i = 0; i < loan.getTerm(); i++) {


            BigDecimal interestAmount = loanBalance.multiply(monthInterestRate);
            BigDecimal principalAmount = paymentAmount.subtract(interestAmount);

            paymentAmount = interestAmount.add(principalAmount);
            loanBalance = loanBalance.subtract(principalAmount);

            payments.add(MonthlyPayment.builder()
                            .interestPaymentAmount(interestAmount)
                            .debtPaymentAmount(principalAmount)
                            .loanBalanceAmount(loanBalance) // TODO
                            .monthNumber(i)
                            .build()
            );
        }
        return builder
                .monthlyPayments(Collections.unmodifiableList(payments))
                .build();
    }

    private void verify(Loan loan) {
        if (loan == null || loan.getAmount() == null || loan.getRate() == null || loan.getTerm() == null) {
            throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.NULL.getMessageText());
        }
    }
}


