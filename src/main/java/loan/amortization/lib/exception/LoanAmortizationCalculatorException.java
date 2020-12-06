package loan.amortization.lib.exception;

/**
 * Custom runtime exception for this lib
 * Contains {@link ExceptionType} that might help to classify an error
 *
 * @author Artyom Panfutov
 */
public class LoanAmortizationCalculatorException extends RuntimeException {
    private static final long serialVersionUID = -7709387093821627328L;

    private final ExceptionType type;

    public LoanAmortizationCalculatorException(ExceptionType type, String message) {
        super(message);
        this.type = type;
    }

    @Override
    public String toString() {
        return "LoanAmortizationCalculatorException{" +
                "type=" + type +
                '}';
    }
}
