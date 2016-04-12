package poc.mcastro.sprinboot.restservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import poc.mcastro.sprinboot.restservice.model.AccountTO;

@Repository
public interface AccountsRepository extends MongoRepository<AccountTO,String>{
	public AccountTO findByAccountNumber(String accountNumber);
	public AccountTO findByAccountBalance(String accountNumber);

}
