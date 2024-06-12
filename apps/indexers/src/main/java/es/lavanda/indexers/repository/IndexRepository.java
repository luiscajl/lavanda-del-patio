package es.lavanda.indexers.repository;

import es.lavanda.indexers.model.Index;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexRepository extends MongoRepository<Index, String> {

    Page<Index> findAllByTypeAndQualityAndDomain(Pageable pageable, Index.Type type, Index.Quality quality, String domain);
}