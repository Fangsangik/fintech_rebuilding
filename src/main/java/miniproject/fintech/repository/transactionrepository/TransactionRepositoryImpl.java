package miniproject.fintech.repository.transactionrepository;

import miniproject.fintech.domain.Transaction;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TransactionRepositoryImpl {

    private final TransactionRepository repository;

    public TransactionRepositoryImpl(@Lazy TransactionRepository repository) {
        this.repository = repository;
    }

    private Transaction save (Transaction transaction){
        return repository.save(transaction);
    }

    private Optional<Transaction> findById(Transaction transaction){
        return repository.findById(transaction.getId());
    }

    private List<Transaction> findAll() {
        return repository.findAll();
    }
}
