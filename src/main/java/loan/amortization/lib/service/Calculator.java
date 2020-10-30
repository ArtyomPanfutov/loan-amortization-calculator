package loan.amortization.lib.service;

import loan.amortization.lib.domain.Loan;
import loan.amortization.lib.domain.LoanAmortization;

public interface Calculator {
    LoanAmortization calculate(Loan loan);
}
