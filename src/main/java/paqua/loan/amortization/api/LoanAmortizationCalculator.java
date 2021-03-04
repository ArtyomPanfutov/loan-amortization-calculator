package paqua.loan.amortization.api;

import paqua.loan.amortization.dto.Loan;
import paqua.loan.amortization.dto.LoanAmortization;

/**
 * Loan amortization calculator
 *
 * @author Artyom Panfutov
 */
public interface LoanAmortizationCalculator {

    /**
     * Calculates loan amortization
     *
     * @param loan loan attributes
     * @return calculated loan amortization
     */
    LoanAmortization calculate(Loan loan);
}
