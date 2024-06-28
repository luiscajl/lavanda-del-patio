package es.lavanda.filebot.repository;

import es.lavanda.filebot.model.FilebotFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface FilebotFileRepository extends MongoRepository<FilebotFile, String> {
    
}
