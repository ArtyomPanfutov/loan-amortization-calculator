package paqua.loan.amortization.utils.factory;

import paqua.loan.amortization.api.impl.repeating.EarlyPaymentRepeatingStrategy;
import paqua.loan.amortization.dto.EarlyPayment;
import paqua.loan.amortization.dto.EarlyPaymentStrategy;

import java.math.BigDecimal;
import java.util.Collections;

public class EarlyPaymentFactory {
    public static EarlyPayment createSingleWithDecreasePaymentAmountStrategy(double amount) {
        return new EarlyPayment(
                BigDecimal.valueOf(amount),
                EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT,
                EarlyPaymentRepeatingStrategy.SINGLE,
                Collections.emptyMap()
        );
    }
}
