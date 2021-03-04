/*
 * MIT License
 *
 * Copyright (c) 2021 Artyom Panfutov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package paqua.loan.amortization.api.impl;

import paqua.loan.amortization.api.LoanAmortizationCalculator;
import paqua.loan.amortization.api.impl.annual.AnnualPaymentLoanCalculator;
import paqua.loan.amortization.api.impl.message.Messages;
import paqua.loan.amortization.api.impl.repeating.EarlyPaymentRepeatingStrategy;
import paqua.loan.amortization.exception.ExceptionType;
import paqua.loan.amortization.exception.LoanAmortizationCalculatorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import paqua.loan.amortization.dto.EarlyPayment;
import paqua.loan.amortization.dto.Loan;
import paqua.loan.amortization.dto.LoanAmortization;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of loan amortization calculator
 *
 * Calculates annual amortization schedule
 *
 * @author Artyom Panfutov
 */
public class LoanAmortizationCalculatorImpl implements LoanAmortizationCalculator {
    private static final Logger LOGGER = LogManager.getLogger(LoanAmortizationCalculatorImpl.class);
    private static final LoanAmortizationCalculator ANNUAL_PAYMENT_LOAN_AMORTIZATION_CALCULATOR = new AnnualPaymentLoanCalculator();

    /**
     * Calculates annual loan amortization schedule
     *
     * @return Calculated loan amortization schedule {@link LoanAmortization}
     */
    @Override
    public LoanAmortization calculate(Loan inputLoan) {
        validate(inputLoan);

        return ANNUAL_PAYMENT_LOAN_AMORTIZATION_CALCULATOR.calculate(
                getLoanWithImplementedEarlyPaymentStrategy(inputLoan)
        );
    }

    /**
     * Implements the first found early payment repeating strategy
     *
     * Iterates through early payments entries and implements first found repeating strategy
     * @return new loan with filled early payment list (according to a repeating strategy)
     */
    private Loan getLoanWithImplementedEarlyPaymentStrategy(Loan loan) {
        final Map<Integer, EarlyPayment> allEarlyPayments = new HashMap<>();

        if (loan.getEarlyPayments() != null) {
            final Set<Map.Entry<Integer, EarlyPayment>> payments = loan.getEarlyPayments().entrySet();

            allEarlyPayments.putAll(extractOnlySingleEarlyPayments(loan));

            payments.stream()
                    .filter(p -> p.getValue().getRepeatingStrategy() != EarlyPaymentRepeatingStrategy.SINGLE)
                    .findFirst()
                    // If there are more than one early payments with repeating strategy - we ignore them
                    // This case cannot be supported because it is contradictory - such payments could intersect with each other
                    .ifPresent(p -> allEarlyPayments.putAll(
                            p.getValue().getRepeatingStrategy()
                                    .getRepeated(loan, p.getKey(), p.getValue())
                            )
                    );
        }

        LOGGER.info("After applying repeating strategy: " + allEarlyPayments);
        return Loan.builder()
                .amount(loan.getAmount())
                .earlyPayments(allEarlyPayments)
                .rate(loan.getRate())
                .term(loan.getTerm())
                .firstPaymentDate(loan.getFirstPaymentDate())
                .build();
    }

    private void validate(Loan loan) {
        LOGGER.info("Validating input. Loan: " + loan);

        if (loan == null || loan.getAmount() == null || loan.getRate() == null || loan.getTerm() == null) {
            throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.NULL.getMessageText());
        }

        if (loan.getAmount().compareTo(BigDecimal.ZERO) <= 0 || loan.getTerm() <= 0 || loan.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.NEGATIVE_NUMBER.getMessageText());
        }

        // Early payments
        if (loan.getEarlyPayments() != null) {
            for (Map.Entry<Integer, EarlyPayment> entry :loan.getEarlyPayments().entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.NULL.getMessageText());
                }

                if (entry.getKey() < 0) {
                   throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.EARLY_PAYMENT_NUMBER_IS_NEGATIVE.getMessageText());
                }

                if (entry.getValue().getAmount().compareTo(BigDecimal.ZERO) < 0) {
                    throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.EARLY_PAYMENT_AMOUNT_IS_NEGATIVE.getMessageText());
                }

                if (entry.getValue().getStrategy() == null) {
                    throw new LoanAmortizationCalculatorException(ExceptionType.INPUT_VERIFICATION_EXCEPTION, Messages.EARLY_PAYMENT_STRATEGY_IS_NULL.getMessageText());
                }
            }
        }
        LOGGER.info("Successful validation!");
    }


    private Map<Integer, EarlyPayment> extractOnlySingleEarlyPayments(Loan loan) {
        return loan.getEarlyPayments().entrySet().stream()
                .filter(entry -> entry.getValue().getRepeatingStrategy().equals(EarlyPaymentRepeatingStrategy.SINGLE))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}


