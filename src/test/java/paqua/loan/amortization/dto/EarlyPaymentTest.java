package paqua.loan.amortization.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import paqua.loan.amortization.utils.factory.EarlyPaymentFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EarlyPaymentTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void shouldMatchSerializedAndDeserializedObjects() throws JsonProcessingException {
        final double amount = 24424.54;

        EarlyPayment earlyPayment = EarlyPaymentFactory.createSingleWithDecreasePaymentAmountStrategy(amount);

        String serialized = OBJECT_MAPPER.writeValueAsString(earlyPayment);
        EarlyPayment deserialized = OBJECT_MAPPER.readValue(serialized, EarlyPayment.class);

        assertEquals(deserialized, earlyPayment);
        assertEquals(deserialized.hashCode(), earlyPayment.hashCode());
    }
}