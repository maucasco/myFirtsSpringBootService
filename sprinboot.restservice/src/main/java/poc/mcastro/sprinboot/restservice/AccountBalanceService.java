package poc.mcastro.sprinboot.restservice;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import poc.mcastro.sprinboot.restservice.model.AccountTO;
import poc.mcastro.sprinboot.restservice.model.SendMoneyTO;
import poc.mcastro.sprinboot.restservice.repository.AccountsRepository;

@RestController
@RequestMapping("/balance")
public class AccountBalanceService {
	@Autowired 
	AccountsRepository accountRepository;
	
		
	@RequestMapping("addMoney")
	public void addMoney(SendMoneyTO sendMoneyTO){
		AccountTO accountTO=accountRepository.findByAccountNumber(sendMoneyTO.getAccountTO().getAccountNumber());
		if(accountTO!=null){
			accountTO.getAccountBalance().add(sendMoneyTO.getAmmount());
			accountRepository.save(accountTO);
		}
	}
	@RequestMapping("getAccountBalance")
	public BigDecimal getAccountBalance(String accountNumber){
		AccountTO accountTO=accountRepository.findByAccountNumber(accountNumber);
		if(accountTO!=null){
			return accountTO.getAccountBalance();
		}
		return null;
	}
	
	@RequestMapping("getAccount")
	public AccountTO getAccount(String accountNumber){
		
		return accountRepository.findByAccountNumber(accountNumber);
	}
}
