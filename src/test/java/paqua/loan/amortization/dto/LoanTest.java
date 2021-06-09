package paqua.loan.amortization.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import paqua.loan.amortization.utils.factory.LoanFactory;
import paqua.loan.amortization.utils.factory.ObjectMapperFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanTest {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.create();

    @Test
    void shouldMatchSerializedAndDeserializedObjects() throws JsonProcessingException {
        Loan loan = LoanFactory.getDefaultWithEarlyPayments();

        String serialized = OBJECT_MAPPER.writeValueAsString(loan);
        Loan deserialized = OBJECT_MAPPER.readValue(serialized, Loan.class);

        assertEquals(deserialized, loan);
        assertEquals(deserialized.hashCode(), loan.hashCode());
    }

	@Test
	public void shouldTakeDoubleValuesForLoan(){
		Loan loan = Loan.builder()
				.amount(500000.32)
				.rate(4.56)
				.term(10)
				.build();

		assertEquals(loan.getAmount(), BigDecimal.valueOf(500000.32));
		assertEquals(loan.getRate(), BigDecimal.valueOf(4.56));


	}

}
