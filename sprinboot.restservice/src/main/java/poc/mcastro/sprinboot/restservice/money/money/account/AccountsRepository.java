package poc.mcastro.sprinboot.restservice.money.money.account;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends MongoRepository<AccountTO, String> {
    public AccountTO findByAccountNumber(String accountNumber);

    public AccountTO findByAccountBalance(String accountNumber);

}
