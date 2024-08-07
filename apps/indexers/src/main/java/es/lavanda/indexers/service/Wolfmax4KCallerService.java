package es.lavanda.indexers.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;

import es.lavanda.indexers.exception.IndexerException;
import es.lavanda.indexers.model.Index;
import es.lavanda.indexers.repository.IndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import es.lavanda.lib.common.model.flaresolverr.input.FlaresolverrIDTO;
import es.lavanda.lib.common.model.flaresolverr.output.FlaresolverrODTO;

@Service
@Slf4j
@RequiredArgsConstructor
public class Wolfmax4KCallerService {

    @Value("${flaresolverr.url}")
    private String FLARESOLVERR_URL;

    @Value("${proxy.host}")
    private String PROXY_HOST;

    @Value("${proxy.port}")
    private Integer PROXY_PORT;

    private final Wolfmax4kService wolfmax4kService;

    private final MinioService minioService;

    private static final String WOLFMAX4K_URL = "https://wolfmax4k.com";
    private static final String DOMAIN_WOLFMAX4K = "WOLFMAX4K";
    private static final String HTTPS = "https:";
    private static final String[] SHOWS_URLS = {
            "/series/720p/",
            "/series/1080p/",
            "/series/4k-2160p/"
    };
    private static final String[] FILMS_URLS = {
            "/peliculas/bluray-1080p/",
            "/peliculas/4k-2160p/"
    };
    private boolean doubleCheck = false;

    @Scheduled(fixedDelay = 24, timeUnit = TimeUnit.HOURS)
    private void updateIndex() {
        log.info("Updating Wolfmax4k Indexes");
        //SHOWS
        for (String url : SHOWS_URLS) {
            getIndexForMainPageV2(WOLFMAX4K_URL + url, Index.Type.TV_SHOW, getQualityFromUrl(url), true);
        }
        //FILMS
        for (String url : FILMS_URLS) {
            getIndexForMainPageV2(WOLFMAX4K_URL + url, Index.Type.FILM, getQualityFromUrl(url), false);
        }
        log.info("Finish Wolfmax4k Indexes");
    }

    private Index.Quality getQualityFromUrl(String url) {
        if (url.contains("720p")) return Index.Quality.HD;
        if (url.contains("1080p")) return Index.Quality.FULL_HD;
        return Index.Quality.ULTRA_HD;
    }

    public void getIndexForMainPageV2(String url, Index.Type type, Index.Quality quality, boolean multipleOnAPage) {
        log.info("Wolfmax4K Type: {} Quality: {} ", type, quality);
        Document document = retryGetHtmlResponse(url, "row row-cols-xxl-8 row-cols-md-4 row-cols-2");
        Elements elements = document.getElementsByClass("row row-cols-xxl-8 row-cols-md-4 row-cols-2");
        for (Element rowElement : elements) {
            List<Element> colLg2Elements = rowElement.getElementsByClass("col-lg-2");
            for (int i = colLg2Elements.size() - 1; i >= 0; i--) {
                Element colLg2Element = colLg2Elements.get(i);
                if (multipleOnAPage) {
                    processShow(colLg2Element, type, quality);
                } else {
                    Index indexToSave = buildIndex(colLg2Element, type, quality);
                    if (Objects.nonNull(indexToSave)) {
                        wolfmax4kService.save(indexToSave);
                    }
                }
            }
        }
        log.info("Finish Wolfmax4K Type: {} Quality: {} ", type, quality);
    }


    private void processShow(Element colLg2, Index.Type type, Index.Quality quality) {
        String urlMainShow = HTTPS + colLg2.getElementsByTag("a").first().attr("href");
        try {
            Document documentMainShow = retryGetHtmlResponse(urlMainShow, "row gx-lg-4 gx-0");
            Element rowAfterLayoutSection = documentMainShow.getElementsByClass("row gx-lg-4 gx-0").getFirst();
//        String imageUrl = getImageUrl(colLg2.getElementsByClass("img-fluid rounded-1").attr("src"));
//        String imageBase64 = getImageAsBase64(imageUrl);
            if (rowAfterLayoutSection != null) {
                Elements tempSerieElements = rowAfterLayoutSection.getElementsByClass("temp-serie");
                for (int i = tempSerieElements.size() - 1; i >= 0; i--) {
                    Element tempSerie = tempSerieElements.get(i);
                    for (int j = tempSerie.getElementsByTag("a").size() - 1; j >= 0; j--) {
                        Element aElement = tempSerie.getElementsByTag("a").get(j);
                        String nameShow = colLg2.getElementsByTag("h3").text();
                        Index indexToSave = buildIndexForShow(nameShow, aElement, type, quality);
                        if (Objects.nonNull(indexToSave)) {
                            wolfmax4kService.save(indexToSave);
                        }
                    }
                }
            }
        } catch (IndexerException e) {
            log.error("IndexerException processingShow", e);
        }
    }

    private Index buildIndexForShow(String nameShow, Element aElement, Index.Type type, Index.Quality quality) {
        Index index = new Index();
        index.setName(nameShow);
        String url = aElement.getElementsByTag("a").first().attr("href");
        index.setUrl(WOLFMAX4K_URL + url);
        try {
            Document documentIndividual = retryGetHtmlResponse(index.getUrl(), "h3 fw-semibold mb-1");
            index.setIndexName(documentIndividual.getElementsByClass("h3 fw-semibold mb-1").text());
            String imageUrl = getImageUrl(documentIndividual.getElementsByClass("img-fluid rounded-1").attr("src"));
            String imageBase64 = getImageAsBase64(imageUrl);
            index.setImage(minioService.saveImage(index.getName(), imageBase64));
            index.setDomain(DOMAIN_WOLFMAX4K);
            index.setQuality(quality);
            index.setCreateTime(new Date());
            index.setType(type);
            return index;
        } catch (IndexerException e) {
            return null;
        }
    }

    private Index buildIndex(Element colLg2OrA, Index.Type type, Index.Quality quality) {
        Index index = new Index();
        index.setName(colLg2OrA.getElementsByTag("h3").text());
        String url = colLg2OrA.getElementsByTag("a").first().attr("href");
        index.setUrl(HTTPS + url);
        try {
            Document documentIndividual = retryGetHtmlResponse(index.getUrl(), "h3 fw-semibold mb-1");
            index.setIndexName(documentIndividual.getElementsByClass("h3 fw-semibold mb-1").text());
            index.setDomain(DOMAIN_WOLFMAX4K);
            String imageUrl = getImageUrl(colLg2OrA.getElementsByClass("img-fluid rounded-1").attr("src"));
            String imageBase64 = getImageAsBase64(imageUrl);
            index.setImage(minioService.saveImage(index.getName(), imageBase64));
            index.setQuality(quality);
            index.setCreateTime(new Date());
            index.setType(type);
            return index;
        } catch (IndexerException e) {
            return null;
        }
    }

    private String getImageUrl(String imageElement) {
        return imageElement.contains("wolfmax4k.com") ? HTTPS + imageElement : WOLFMAX4K_URL + imageElement;
    }

    private Document retryGetHtmlResponse(String url, String checkContainsClass) {
        log.info("retryGetHtmlResponse for url {} and checkContainsClass {}", url, checkContainsClass);
        try {
            String htmlResponse = getHtmlResponse(url).getSolution().getResponse();
            Document document = Jsoup.parse(htmlResponse);

            if (!document.getElementsByClass(checkContainsClass).isEmpty()) {
                doubleCheck = false;
                return document;
            } else {
                if (!doubleCheck) {
                    log.error("Class '{}' not found on the result of the url {}", checkContainsClass, url);
                    doubleCheck = true;
                    return retryGetHtmlResponse(url, checkContainsClass);
                } else {
                    log.error("Class '{}' not found two times on the result of the url {}", checkContainsClass, url);
                    doubleCheck = false;
                    throw new IndexerException("Class not found two times on retryGetHtmlResponse");
                }
            }
        } catch (Exception e) {
            log.error("Exception occurred while fetching or parsing the HTML response for url {}: {}", url, e.getMessage());
            throw new IndexerException("Exception in retryGetHtmlResponse", e);
        }
    }

    public FlaresolverrIDTO getHtmlResponse(String url) {
        try {
            log.info("Calling to flaresolverr for url {}", url);
            FlaresolverrODTO request = new FlaresolverrODTO();
            request.setCmd("request.get");
            request.setUrl(url);
            request.setMaxTimeout(600000);
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.postForObject(FLARESOLVERR_URL, request, FlaresolverrIDTO.class);
        } catch (Exception e) {
            log.error("Exception getting htmlResponse: {}", e.getMessage());
            return getHtmlResponse(url);
        }
    }

    @Cacheable("images")
    public String getImageAsBase64(String imageUrl) {
        log.info("Calling to get image from url {} ", imageUrl);
        InputStream in = null;
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection(proxy);
            in = connection.getInputStream();
            byte[] imageBytes = IOUtils.toByteArray(in);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("Error while fetching image as Base64: {}", e.getLocalizedMessage());
            return imageUrl;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("Error closing InputStream: {}", e.getLocalizedMessage());
                }
            }
        }
    }
}
