package poc.mcastro.sprinboot.restservice.money.core;

public class DataNotFoundException extends RuntimeException {
    private String message;

    public DataNotFoundException(String message) {
        super(message);
    }

    public String getMessage() {
        return message;
    }

}
