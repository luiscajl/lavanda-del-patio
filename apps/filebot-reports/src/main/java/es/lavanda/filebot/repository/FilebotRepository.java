package es.lavanda.filebot.repository;

import org.springframework.stereotype.Repository;

import es.lavanda.filebot.model.Filebot;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

@Repository
public interface FilebotRepository extends PagingAndSortingRepository<Filebot, String> {

    Page<Filebot> findAllByOrderByLastModifiedAtDesc(Pageable pageable);

    List<Filebot> findAll();
}
