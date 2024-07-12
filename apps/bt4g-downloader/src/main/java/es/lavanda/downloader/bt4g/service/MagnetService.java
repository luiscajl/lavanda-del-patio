package es.lavanda.downloader.bt4g.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.lavanda.downloader.bt4g.model.Magnet;
import es.lavanda.downloader.bt4g.repository.MagnetRepository;
import es.lavanda.lib.common.model.flaresolverr.input.FlaresolverrIDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MagnetService {
    private final MagnetRepository magnetRepository;


    private final String MAGNET_URL = "https://raw.githubusercontent.com/ngosang/trackerslist/master/trackers_all.txt";

    private static final String MAGNET_LINK_INIT = "magnet:?xt=urn:btih:";

    public List<String> getAllMagnets() {
        return magnetRepository.findAll().stream().map(Magnet::getUrl).toList();
    }

    public void updateMagnets() {
        log.info("Updating magnets");
        RestClient restClient = RestClient.create();
        String magnetsString = restClient
                .get()
                .uri(MAGNET_URL)
                .retrieve()
                .body(String.class);
        if (magnetsString != null) {
            List<Magnet> magnets = Arrays.stream(magnetsString.split("\n"))
                    .filter(line -> !line.trim().isEmpty())
                    .map(url -> {
                        Magnet magnet = new Magnet();
                        magnet.setUrl(url.trim());
                        return magnet;
                    })
                    .collect(Collectors.toList());
            magnetRepository.deleteAll();
            magnetRepository.saveAll(magnets);
            log.info("Magnet updateds");
        } else {
            log.error("Failed to fetch magnet URLs from the provided URL.");
            log.info("Magnet not updated");
        }
    }

    public String getMagnetWithTrackers(String hashMagnet, String name) {
        return MAGNET_LINK_INIT + hashMagnet + "&dn=" + name
                + joinTrackers(getAllMagnets());
    }

    private String joinTrackers(List<String> trackers) {
        StringBuilder sb = new StringBuilder();
        for (String tracker : trackers) {
            sb.append("&tr=").append(tracker);
        }
        return sb.toString();
    }
}
