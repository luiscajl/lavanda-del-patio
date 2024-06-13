package es.lavanda.indexers.service;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.lavanda.indexers.model.Index;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import es.lavanda.lib.common.model.flaresolverr.input.FlaresolverrIDTO;
import es.lavanda.lib.common.model.flaresolverr.output.FlaresolverrODTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class Wolfmax4kCallerWithThreadsService {
    private final String WOLFMAX4K_URL = "https://wolfmax4k.com";
    private final String WOLFMAX4K_FILMS_1080P = "/peliculas/bluray-1080p/";
    private final String WOLFMAX4K_SHOWS_1080P = "/series/1080p/";
    private final String WOLFMAX4K_SHOWS_720P = "/series/720p/";

    private final String DOMAIN_WOLFMAX4K = "WOLFMAX4K";
    private final String HTTPS = "https:";

    @Value("${flaresolverr.url}")
    private String FLARESOLVERR_URL;

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    public List<Index> getAllFromAllPages() {
        List<Future<List<Index>>> futures = new ArrayList<>();
        futures.add(executorService.submit(() -> getIndexFromPage(WOLFMAX4K_FILMS_1080P, Index.Type.FILM, Index.Quality.FULL_HD)));
        futures.add(executorService.submit(() -> getIndexFromMultiPage(WOLFMAX4K_SHOWS_1080P, Index.Type.TV_SHOW, Index.Quality.FULL_HD)));
        futures.add(executorService.submit(() -> getIndexFromMultiPage(WOLFMAX4K_SHOWS_720P, Index.Type.TV_SHOW, Index.Quality.HD)));

        List<Index> allWolfmax4k = futures.stream()
                .flatMap(future -> {
                    try {
                        return future.get().stream();
                    } catch (InterruptedException | ExecutionException e) {
                        log.error("Error retrieving results", e);
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());

        executorService.shutdown();
        return allWolfmax4k;
    }

    private List<Index> getIndexFromPage(String path, Index.Type type, Index.Quality quality) {
        log.info("Calling To Wolfmax4K with url path {}", path);
        List<Index> indexes = new ArrayList<>();
        String html = callWithFlaresolverr(WOLFMAX4K_URL + path).getSolution().getResponse();
        Document document = Jsoup.parse(html);
        Elements colLg2Elements = document.getElementsByClass("col-lg-2");

        List<Future<Index>> futures = new ArrayList<>();
        for (Element colLg2 : colLg2Elements) {
            if (Objects.requireNonNull(colLg2.children().first()).tagName().equals("a")) {
                futures.add(executorService.submit(() -> {
                    Index index = new Index();
                    index.setName(colLg2.getElementsByTag("h3").text());
                    String url = Objects.requireNonNull(colLg2.getElementsByTag("a").first()).attr("href");
                    index.setUrl(WOLFMAX4K_URL + url);
                    String htmlIndividual = callWithFlaresolverr(index.getUrl()).getSolution().getResponse();
                    Document documentIndividual = Jsoup.parse(htmlIndividual);
                    index.setIndexName(documentIndividual.getElementsByClass("h3 fw-semibold mb-1").first().text());
                    index.setDomain(DOMAIN_WOLFMAX4K);
                    String imageUrl = Objects.requireNonNull(colLg2.getElementsByClass("img-fluid rounded-1").first()).attr("src");
                    index.setImage(getByteArrayFromImageURL(HTTPS + imageUrl));
                    index.setQuality(quality);
                    index.setCreateTime(new Date());
                    index.setType(type);
                    return index;
                }));
            }
        }

        for (Future<Index> future : futures) {
            try {
                indexes.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error retrieving index", e);
            }
        }

        log.info("Finish To call Wolfmax4K with url {}", path);
        return indexes;
    }

    private List<Index> getIndexFromMultiPage(String path, Index.Type type, Index.Quality quality) {
        log.info("Calling To Wolfmax4K with url path {}", path);
        List<Index> indexes = new ArrayList<>();
        String html = callWithFlaresolverr(WOLFMAX4K_URL + path).getSolution().getResponse();
        Document document = Jsoup.parse(html);
        Elements colLg2Elements = document.getElementsByClass("col-lg-2");

        List<Future<List<Index>>> futures = new ArrayList<>();
        for (Element colLg2 : colLg2Elements) {
            if (Objects.requireNonNull(colLg2.children().first()).tagName().equals("a")) {
                futures.add(executorService.submit(() -> {
                    List<Index> localIndexes = new ArrayList<>();
                    String urlForTemps = Objects.requireNonNull(colLg2.getElementsByTag("a").first()).attr("href");
                    String htmlShowWithTemps = callWithFlaresolverr(WOLFMAX4K_URL + urlForTemps).getSolution().getResponse();
                    Document documentShowWithTemps = Jsoup.parse(htmlShowWithTemps);
                    Elements tempElements = documentShowWithTemps.getElementsByClass("row gx-lg-4 gx-0");
                    for (Element tempElement : tempElements) {
                        Index index = new Index();
                        index.setName(colLg2.getElementsByTag("h3").text());
                        Elements elementsa = tempElement.getElementsByTag("a");
                        for (Element elementA : elementsa) {
                            String htmlShowChapter = callWithFlaresolverr(WOLFMAX4K_URL + elementA.attr("href")).getSolution().getResponse();
                            Document documentShowChapter = Jsoup.parse(htmlShowChapter);
                            index.setUrl(WOLFMAX4K_URL + elementA.attr("href"));
                            index.setIndexName(Objects.requireNonNull(documentShowChapter.getElementsByClass("h3 fw-semibold mb-1").first()).text());
                            index.setDomain(DOMAIN_WOLFMAX4K);
                            String imageUrl = Objects.requireNonNull(documentShowChapter.getElementsByClass("img-fluid rounded-1").first()).attr("src");
                            index.setImage(getByteArrayFromImageURL(HTTPS + imageUrl));
                            index.setQuality(quality);
                            index.setCreateTime(new Date());
                            index.setType(type);
                            localIndexes.add(index);
                        }
                    }
                    return localIndexes;
                }));
            }
        }

        for (Future<List<Index>> future : futures) {
            try {
                indexes.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error retrieving index", e);
            }
        }

        log.info("Finish To call Wolfmax4K with url {}", path);
        return indexes;
    }

    private FlaresolverrIDTO callWithFlaresolverr(String url) {
        log.info("Calling to flaresolverr for url {}", url);
        FlaresolverrODTO flaresolverrODTO = new FlaresolverrODTO();
        flaresolverrODTO.setCmd("request.get");
        flaresolverrODTO.setMaxTimeout(600000);
        flaresolverrODTO.setUrl(url);
        RestClient restClient = RestClient.create();
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
        } catch (Exception e) {
            log.error(e.toString());
            return url;
        }
    }
}
