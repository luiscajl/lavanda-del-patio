package es.lavanda.filebot.repository;

import es.lavanda.filebot.model.Filebot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import org.springframework.data.mongodb.repository.MongoRepository;


@Repository
public interface FilebotRepository extends MongoRepository<Filebot, String> {

    Page<Filebot> findAllByOrderByLastModifiedAtDesc(Pageable pageable);

    
}
