package es.lavanda.filebot.executor.repository;

import org.springframework.stereotype.Repository;

import es.lavanda.filebot.executor.model.FilebotExecution;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface FilebotExecutionRepository extends MongoRepository<FilebotExecution, String> {

    boolean existsByName(String name);

    Optional<FilebotExecution> findByName(String name);

    Optional<FilebotExecution> findByPath(String path);

    Page<FilebotExecution> findAllByOrderByLastModifiedAtDesc(Pageable pageable);

    Page<FilebotExecution> findAllByStatusAndPathContainingIgnoreCaseOrderByLastModifiedAtDesc(Pageable pageable, String status, String path);

    Page<FilebotExecution> findAllByStatusOrderByLastModifiedAtDesc(Pageable pageable, String status);

    Page<FilebotExecution> findAllByPathIgnoreCaseContainingOrderByLastModifiedAtDesc(Pageable pageable, String path);

    List<FilebotExecution> findByStatusIn(List<String> status);

}
