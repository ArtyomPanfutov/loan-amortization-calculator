package paqua.loan.amortization.utils.factory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperFactory {
    public static ObjectMapper create() {
        return new ObjectMapper();
    }
}
