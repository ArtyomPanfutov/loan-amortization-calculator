package paqua.loan.amortization.api.impl.interestrate;

import java.math.BigDecimal;

public interface InterestRateInput {
    BigDecimal getBaseInterestRate();
    int getPaymentNumber();
}
