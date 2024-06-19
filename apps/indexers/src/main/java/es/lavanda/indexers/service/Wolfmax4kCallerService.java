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

import es.lavanda.lib.common.model.flaresolverr.input.FlaresolverrIDTO;
import es.lavanda.lib.common.model.flaresolverr.output.FlaresolverrODTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class Wolfmax4kCallerService {

    @Value("${flaresolverr.url}")
    private String FLARESOLVERR_URL;

    private final String WOLFMAX4K_URL = "https://wolfmax4k.com";

    private final String WOLFMAX4K_FILMS_1080P = "/peliculas/bluray-1080p/";
    private final String WOLFMAX4K_SHOWS_1080P = "/series/1080p/";
    private final String WOLFMAX4K_SHOWS_720P = "/series/720p/";

    private final String DOMAIN_WOLFMAX4K = "WOLFMAX4K";

    private final String HTTPS = "https:";

    public List<Index> getIndexForMainPage() {
        List<Index> indexes = new ArrayList<>();
        String html = callWithFlaresolverrV2(WOLFMAX4K_URL).getSolution().getResponse();
        Document document = Jsoup.parse(html);
        Elements layoutSectionElements = document.getElementsByClass("layout-section");
        indexes.addAll(getNewReleases(layoutSectionElements, "Peliculas bluray 1080p", Index.Type.FILM, Index.Quality.FULL_HD));
        indexes.addAll(getNewReleases(layoutSectionElements, "Series 720p", Index.Type.TV_SHOW, Index.Quality.HD));
        indexes.addAll(getNewReleases(layoutSectionElements, "Series 1080p", Index.Type.TV_SHOW, Index.Quality.FULL_HD));
        return indexes;
    }

    private List<Index> getNewReleases(Elements elements, String filter, Index.Type type, Index.Quality quality) {
        List<Index> indexes = new ArrayList<>();
        try {
            for (Element element : elements) {
                if (Objects.nonNull(element.getElementsByClass("layout-title").first())
                        && Objects.requireNonNull(element.getElementsByClass("layout-title").first()).text().equals(filter)) {
                    log.info("Founded elements on {}", filter);
                    return getIndexFromColLg2(element.getElementsByClass("col-lg-2"), type, quality);
                }
            }
        } catch (NullPointerException e) {
            log.error("Exception by nullPointer ", e);
            return indexes;
        }
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
        try {
            for (Element colLg2 : colLg2Elements) {
                if (Objects.requireNonNull(colLg2.children().first()).tagName().equals("a")) {
                    Index index = new Index();
                    index.setName(colLg2.getElementsByTag("h3").text());
                    String url = Objects.requireNonNull(colLg2.getElementsByTag("a").first()).attr("href");
                    index.setUrl(WOLFMAX4K_URL + url);
                    String htmlIndividual = callWithFlaresolverrV2(index.getUrl()).getSolution().getResponse();
                    Document documentIndividual = Jsoup.parse(htmlIndividual);
                    index.setIndexName(documentIndividual.getElementsByClass("h3 fw-semibold mb-1").first().text());
                    index.setDomain(DOMAIN_WOLFMAX4K);
                    String imageUrl = Objects.requireNonNull(colLg2.getElementsByClass("img-fluid rounded-1").first()).attr("src");
                    index.setImage(getByteArrayFromImageURL(HTTPS + imageUrl));
                    index.setQuality(quality);
                    index.setCreateTime(new Date());
                    index.setType(type);
                    indexes.add(index);
                }
            }
        } catch (Exception e) {
            log.error("Error getIndexFromColLg2 for exception", e);
            return indexes;
        }
        return indexes;
    }

    private List<Index> getIndexFromPage(String path, Index.Type type, Index.Quality quality) {
        log.info("Calling To Wolfmax4K with url path {}", path);
        List<Index> indexes = new ArrayList<>();
        String html = callWithFlaresolverrV2(WOLFMAX4K_URL + path).getSolution().getResponse();
        Document document = Jsoup.parse(html);
        Elements colLg2Elements = document.getElementsByClass("col-lg-2");
        try {
            for (Element colLg2 : colLg2Elements) {
                if (Objects.requireNonNull(colLg2.children().first()).tagName().equals("a")) {
                    Index index = new Index();
                    index.setName(colLg2.getElementsByTag("h3").text());
                    String url = Objects.requireNonNull(colLg2.getElementsByTag("a").first()).attr("href");
                    index.setUrl(WOLFMAX4K_URL + url);
                    String htmlIndividual = callWithFlaresolverrV2(index.getUrl()).getSolution().getResponse();
                    Document documentIndividual = Jsoup.parse(htmlIndividual);
                    index.setIndexName(documentIndividual.getElementsByClass("h3 fw-semibold mb-1").first().text());
                    index.setDomain(DOMAIN_WOLFMAX4K);
                    String imageUrl = Objects.requireNonNull(colLg2.getElementsByClass("img-fluid rounded-1").first()).attr("src");
                    index.setImage(getByteArrayFromImageURL(HTTPS + imageUrl));
                    index.setQuality(quality);
                    index.setCreateTime(new Date());
                    index.setType(type);
                    indexes.add(index);
                }
            }
        } catch (Exception e) {
            log.error("Error getIndexFromPage for exception", e);
            return indexes;
        }
        log.info("Finish To call Wolfmax4K with url {}", path);
        return indexes;
    }

    private List<Index> getIndexFromMultiPage(String path, Index.Type type, Index.Quality quality) {
        log.info("Calling To Wolfmax4K with url path {}", path);
        List<Index> indexes = new ArrayList<>();
        String html = callWithFlaresolverrV2(WOLFMAX4K_URL + path).getSolution().getResponse();
        Document document = Jsoup.parse(html);
        Elements colLg2Elements = document.getElementsByClass("col-lg-2");
        try {
            for (Element colLg2 : colLg2Elements) {
                if (Objects.requireNonNull(colLg2.children().first()).tagName().equals("a")) {
                    String urlForTemps = Objects.requireNonNull(colLg2.getElementsByTag("a").first()).attr("href");
                    String htmlShowWithTemps = callWithFlaresolverrV2(WOLFMAX4K_URL + urlForTemps).getSolution().getResponse();
                    Document documentShowWithTemps = Jsoup.parse(htmlShowWithTemps);
                    Elements tempElements = documentShowWithTemps.getElementsByClass("row gx-lg-4 gx-0");
                    for (Element tempElement : tempElements) {
                        Index index = new Index();
                        index.setName(colLg2.getElementsByTag("h3").text());
                        Elements elementsa = tempElement.getElementsByTag("a");
                        for (Element elementA : elementsa) {
                            String htmlShowChapter = callWithFlaresolverrV2(WOLFMAX4K_URL + elementA.attr("href")).getSolution().getResponse();
                            Document documentShowChapter = Jsoup.parse(htmlShowChapter);
                            index.setUrl(WOLFMAX4K_URL + elementA.attr("href"));
                            index.setIndexName(Objects.requireNonNull(documentShowChapter.getElementsByClass("h3 fw-semibold mb-1").first()).text());
                            index.setDomain(DOMAIN_WOLFMAX4K);
                            String imageUrl = Objects.requireNonNull(documentShowChapter.getElementsByClass("img-fluid rounded-1").first()).attr("src");
                            index.setImage(getByteArrayFromImageURL(HTTPS + imageUrl));
                            index.setQuality(quality);
                            index.setCreateTime(new Date());
                            index.setType(type);
                            indexes.add(index);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getIndexFromMultiPage for exception", e);
            return indexes;
        }
        log.info("Finish To call Wolfmax4K with url {}", path);
        return indexes;
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

    private String getByteArrayFromImageURL(String url) {
        try {
            try (InputStream in = new URL(url).openStream()) {
                byte[] imageBytes = IOUtils.toByteArray(in);
                return Base64.getEncoder().encodeToString(imageBytes);
            }
//            byte[] imageBytes = IOUtils.toByteArray(callWithFlaresolverr(url).getSolution().getResponse());
//            byte[] imageBytes = Base64.getDecoder().decode(callWithFlaresolverr(url).getSolution().getResponse());
//            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            log.error(e.toString());
            return url;
        }
    }

}
