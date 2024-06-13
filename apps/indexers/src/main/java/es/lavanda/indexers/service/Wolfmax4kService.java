package es.lavanda.indexers.service;

import es.lavanda.indexers.model.Index;
import es.lavanda.indexers.repository.IndexRepository;
import es.lavanda.lib.common.model.bt4g.Bt4gDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class Wolfmax4kService {

    private final String DOMAIN_WOLFMAX4K = "WOLFMAX4K";

    private final Wolfmax4kCallerService wolfmax4kCallerService;

    private final IndexRepository indexRepository;

    public Page<Index> getAllPageable(Pageable pageable, Index.Type type, Index.Quality quality) {
        return indexRepository.findAllByTypeAndQualityAndDomain(pageable, type, quality, DOMAIN_WOLFMAX4K);
    }

    @Scheduled(fixedDelay = 4, timeUnit = TimeUnit.HOURS)
    public void updateIndex() {
        log.info("Updating Wolfmax4k Indexes");
        List<Index> indexesFilmsFullHd = wolfmax4kCallerService.getIndexFilmsFullHd();
        List<Index> indexesShowsFullHd = wolfmax4kCallerService.getIndexShowsFullHd();
        List<Index> indexesShowsHd = wolfmax4kCallerService.getIndexShowsHd();
        saveList(indexesFilmsFullHd);
        saveList(indexesShowsFullHd);
        saveList(indexesShowsHd);
        log.info("Finish Wolfmax4k Indexes");
    }

    private void saveList(List<Index> indexes) {
        for (Index index : indexes) {
            try {
                log.info("Trying to save index {}", index.toString());
                indexRepository.save(index);
            } catch (Exception e) {
                log.error("Can't save object by:", e);
            }
        }
    }
}
