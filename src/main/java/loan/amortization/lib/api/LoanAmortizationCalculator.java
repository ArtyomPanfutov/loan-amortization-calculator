package loan.amortization.lib.api;

import loan.amortization.lib.dto.Loan;
import loan.amortization.lib.dto.LoanAmortization;

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
