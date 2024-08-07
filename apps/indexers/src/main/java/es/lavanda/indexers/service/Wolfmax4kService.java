package es.lavanda.indexers.service;

import es.lavanda.indexers.exception.IndexerException;
import es.lavanda.indexers.model.Index;
import es.lavanda.indexers.repository.IndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class Wolfmax4kService
        implements CommandLineRunner {

    private final String DOMAIN_WOLFMAX4K = "WOLFMAX4K";

    private final IndexRepository indexRepository;

    private final MinioService minioService;


    public Page<Index> getAllPageable(Pageable pageable, Index.Type type, Index.Quality quality, String name) {
        if (Objects.nonNull(name)) {
            return indexRepository.findAllByTypeAndQualityAndDomainAndIndexNameContainingIgnoreCaseOrderByCreateTimeDesc(pageable, type, quality, DOMAIN_WOLFMAX4K, name);
        } else {
            return indexRepository.findAllByTypeAndQualityAndDomainOrderByCreateTimeDesc(pageable, type, quality, DOMAIN_WOLFMAX4K);
        }
    }

    public Index save(Index index) {
        try {
            log.debug("Checking if exist index {}", index);
            if (Boolean.FALSE.equals(indexRepository.existsByIndexName(index.getIndexName()))) {
                log.info("Trying to save new index {}", index);
                return indexRepository.save(index);
            }
            return null;
        } catch (Exception e) {
            log.error("Can't save object by:", e);
            throw new IndexerException("Can't save object on database");
        }
    }


    public void deleteById(String id) {
        indexRepository.deleteById(id);
    }

    @Override
    public void run(String... args) {
        indexRepository.findAll().forEach(index -> {
            try {
                index.setImage(minioService.saveImage(index.getName(), index.getImage()));
                save(index);
            } catch (IndexerException e) {
                log.error("Can't update the image because... ", e);
            }
        });
    }

}
