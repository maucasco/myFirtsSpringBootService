package poc.mcastro.sprinboot.restservice.money.core;

public class InsuficientFundsException extends RuntimeException {
    private String message;

    public InsuficientFundsException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
