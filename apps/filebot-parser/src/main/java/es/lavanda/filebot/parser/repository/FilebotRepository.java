package es.lavanda.filebot.parser.repository;

import org.springframework.stereotype.Repository;

import es.lavanda.filebot.parser.model.Filebot;

import org.springframework.data.mongodb.repository.MongoRepository;


@Repository
public interface FilebotRepository extends MongoRepository<Filebot, String> {

    
}
