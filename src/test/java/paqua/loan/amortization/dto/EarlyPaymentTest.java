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
package paqua.loan.amortization.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import paqua.loan.amortization.api.impl.repeating.EarlyPaymentRepeatingStrategy;
import paqua.loan.amortization.utils.factory.EarlyPaymentFactory;
import paqua.loan.amortization.utils.factory.ObjectMapperFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EarlyPaymentTest {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.create();

    @Test
    void shouldMatchSerializedAndDeserializedObjects() throws JsonProcessingException {
        final double amount = 24424.54;

        EarlyPayment earlyPayment = EarlyPaymentFactory.createSingleWithDecreasePaymentAmountStrategy(amount);

        String serialized = OBJECT_MAPPER.writeValueAsString(earlyPayment);
        EarlyPayment deserialized = OBJECT_MAPPER.readValue(serialized, EarlyPayment.class);

        assertEquals(earlyPayment, deserialized);
        assertEquals(earlyPayment.hashCode(), deserialized.hashCode());
    }

    @Test
    void shouldSetAllWithBuilder() {
        BigDecimal amount = BigDecimal.valueOf(1000000.22);
        int repeatTo = 12;
        EarlyPaymentStrategy strategy = EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT;
        EarlyPaymentRepeatingStrategy repeatingStrategy = EarlyPaymentRepeatingStrategy.TO_END;

        EarlyPayment earlyPayment = EarlyPayment.builder()
                .amount(amount)
                .repeatTo(repeatTo)
                .repeatingStrategy(repeatingStrategy)
                .strategy(strategy)
                .build();

        assertEquals(amount, earlyPayment.getAmount());
        assertEquals(strategy, earlyPayment.getStrategy());
        assertEquals(repeatingStrategy, earlyPayment.getRepeatingStrategy());
        assertEquals(String.valueOf(repeatTo), earlyPayment.getAdditionalParameters().get(EarlyPaymentAdditionalParameters.REPEAT_TO_MONTH_NUMBER));
    }
}