package es.lavanda.filebot.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import es.lavanda.filebot.model.FilebotFile;

@Repository
public interface FilebotFileRepository extends PagingAndSortingRepository<FilebotFile, String> {
    
}
