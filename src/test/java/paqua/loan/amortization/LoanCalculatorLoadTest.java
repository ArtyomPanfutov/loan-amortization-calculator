package paqua.loan.amortization;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.domain.TestMappings;
import org.jsmart.zerocode.jupiter.extension.ParallelLoadExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({ParallelLoadExtension.class})
class LoanCalculatorLoadTest {
    @Test
    @LoadWith("load.properties")
    @TestMappings({
            @TestMapping(testClass = LoanAmortizationCalculatorTest.class, testMethod = "shouldCalculateWithAllKindsOfEarlyPayment"),
            @TestMapping(testClass = LoanAmortizationCalculatorTest.class, testMethod = "shouldCalculateWithOneEarlyPaymentAndTermDecreasingStrategy")
    })
    void loadTest() {

    }
}
