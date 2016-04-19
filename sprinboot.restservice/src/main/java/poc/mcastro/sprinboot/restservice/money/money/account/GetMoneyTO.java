package poc.mcastro.sprinboot.restservice.money.money.account;

import java.io.Serializable;
import java.math.BigDecimal;

public class GetMoneyTO implements Serializable {
    private AccountTO accountTO;
    private BigDecimal amount;
    private String reason;

    private GetMoneyTO() {
    }

    public AccountTO getAccountTO() {
        return accountTO;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    public String getReason() {
        return reason;
    }


    public boolean transferAmountLowerThanAccountBalance() {
        return getAmount().compareTo(accountTO.getAccountBalance()) > 0;
    }

    public static class GetMoneyBuilder {
        private AccountTO accountTO;
        private BigDecimal ammount;
        private String reason;

        public GetMoneyBuilder withAccountTO(AccountTO accountTO) {
            this.accountTO = accountTO;
            return this;
        }

        public GetMoneyBuilder withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public GetMoneyBuilder withAmount(BigDecimal amount) {
            this.ammount = amount;
            return this;
        }

        public GetMoneyTO build() {
            final GetMoneyTO getMoneyTO = new GetMoneyTO();
            getMoneyTO.accountTO = accountTO;
            getMoneyTO.amount = ammount;
            getMoneyTO.reason = reason;
            return getMoneyTO;
        }
    }
}
