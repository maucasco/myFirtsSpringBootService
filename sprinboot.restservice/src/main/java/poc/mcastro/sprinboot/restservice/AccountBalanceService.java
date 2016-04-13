package poc.mcastro.sprinboot.restservice;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import poc.mcastro.sprinboot.restservice.model.AccountTO;
import poc.mcastro.sprinboot.restservice.model.SendMoneyTO;
import poc.mcastro.sprinboot.restservice.repository.AccountsRepository;

@RestController
@RequestMapping("/balance")
public class AccountBalanceService {
	@Autowired 
	AccountsRepository accountRepository;
	
		
	@RequestMapping(value="addMoney", method = RequestMethod.POST)
	public void addMoney(SendMoneyTO sendMoneyTO){
		AccountTO accountTO=accountRepository.findByAccountNumber(sendMoneyTO.getAccountTO().getAccountNumber());
		if(accountTO==null){
			throw new EmptyResultDataAccessException(1);
			
		}
		accountTO.getAccountBalance().add(sendMoneyTO.getAmmount());
		accountRepository.save(accountTO);
	}
	@RequestMapping(value="getAccountBalance", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public BigDecimal getAccountBalance(String accountNumber){
		AccountTO accountTO=accountRepository.findByAccountNumber(accountNumber);
		if(accountTO==null){
			throw new EmptyResultDataAccessException(1);
			
		}
		return accountTO.getAccountBalance();
	}
	
	@RequestMapping(value="getAccount", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public AccountTO getAccount(String accountNumber){
		AccountTO accountTO=accountRepository.findByAccountNumber(accountNumber);
		if(accountTO==null){
			throw new EmptyResultDataAccessException(1);
		}
		return accountRepository.findByAccountNumber(accountNumber);
		
		  
	}
	   @ResponseStatus(HttpStatus.BAD_REQUEST)
 @ExceptionHandler(value = {EmptyResultDataAccessException.class})
	public void notFound(){
		
	}
}
