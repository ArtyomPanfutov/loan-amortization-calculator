package service;

import domain.Loan;
import domain.LoanAmortization;

public interface Calculator {
    public LoanAmortization calculate(Loan loan);
}
