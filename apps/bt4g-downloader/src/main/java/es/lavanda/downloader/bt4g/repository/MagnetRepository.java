package es.lavanda.downloader.bt4g.repository;

import es.lavanda.downloader.bt4g.model.Magnet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MagnetRepository extends MongoRepository<Magnet, String> {
}

