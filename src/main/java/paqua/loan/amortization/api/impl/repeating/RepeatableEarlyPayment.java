package paqua.loan.amortization.api.impl.repeating;

import paqua.loan.amortization.dto.EarlyPayment;
import paqua.loan.amortization.dto.Loan;

import java.util.Map;

interface RepeatableEarlyPayment {
    /**
     * Copies early payment
     *
     * @param loan loan
     * @param startNumber the number of the early payment from which will be the first
     * @param earlyPayment source payment to copy
     */
    Map<Integer, EarlyPayment> getRepeated(final Loan loan, final int startNumber, final EarlyPayment earlyPayment);
}
