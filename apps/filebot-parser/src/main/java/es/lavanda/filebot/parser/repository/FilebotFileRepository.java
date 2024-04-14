package es.lavanda.filebot.parser.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.lavanda.filebot.parser.model.FilebotFile;


@Repository
public interface FilebotFileRepository extends MongoRepository<FilebotFile, String> {
    
}
