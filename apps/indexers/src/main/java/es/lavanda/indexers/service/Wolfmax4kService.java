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
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class Wolfmax4kService implements CommandLineRunner {

    private final String DOMAIN_WOLFMAX4K = "WOLFMAX4K";

    private final Wolfmax4kCallerService wolfmax4kCallerService;

    private final IndexRepository indexRepository;

    public Page<Index> getAllPageable(Pageable pageable, Index.Type type, Index.Quality quality) {
        return indexRepository.findAllByTypeAndQualityAndDomain(pageable, type, quality, DOMAIN_WOLFMAX4K);
    }

    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
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

    @Override
    public void run(String... args) throws Exception {
        log.info(callWithFlaresolverrV2("https://wolfmax4k.com").getSolution().getResponse());
        log.info(callWithFlaresolverrV2("https://bt4gprx.com/search/Sound of Freedom (2023) [4k 2160p][Esp]").getSolution().getResponse());
    }

    private RestClient configureRestClient() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        return RestClient.create(restTemplate);
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000); // 60 seconds
        factory.setReadTimeout(120000);    // 120 seconds
        return factory;
    }

    private FlaresolverrIDTO callWithFlaresolverrV2(String url) {
        log.info("Calling to flaresolverr for url {}", url);
        FlaresolverrODTO flaresolverrODTO = new FlaresolverrODTO();
        flaresolverrODTO.setCmd("request.get");
        flaresolverrODTO.setMaxTimeout(600000);
        flaresolverrODTO.setUrl(url);
        RestClient restClient = configureRestClient();
        return restClient
                .post()
                .uri(FLARESOLVERR_URL)
                .body(flaresolverrODTO)
                .retrieve()
                .body(FlaresolverrIDTO.class);
    }

    @Value("${flaresolverr.url}")
    private String FLARESOLVERR_URL;
}
