package es.lavanda.downloader.bt4g.repository;

import es.lavanda.downloader.bt4g.model.Bt4g;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Bt4gRepository extends MongoRepository<Bt4g, String> {

    Page<Bt4g> findByNameContainingIgnoreCase(Pageable pageable, String name);
}
