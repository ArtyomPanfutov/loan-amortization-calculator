package paqua.loan.amortization.utils.factory;

import paqua.loan.amortization.dto.EarlyPayment;
import paqua.loan.amortization.dto.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class LoanFactory {
    /**
     * Creates a loan with one early payment
     */
    public static Loan getDefaultWithEarlyPayments() {
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();
        earlyPayments.put(3, EarlyPaymentFactory.createSingleWithDecreasePaymentAmountStrategy(2330.4));

        return Loan.builder()
                .amount(BigDecimal.valueOf(10000.00))
                .rate(BigDecimal.valueOf(5.53))
                .term(12)
                .earlyPayments(earlyPayments)
                .firstPaymentDate(LocalDate.now())
                .build();
    }
}
