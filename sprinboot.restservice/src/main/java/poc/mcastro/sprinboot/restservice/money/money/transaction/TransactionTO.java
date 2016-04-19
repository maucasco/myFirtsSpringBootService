package poc.mcastro.sprinboot.restservice.money.money.transaction;

import org.springframework.data.annotation.Id;
import poc.mcastro.sprinboot.restservice.money.money.account.AccountTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

public class TransactionTO implements Serializable {

    @Id
    private String uuid;
    private Calendar date;
    private AccountTO accountTO;
    private String reason;
    private TransactionStatus transactionStatus;
    private BigDecimal ammount;
    private TransactionType transactionType;

    public TransactionTO() {

    }

    public TransactionTO(String uuid, Calendar date, AccountTO accountTO, String reason, TransactionStatus transactionStatus,
                         BigDecimal ammount, TransactionType transactionType) {

        this.uuid = uuid;
        this.date = date;
        this.accountTO = accountTO;
        this.reason = reason;
        this.transactionStatus = transactionStatus;
        this.ammount = ammount;
        this.transactionType = transactionType;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmmount() {
        return ammount;
    }

    public void setAmmount(BigDecimal ammount) {
        this.ammount = ammount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public AccountTO getAccountTO() {
        return accountTO;
    }

    public void setAccountTO(AccountTO accountTO) {
        this.accountTO = accountTO;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

}
