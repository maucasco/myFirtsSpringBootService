package poc.mcastro.sprinboot.restservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import poc.mcastro.sprinboot.restservice.model.TransactionTO;

@Repository
public interface TransactionRepository extends MongoRepository<TransactionTO, String> {
	public List<TransactionTO> findByStatus(String status);
}
