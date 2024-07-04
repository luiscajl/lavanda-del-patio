package es.lavanda.indexers.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.lavanda.indexers.exception.IndexerException;
import es.lavanda.indexers.model.Index;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import es.lavanda.lib.common.model.flaresolverr.input.FlaresolverrIDTO;
import es.lavanda.lib.common.model.flaresolverr.output.FlaresolverrODTO;

@Service
@Slf4j
@RequiredArgsConstructor
public class Wolfmax4KCallerService {

    @Value("${flaresolverr.url}")
    private String flaresolverrUrl;

    private static final String WOLFMAX4K_URL = "https://wolfmax4k.com";
    private static final String DOMAIN_WOLFMAX4K = "WOLFMAX4K";
    private static final String HTTPS = "https:";

    public List<Index> getIndexForMainPageV2(String url, Index.Type type, Index.Quality quality, boolean multipleOnAPage) {
        log.info("Wolfmax4K Type: {} Quality: {} ", type, quality);
        FlaresolverrIDTO flaresolverrIDTOWolfmax4k = getHtmlResponse(url);
        Document document = Jsoup.parse(flaresolverrIDTOWolfmax4k.getSolution().getResponse());
        List<Index> indexes = new ArrayList<>(getNewReleasesV2(document, type, quality, multipleOnAPage));
        log.info("Finish Wolfmax4K Type: {} Quality: {} ", type, quality);
        return indexes;
    }

    private List<Index> getNewReleasesV2(Document document, Index.Type type, Index.Quality quality, boolean multipleOnAPage) {
        return new ArrayList<>(getIndexFromColLg2(document.getElementsByClass("col-lg-2"), type, quality, multipleOnAPage));
    }

    private List<Index> getIndexFromColLg2(Elements colLg2Elements, Index.Type type, Index.Quality quality, boolean multipleOnAPage) {
        List<Index> indexes = new ArrayList<>();
        for (Element colLg2 : colLg2Elements) {
            if (colLg2.getElementsByClass("fs-xs text-muted fw-semibold mb-2").isEmpty()) {
                Element anchor = colLg2.getElementsByTag("a").first();
                if (anchor != null) {
                    if (multipleOnAPage) {
                        indexes.addAll(buildIndexForMulti(colLg2, type, quality, anchor.attr("href")));
                    } else {
                        indexes.add(buildIndex(colLg2, type, quality, anchor.attr("href")));
                    }
                }
            }
        }
        return indexes;
    }


    private Index buildIndex(Element colLg2, Index.Type type, Index.Quality quality, String url) {
        Index index = new Index();
        index.setName(colLg2.getElementsByTag("h3").text());
        index.setUrl(HTTPS + url);
        FlaresolverrIDTO flaresolverrIDTOIndividual = getHtmlResponse(index.getUrl());
        Document documentIndividual = Jsoup.parse(flaresolverrIDTOIndividual.getSolution().getResponse());
        index.setIndexName(documentIndividual.getElementsByClass("h3 fw-semibold mb-1").text());
        index.setDomain(DOMAIN_WOLFMAX4K);
        String imageElement = colLg2.getElementsByClass("img-fluid rounded-1").attr("src");
        String imageUrl = getImageUrl(imageElement);
        index.setImage(getImageAsBase64(imageUrl));
        index.setQuality(quality);
        index.setCreateTime(new Date());
        index.setType(type);
        return index;
    }

    private List<Index> buildIndexForMulti(Element colLg2, Index.Type type, Index.Quality quality, String url) {
        List<Index> indexes = new ArrayList<>();
        String urlMainShow = HTTPS + url;
        FlaresolverrIDTO flaresolverrIDTOMainShow = getHtmlResponse(urlMainShow);
        Document documentMainShow = Jsoup.parse(flaresolverrIDTOMainShow.getSolution().getResponse());
        Element rowAfterLayoutSection = documentMainShow.getElementsByClass("row gx-lg-4 gx-0").first();
        String imageElement = colLg2.getElementsByClass("img-fluid rounded-1").attr("src");
        String imageUrl = getImageUrl(imageElement);
        String imageBase64 = getImageAsBase64(imageUrl);
        for (Element tempSerie : rowAfterLayoutSection.getElementsByClass("temp-serie")) {
            for (Element a : tempSerie.getElementsByTag("a")) {
                Index index = new Index();
                index.setName(colLg2.getElementsByTag("h3").text());
                index.setUrl(WOLFMAX4K_URL + a.attr("href"));
                try {
                    index.setIndexName(
                            Jsoup.parse(getHtmlResponse(index.getUrl()).getSolution().getResponse()).getElementsByClass("h3 fw-semibold mb-1").first().text());
                } catch (Exception e) {
                    log.error("Exception trying get indexName", e);
                    index.setIndexName(a.text());
                }
                index.setDomain(DOMAIN_WOLFMAX4K);
                index.setImage(imageBase64);
                index.setQuality(quality);
                index.setCreateTime(new Date());
                index.setType(type);
                indexes.add(index);
            }
        }
        return indexes;
    }

    private RestClient configureRestClient() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        return RestClient.create(restTemplate);
    }

    private String getImageUrl(String imageElement) {
        if (imageElement.contains("wolfmax4k.com")) {
            return HTTPS + imageElement;
        } else {
            return WOLFMAX4K_URL + imageElement;
        }
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000); // 60 seconds
        factory.setReadTimeout(120000);   // 120 seconds
        return factory;
    }

    @Cacheable("urls")
    public FlaresolverrIDTO getHtmlResponse(String url) {
        try {
            log.info("Calling to flaresolverr for url {}", url);
            FlaresolverrODTO request = new FlaresolverrODTO();
            request.setCmd("request.get");
            request.setUrl(url);
            request.setMaxTimeout(600000);
            RestClient restClient = configureRestClient();
            return restClient.post().uri(flaresolverrUrl).body(request).retrieve().body(FlaresolverrIDTO.class);
        } catch (Exception e) {
            log.error("Exception getting htmlResponse {}", e.getMessage());
            return getHtmlResponse(url);
        }
    }

    @Cacheable("images")
    public String getImageAsBase64(String imageUrl) {
        log.info("Calling to get image from url {} ", imageUrl);
        try (InputStream in = new URL(imageUrl).openStream()) {
            byte[] imageBytes = IOUtils.toByteArray(in);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("Error while fetching image as Base64", e);
            return imageUrl;
        }
    }
}
