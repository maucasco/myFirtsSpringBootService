package poc.mcastro.sprinboot.restservice.money.money.transaction;

import org.springframework.data.annotation.Id;
import poc.mcastro.sprinboot.restservice.money.money.account.AccountTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

public class TransactionTO implements Serializable {

    @Id
    private String id;
    private Calendar date;
    private AccountTO accountTO;
    private String reason;
    private TransactionStatus transactionStatus;
    private BigDecimal ammount;
    private TransactionType transactionType;

    private TransactionTO() {
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmmount() {
        return ammount;
    }

    public String getId() {
        return id;
    }

    public Calendar getDate() {
        return date;
    }

    public AccountTO getAccountTO() {
        return accountTO;
    }

    public String getReason() {
        return reason;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public static class TransactionBuilder {
        private String id;
        private Calendar date;
        private AccountTO accountTO;
        private String reason;
        private TransactionStatus transactionStatus;
        private BigDecimal ammount;
        private TransactionType transactionType;

        public TransactionBuilder withRandomId() {
            this.id = UUID.randomUUID().toString();
            return this;
        }

        public TransactionBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public TransactionBuilder withDate(Calendar date) {
            this.date = date;
            return this;
        }

        public TransactionBuilder withTransactionStatus(TransactionStatus transactionStatus) {
            this.transactionStatus = transactionStatus;
            return this;
        }

        public TransactionBuilder withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public TransactionBuilder withAccount(AccountTO accountTO) {
            this.accountTO = accountTO;
            return this;
        }

        public TransactionBuilder withTransactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public TransactionBuilder setAmmount(BigDecimal ammount) {
            this.ammount = ammount;
            return this;
        }

        public TransactionTO build() {
            final TransactionTO transactionTO = new TransactionTO();
            transactionTO.id = id;
            transactionTO.date = date;
            transactionTO.accountTO = accountTO;
            transactionTO.reason = reason;
            transactionTO.ammount = ammount;
            transactionTO.transactionStatus = transactionStatus;
            transactionTO.transactionType = transactionType;
            return transactionTO;
        }
    }
}
