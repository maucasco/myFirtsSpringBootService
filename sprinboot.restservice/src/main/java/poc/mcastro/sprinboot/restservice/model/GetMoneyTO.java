package poc.mcastro.sprinboot.restservice.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class GetMoneyTO implements Serializable {
	private AccountTO accountTO;
	private BigDecimal ammount;
	private String reason;

	public AccountTO getAccountTO() {
		return accountTO;
	}

	public void setAccountTO(AccountTO accountTO) {
		this.accountTO = accountTO;
	}

	public BigDecimal getAmmount() {
		return ammount;
	}

	public void setAmmount(BigDecimal ammount) {
		this.ammount = ammount;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
