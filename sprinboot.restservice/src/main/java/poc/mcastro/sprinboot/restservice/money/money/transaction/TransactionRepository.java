package poc.mcastro.sprinboot.restservice.money.money.transaction;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<TransactionTO, String> {
    public List<TransactionTO> findByStatus(String status);
}
