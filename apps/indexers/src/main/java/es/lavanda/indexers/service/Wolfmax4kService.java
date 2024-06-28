package es.lavanda.indexers.service;

import es.lavanda.indexers.model.Index;
import es.lavanda.indexers.repository.IndexRepository;
import es.lavanda.lib.common.model.bt4g.Bt4gDTO;
import es.lavanda.lib.common.model.flaresolverr.input.FlaresolverrIDTO;
import es.lavanda.lib.common.model.flaresolverr.output.FlaresolverrODTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class Wolfmax4kService {

    private final String DOMAIN_WOLFMAX4K = "WOLFMAX4K";

    private final Wolfmax4KCallerService wolfmax4kCallerService;

    private final IndexRepository indexRepository;

    public Page<Index> getAllPageable(Pageable pageable, Index.Type type, Index.Quality quality, String name) {
        if (Objects.nonNull(name)) {
            return indexRepository.findAllByTypeAndQualityAndDomainAndNameContainingIgnoreCaseOrderByCreateTimeDesc(pageable, type, quality, DOMAIN_WOLFMAX4K, name);
        } else {
            return indexRepository.findAllByTypeAndQualityAndDomainOrderByCreateTimeDesc(pageable, type, quality, DOMAIN_WOLFMAX4K);
        }
    }

    @Scheduled(fixedDelay = 20, timeUnit = TimeUnit.MINUTES)
    public void updateIndex() {
        log.info("Updating Wolfmax4k Indexes");
        saveList(wolfmax4kCallerService.getIndexForMainPage());
        log.info("Finish Wolfmax4k Indexes");
    }

    private void saveList(List<Index> indexes) {
        for (Index index : indexes) {
            try {
                log.debug("Checking if exist index {}", index);
                if (Boolean.FALSE.equals(indexRepository.existsByIndexName(index.getIndexName()))) {
                    log.info("Trying to save new index {}", index);
                    indexRepository.save(index);
                }
            } catch (Exception e) {
                log.error("Can't save object by:", e);
            }
        }
    }
}
