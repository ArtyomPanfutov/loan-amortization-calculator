package paqua.loan.amortization.api.impl.annual;

import java.beans.ConstructorProperties;

public final class MonthlyInterestRateInputImpl implements MonthlyInterestRateInput {
    private final int paymentNumber;

    @ConstructorProperties({"paymentNumber"})
    public MonthlyInterestRateInputImpl(int paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    @Override
    public int getPaymentNumber() {
        return paymentNumber;
    }
}
