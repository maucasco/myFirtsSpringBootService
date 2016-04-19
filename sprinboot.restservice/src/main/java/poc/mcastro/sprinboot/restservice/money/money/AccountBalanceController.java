package poc.mcastro.sprinboot.restservice.money.money;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poc.mcastro.sprinboot.restservice.money.money.account.AccountTO;
import poc.mcastro.sprinboot.restservice.money.money.account.AccountsRepository;
import poc.mcastro.sprinboot.restservice.money.money.account.GetMoneyTO;
import poc.mcastro.sprinboot.restservice.money.money.account.SendMoneyTO;
import poc.mcastro.sprinboot.restservice.money.core.DataNotFoundException;
import poc.mcastro.sprinboot.restservice.money.core.InsuficientFundsException;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionStatus;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionRepository;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionTO;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Optional;

@RestController
@RequestMapping("/balance")
public class AccountBalanceController {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AccountsRepository accountRepository;

    @RequestMapping(value = "addMoney", method = RequestMethod.PUT)
    public AccountTO addMoney(@RequestBody SendMoneyTO sendMoneyTO) {
        final Optional<AccountTO> maybeAccount = Optional
                .ofNullable(accountRepository.findByAccountNumber(sendMoneyTO.getAccountTO().getAccountNumber()));
        return maybeAccount
                .map(accountTO ->
                        {
                            accountTO.setAccountBalance(accountTO.getAccountBalance().add(sendMoneyTO.getAmmount()));
                            accountRepository.save(accountTO);
                            return accountTO;
                        }
                ).orElseThrow(() -> new DataNotFoundException("account nof found"));
    }

    @RequestMapping(value = "getMoney", method = RequestMethod.PUT)
    public AccountTO getMoney(@RequestBody GetMoneyTO getMoneyTO) {
        final Optional<AccountTO> maybeAccount = Optional
                .ofNullable(accountRepository.findByAccountNumber(getMoneyTO.getAccountTO().getAccountNumber()));
        return maybeAccount
                .map(accountTO ->
                        {
                            TransactionTO to = new TransactionTO();
                            to.setAccountTO(accountTO);
                            to.setDate(Calendar.getInstance());
                            to.setReason(getMoneyTO.getReason());
                            to.setTransactionType(TransactionType.DEBIT);

                            if (getMoneyTO.getAmmount().compareTo(accountTO.getAccountBalance()) > 0) {
                                to.setTransactionStatus(TransactionStatus.REJECTED);
                                throw new InsuficientFundsException("insufficient funds");
                            } else {
                                accountTO.setAccountBalance(accountTO.getAccountBalance().subtract(getMoneyTO.getAmmount()));
                                accountRepository.save(accountTO);
                                transactionRepository.save(to);
                            }
                            return accountTO;
                        }
                ).orElseThrow(() -> new DataNotFoundException("account nof found"));
    }

    @RequestMapping(value = "getAccountBalance", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getAccountBalance(String accountNumber) {
        return Optional.ofNullable(accountRepository
                .findByAccountNumber(accountNumber)).map(AccountTO::getAccountBalance)
                .orElseThrow(() -> new DataNotFoundException("account nof found"));
    }

    @RequestMapping(value = "getAccount", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public AccountTO getAccount(String accountNumber) {
        return Optional.ofNullable(accountRepository
                .findByAccountNumber(accountNumber))
                .orElseThrow(() -> new DataNotFoundException("account nof found"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {EmptyResultDataAccessException.class, InsuficientFundsException.class, DataNotFoundException.class})
    public void badRequest() {
    }
}
