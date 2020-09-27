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

        BigDecimal paymentAmount = loan.getAmount().divide(BigDecimal.valueOf(loan.getTerm()), 15, RoundingMode.HALF_UP);
        builder.monthlyPaymentAmount(paymentAmount);

        BigDecimal loanBalance = loan.getAmount();
        BigDecimal interestRate = loan.getRate();//.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        for (int i = 0; i < loan.getTerm(); i++) {
//            BigDecimal interestAmount = loanBalance.multiply( ((interestRate).multiply(BigDecimal.valueOf(30)))
//                                                                    .divide((BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(366))), 15, RoundingMode.HALF_UP)); // TODO
            BigDecimal interestAmount = (interestRate
                                            .divide(BigDecimal.valueOf(100), 15,RoundingMode.HALF_UP)
                                            .divide(BigDecimal.valueOf(366), 15, RoundingMode.HALF_UP)
                                            .multiply(BigDecimal.valueOf(31)))
                                        .multiply(loanBalance);


            BigDecimal principalAmount = paymentAmount.subtract(interestAmount);

            paymentAmount = interestAmount.add(principalAmount);
            loanBalance = loanBalance.subtract(paymentAmount);

            payments.add(MonthlyPayment.builder()
                            .interestPaymentAmount(interestAmount)
                            .debtPaymentAmount(principalAmount)
                            .loanBalanceAmount(loanBalance)
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


