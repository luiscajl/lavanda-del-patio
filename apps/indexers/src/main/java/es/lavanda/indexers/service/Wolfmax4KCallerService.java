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
public class Wolfmax4KCallerServiceChatGPTVersion {

    @Value("${flaresolverr.url}")
    private String flaresolverrUrl;

    private static final String WOLFMAX4K_URL = "https://wolfmax4k.com";
    private static final String WOLFMAX4K_FILMS_1080P = "/peliculas/bluray-1080p/";
    private static final String WOLFMAX4K_SHOWS_1080P = "/series/1080p/";
    private static final String WOLFMAX4K_SHOWS_720P = "/series/720p/";
    private static final String DOMAIN_WOLFMAX4K = "WOLFMAX4K";
    private static final String HTTPS = "https:";

    public List<Index> getIndexForMainPage() {
        List<Index> indexes = new ArrayList<>();
        FlaresolverrIDTO flaresolverrIDTOWolfmax4k = getHtmlResponse(WOLFMAX4K_URL);
        Document document = Jsoup.parse(flaresolverrIDTOWolfmax4k.getSolution().getResponse());
        Elements layoutSectionElements = document.getElementsByClass("layout-section");
        indexes.addAll(getNewReleases(layoutSectionElements, "Series 720p", Index.Type.TV_SHOW, Index.Quality.HD));
        indexes.addAll(getNewReleases(layoutSectionElements, "Series 1080p", Index.Type.TV_SHOW, Index.Quality.FULL_HD));
        indexes.addAll(getNewReleases(layoutSectionElements, "Series 4K 2160p", Index.Type.FILM, Index.Quality.ULTRA_HD));
        indexes.addAll(getNewReleases(layoutSectionElements, "Peliculas bluray 1080p", Index.Type.FILM, Index.Quality.FULL_HD));
        indexes.addAll(getNewReleases(layoutSectionElements, "Peliculas 4K 2160p", Index.Type.FILM, Index.Quality.ULTRA_HD));
        return indexes;
    }

    private List<Index> getNewReleases(Elements elements, String filter, Index.Type type, Index.Quality quality) {
        List<Index> indexes = new ArrayList<>();
        elements.stream()
                .filter(element -> filter.equals(element.getElementsByClass("layout-title").text()))
                .findFirst()
                .ifPresent(element -> {
                    log.info("Founded elements on {}", filter);
                    indexes.addAll(getIndexFromColLg2(element.getElementsByClass("col-lg-2"), type, quality));
                });
        return indexes;
    }

    public List<Index> getIndexFilmsFullHd() {
        return getIndexFromPage(WOLFMAX4K_FILMS_1080P, Index.Type.FILM, Index.Quality.FULL_HD);
    }

    public List<Index> getIndexShowsFullHd() {
        return getIndexFromMultiPage(WOLFMAX4K_SHOWS_1080P, Index.Type.TV_SHOW, Index.Quality.FULL_HD);
    }

    public List<Index> getIndexShowsHd() {
        return getIndexFromMultiPage(WOLFMAX4K_SHOWS_720P, Index.Type.TV_SHOW, Index.Quality.HD);
    }

    private List<Index> getIndexFromColLg2(Elements colLg2Elements, Index.Type type, Index.Quality quality) {
        List<Index> indexes = new ArrayList<>();
        for (Element colLg2 : colLg2Elements) {
            Element anchor = colLg2.getElementsByTag("a").first();
            if (anchor != null) {
                Index index = buildIndex(colLg2, type, quality, anchor.attr("href"));
                indexes.add(index);
            }
        }
        return indexes;
    }

    private List<Index> getIndexFromPage(String path, Index.Type type, Index.Quality quality) {
        log.info("Calling To Wolfmax4K with url path {}", path);
        List<Index> indexes = new ArrayList<>();
        FlaresolverrIDTO flaresolverrIDTOPage = getHtmlResponse(WOLFMAX4K_URL + path);
        Document document = Jsoup.parse(flaresolverrIDTOPage.getSolution().getResponse());
        Elements colLg2Elements = document.getElementsByClass("col-lg-2");
        for (Element colLg2 : colLg2Elements) {
            Element anchor = colLg2.getElementsByTag("a").first();
            if (anchor != null) {
                Index index = buildIndex(colLg2, type, quality, anchor.attr("href"));
                indexes.add(index);
            }
        }
        log.info("Finish To call Wolfmax4K with url {}", path);
        return indexes;
    }

    private List<Index> getIndexFromMultiPage(String path, Index.Type type, Index.Quality quality) {
        log.info("Calling To Wolfmax4K with url path {}", path);
        List<Index> indexes = new ArrayList<>();
        FlaresolverrIDTO flaresolverrIDTO = getHtmlResponse(WOLFMAX4K_URL + path);
        Document document = Jsoup.parse(flaresolverrIDTO.getSolution().getResponse());
        Elements colLg2Elements = document.getElementsByClass("col-lg-2");
        for (Element colLg2 : colLg2Elements) {
            Element anchor = colLg2.getElementsByTag("a").first();
            if (anchor != null) {
                String urlForTemps = anchor.attr("href");
                FlaresolverrIDTO flaresolverrIDTO1 = getHtmlResponse(WOLFMAX4K_URL + urlForTemps);
                Document documentShowWithTemps = Jsoup.parse(flaresolverrIDTO1.getSolution().getResponse());
                Elements tempElements = documentShowWithTemps.getElementsByClass("row gx-lg-4 gx-0");
                for (Element tempElement : tempElements) {
                    Elements elementAnchors = tempElement.getElementsByTag("a");
                    for (Element elementA : elementAnchors) {
//                        FlaresolverrIDTO flaresolverrIDTO2 = getHtmlResponse(WOLFMAX4K_URL + elementA.attr("href"));
//                        Document documentShowChapter = Jsoup.parse(flaresolverrIDTO2.getSolution().getResponse());
                        Index index = buildIndex(colLg2, type, quality, elementA.attr("href"));
                        indexes.add(index);
                    }
                }
            }
        }
        log.info("Finish To call Wolfmax4K with url {}", path);
        return indexes;
    }

    private Index buildIndex(Element colLg2, Index.Type type, Index.Quality quality, String url) {
        Index index = new Index();
        index.setName(colLg2.getElementsByTag("h3").text());
        index.setUrl(WOLFMAX4K_URL + url);
        FlaresolverrIDTO flaresolverrIDTOIndividual = getHtmlResponse(index.getUrl());
        Document documentIndividual = Jsoup.parse(flaresolverrIDTOIndividual.getSolution().getResponse());
        index.setIndexName(documentIndividual.getElementsByClass("h3 fw-semibold mb-1").text());
        index.setDomain(DOMAIN_WOLFMAX4K);
        String imageUrl = HTTPS + colLg2.getElementsByClass("img-fluid rounded-1").attr("src");
        index.setImage(getImageAsBase64(imageUrl));
        index.setQuality(quality);
        index.setCreateTime(new Date());
        index.setType(type);
        return index;
    }

    private RestClient configureRestClient() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        return RestClient.create(restTemplate);
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000); // 60 seconds
        factory.setReadTimeout(120000);   // 120 seconds
        return factory;
    }

    private FlaresolverrIDTO getHtmlResponse(String url) {
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

    private String getImageAsBase64(String imageUrl) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            byte[] imageBytes = IOUtils.toByteArray(in);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("Error while fetching image");
            return imageUrl;
        }
    }
}
