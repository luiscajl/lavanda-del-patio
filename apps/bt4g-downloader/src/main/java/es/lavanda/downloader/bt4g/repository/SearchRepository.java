package es.lavanda.downloader.bt4g.repository;

import es.lavanda.downloader.bt4g.model.Search;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends MongoRepository<Search, String> {

    void deleteByName(String name);
}
