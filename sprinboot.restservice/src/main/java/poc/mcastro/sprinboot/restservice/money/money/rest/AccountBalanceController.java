package poc.mcastro.sprinboot.restservice.money.money.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poc.mcastro.sprinboot.restservice.money.core.DataNotFoundException;
import poc.mcastro.sprinboot.restservice.money.core.InsuficientFundsException;
import poc.mcastro.sprinboot.restservice.money.money.account.AccountTO;
import poc.mcastro.sprinboot.restservice.money.money.account.AccountsRepository;
import poc.mcastro.sprinboot.restservice.money.money.account.GetMoneyTO;
import poc.mcastro.sprinboot.restservice.money.money.account.SendMoneyTO;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionRepository;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionStatus;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionTO;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@RestController
@RequestMapping("/balance")
public class AccountBalanceController {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AccountsRepository accountRepository;

    @RequestMapping(value = "addMoney", method = RequestMethod.POST)
    public TransactionTO transferMoneyToAccount(@RequestBody SendMoneyTO sendMoneyTO) {
        final Optional<AccountTO> maybeAccount = Optional
                .ofNullable(accountRepository.findByAccountNumber(sendMoneyTO.getAccountTO().getAccountNumber()));

        final TransactionTO.TransactionBuilder transactionBuilder = maybeAccount
                .map(accountTO ->
                            new TransactionTO.TransactionBuilder()
                                    .withRandomId()
                                    .withAccount(accountTO)
                                    .withTransactionStatus(TransactionStatus.APPROVED)
                                    .withDate(OffsetDateTime.now(ZoneOffset.UTC))
                                    .withReason("Add Money")
                                    .withType(TransactionType.CREDIT)

                ).orElseThrow(() -> new DataNotFoundException("account nof found"));

        final TransactionTO transactionTO = transactionBuilder.build();
        transactionTO.getAccountTO().setAccountBalance(transactionTO.getAccountTO().getAccountBalance().add(sendMoneyTO.getAmmount()));
        accountRepository.save(transactionTO.getAccountTO());
        transactionRepository.save(transactionTO);
        return transactionTO;
    }

    @RequestMapping(value = "debitMoney", method = RequestMethod.POST)
    public TransactionTO transferMoneyBetweenAccounts(@RequestBody GetMoneyTO getMoneyTO) {
        final Optional<AccountTO> maybeAccount = Optional
                .ofNullable(accountRepository.findByAccountNumber(getMoneyTO.getAccountTO().getAccountNumber()));

        final TransactionTO.TransactionBuilder transactionBuilder = maybeAccount
                .map(accountTO ->
                        new TransactionTO.TransactionBuilder()
                                .withRandomId()
                                .withAccount(accountTO)
                                .withDate(OffsetDateTime.now(ZoneOffset.UTC))
                                .withReason(getMoneyTO.getReason())
                                .withType(TransactionType.DEBIT)
                ).orElseThrow(() -> new DataNotFoundException("account nof found"));

        if (getMoneyTO.transferAmountLowerThanAccountBalance()) {
            transactionBuilder.withTransactionStatus(TransactionStatus.REJECTED);
            transactionRepository.save(transactionBuilder.build());
            throw new InsuficientFundsException("insufficient funds");
        } else {
            final TransactionTO transactionTO = transactionBuilder.build();
            final BigDecimal newAmount = transactionTO.getAccountTO().getAccountBalance().subtract(getMoneyTO.getAmount());
            transactionTO.getAccountTO().setAccountBalance(newAmount);
            accountRepository.save(transactionTO.getAccountTO());
            transactionRepository.save(transactionTO);
            return transactionTO;
        }
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
