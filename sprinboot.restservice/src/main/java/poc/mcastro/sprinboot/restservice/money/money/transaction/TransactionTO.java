package poc.mcastro.sprinboot.restservice.money.money.transaction;

import org.springframework.data.annotation.Id;
import poc.mcastro.sprinboot.restservice.money.money.account.AccountTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class TransactionTO implements Serializable {

    @Id
    private String id;
    private OffsetDateTime date;
    private AccountTO accountTO;
    private String reason;
    private TransactionStatus transactionStatus;
    private BigDecimal amount;
    private TransactionType type;

    private TransactionTO() {
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

    public OffsetDateTime getDate() {
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

    @Override
    public String toString() {
        return "TransactionTO{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", accountTO=" + accountTO +
                ", reason='" + reason + '\'' +
                ", transactionStatus=" + transactionStatus +
                ", amount=" + amount +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionTO)) return false;
        TransactionTO that = (TransactionTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(date, that.date) &&
                Objects.equals(accountTO, that.accountTO) &&
                Objects.equals(reason, that.reason) &&
                transactionStatus == that.transactionStatus &&
                Objects.equals(amount, that.amount) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, accountTO, reason, transactionStatus, amount, type);
    }

    public static class TransactionBuilder {
        private String id;
        private OffsetDateTime date;
        private AccountTO accountTO;
        private String reason;
        private TransactionStatus transactionStatus;
        private BigDecimal amount;
        private TransactionType transactionType;

        public TransactionBuilder withRandomId() {
            this.id = UUID.randomUUID().toString();
            return this;
        }

        public TransactionBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public TransactionBuilder withDate(OffsetDateTime date) {
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

        public TransactionBuilder withType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public TransactionBuilder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionTO build() {
            final TransactionTO transactionTO = new TransactionTO();
            transactionTO.id = id;
            transactionTO.date = date;
            transactionTO.accountTO = accountTO;
            transactionTO.reason = reason;
            transactionTO.amount = amount;
            transactionTO.transactionStatus = transactionStatus;
            transactionTO.type = transactionType;
            return transactionTO;
        }
    }
}
