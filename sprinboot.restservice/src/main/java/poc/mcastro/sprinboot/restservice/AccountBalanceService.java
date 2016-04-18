package poc.mcastro.sprinboot.restservice;

import java.math.BigDecimal;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import poc.mcastro.sprinboot.restservice.model.AccountTO;
import poc.mcastro.sprinboot.restservice.model.GetMoneyTO;
import poc.mcastro.sprinboot.restservice.model.SendMoneyTO;
import poc.mcastro.sprinboot.restservice.model.Status;
import poc.mcastro.sprinboot.restservice.model.TransactionTO;
import poc.mcastro.sprinboot.restservice.model.TransactionType;
import poc.mcastro.sprinboot.restservice.repository.AccountsRepository;
import poc.mcastro.sprinboot.restservice.repository.TransactionRepository;

@RestController
@RequestMapping("/balance")
public class AccountBalanceService {
	@Autowired
	AccountsRepository accountRepository;
	@Autowired
	TransactionRepository transactionRepository;

	@RequestMapping(value = "addMoney", method = RequestMethod.PUT)
	public void addMoney(SendMoneyTO sendMoneyTO) {
		AccountTO accountTO = accountRepository.findByAccountNumber(sendMoneyTO.getAccountTO().getAccountNumber());
		if (accountTO == null) {
			throw new EmptyResultDataAccessException(1);

		}
		accountTO.getAccountBalance().add(sendMoneyTO.getAmmount());
		accountRepository.save(accountTO);
	}

	@RequestMapping(value = "getMoney", method = RequestMethod.PUT)
	public void getMoney(GetMoneyTO getMoneyTO) {

		TransactionTO to = new TransactionTO();
		try {
			AccountTO accountTO = accountRepository.findByAccountNumber(getMoneyTO.getAccountTO().getAccountNumber());
			if (accountTO == null) {
				throw new EmptyResultDataAccessException(1);

			}

			to.setAccountTO(accountTO);
			to.setDate(Calendar.getInstance());
			to.setReason(getMoneyTO.getReason());
			to.setTransactionType(TransactionType.DEBIT);

			if (getMoneyTO.getAmmount().compareTo(accountTO.getAccountBalance()) > 0) {
				to.setStatus(Status.REJECTED);
				throw new Exception("insufficient funds");
			} else {
				accountTO.getAccountBalance().subtract(getMoneyTO.getAmmount());

				accountRepository.save(accountTO);

			}

		} catch (Exception e) {
			// TODO Ask Fabian that logger??
			e.printStackTrace();
			to.setStatus(Status.FAILED);
		} finally {
			transactionRepository.save(to);
		}

	}

	@RequestMapping(value = "getAccountBalance", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public BigDecimal getAccountBalance(String accountNumber) {
		AccountTO accountTO = accountRepository.findByAccountNumber(accountNumber);
		if (accountTO == null) {
			throw new EmptyResultDataAccessException(1);

		}
		return accountTO.getAccountBalance();
	}

	@RequestMapping(value = "getAccount", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public AccountTO getAccount(String accountNumber) {
		AccountTO accountTO = accountRepository.findByAccountNumber(accountNumber);
		if (accountTO == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return accountRepository.findByAccountNumber(accountNumber);

	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = { EmptyResultDataAccessException.class })
	public void notFound() {

	}
}
