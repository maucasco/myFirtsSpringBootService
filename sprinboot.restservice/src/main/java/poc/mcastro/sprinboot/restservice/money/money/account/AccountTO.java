package poc.mcastro.sprinboot.restservice.money.money.account;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigDecimal;

public class AccountTO implements Serializable {
    @Id
    private String id;

    private String accountNumber;
    private String customerName;
    private BigDecimal accountBalance;

    private AccountTO() {
    }

    public AccountTO(String id, String accountNumber, String customerName, BigDecimal accountBalance) {
        this.accountNumber = accountNumber;
        this.id = id;
        this.customerName = customerName;
        this.accountBalance = accountBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AccountTO{" +
                "id='" + id + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", accountBalance=" + accountBalance +
                '}';
    }
}
