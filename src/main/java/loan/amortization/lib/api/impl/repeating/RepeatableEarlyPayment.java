package loan.amortization.lib.api.impl.repeating;

import loan.amortization.lib.dto.EarlyPayment;
import loan.amortization.lib.dto.Loan;

import java.util.Map;

interface RepeatableEarlyPayment {
    /**
     *
     * @param allEarlyPayments destination map for fill
     * @param loan loan
     * @param startNumber the number of the early payment from which start to fill
     * @param earlyPayment source payment to copy
     */
    void fillEarlyPayments(Map<Integer, EarlyPayment> allEarlyPayments, Loan loan, int startNumber, EarlyPayment earlyPayment);
}
