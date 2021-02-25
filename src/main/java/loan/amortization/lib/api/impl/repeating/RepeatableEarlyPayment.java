package loan.amortization.lib.api.impl.repeating;

import loan.amortization.lib.dto.EarlyPayment;
import loan.amortization.lib.dto.Loan;

import java.util.Map;

interface RepeatableEarlyPayment {
    /**
     * Copies early payment
     *
     * @param loan loan
     * @param startNumber the number of the early payment from which will be the first
     * @param earlyPayment source payment to copy
     */
    Map<Integer, EarlyPayment> repeat(final Loan loan, final int startNumber, final EarlyPayment earlyPayment);
}
