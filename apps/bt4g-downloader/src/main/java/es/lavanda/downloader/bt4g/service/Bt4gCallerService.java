package es.lavanda.downloader.bt4g.service;

import java.io.StringReader;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.lavanda.downloader.bt4g.exception.Bt4gException;
import es.lavanda.downloader.bt4g.model.Bt4g;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import es.lavanda.lib.common.model.flaresolverr.input.FlaresolverrIDTO;
import es.lavanda.lib.common.model.flaresolverr.output.FlaresolverrODTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class Bt4gCallerService {

    private final String BT4ORG_URL = "https://bt4gprx.com";

    @Value("${flaresolverr.url}")
    private String FLARESOLVERR_URL;

    private final MagnetService magnetService;


    public List<Bt4g> callToBT4G(String search) {
        log.info("Call To BT4G");
        List<Bt4g> allBt4g = new ArrayList<>();
        boolean hasNextPage = true;
        int currentPage = 1;

        while (hasNextPage) {
            String html = callWithFlaresolverr(BT4ORG_URL + "/search?q=" + search + "&p=" + currentPage).getSolution().getResponse();
            String rssString = callWithFlaresolverr(BT4ORG_URL + "/search?q=" + search + "&p=" + currentPage + "&page=rss").getSolution().getResponse();

            Document document = Jsoup.parse(html);
            if (document.toString().contains("Web server is returning an unknown error")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Web server is returning an unknown error");
            }

            Elements magnetObjects = document.getElementsByTag("h5");
            for (Element magnetObject : magnetObjects) {
                Bt4g bt4g = new Bt4g();
                bt4g.setName(magnetObject.child(0).attr("title"));
                bt4g.setUrl(BT4ORG_URL + magnetObject.child(0).attr("href"));
                String hash = getHash(rssString, bt4g.getName());
                bt4g.setMagnetHash(hash);
                bt4g.setMagnet(magnetService.getMagnetWithTrackers(hash.toUpperCase(), bt4g.getName()));
                bt4g.setSeeders(Integer.parseInt(magnetObject.parent().getElementById("seeders").text()));
                bt4g.setLeechers(Integer.parseInt(magnetObject.parent().getElementById("leechers").text()));
                if (Objects.nonNull(magnetObject.parent().getElementsByClass("cpill red-pill").first())) {
                    bt4g.setFileSize(magnetObject.parent().getElementsByClass("cpill red-pill").first().text());
                } else if (Objects.nonNull(magnetObject.parent().getElementsByClass("cpill yellow-pill").first())) {
                    bt4g.setFileSize(magnetObject.parent().getElementsByClass("cpill yellow-pill").first().text());
                } else if (Objects.nonNull(magnetObject.parent().getElementsByClass("cpill blue-pill").first())) {
                    bt4g.setFileSize(magnetObject.parent().getElementsByClass("cpill blue-pill").first().text());
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                bt4g.setCreateTime(Date.valueOf(
                        LocalDate.parse(
                                magnetObject.parent().html().split("<span>Create Time:&nbsp;<b>")[1].split("</b>")[0],
                                formatter)));
                Elements fileElements = magnetObject.parent().getElementsByTag("ul").first().getElementsByTag("li");
                List<String> files = new ArrayList<>();
                for (Element fileElement : fileElements) {
                    files.add(fileElement.ownText().split("&nbsp;")[0].trim());
                }
                bt4g.setFiles(files);
                allBt4g.add(bt4g);
            }
            Elements pagination = document.getElementsByClass("pagination");
            if (pagination.isEmpty() || pagination.select(".active + li").isEmpty()) {
                hasNextPage = false;
            } else {
                currentPage++;
            }
        }

        log.info("Finish call To BT4G");
        return allBt4g;
    }


    private String getHash(String rssString, String name) {
        log.info("Get hash with name {}", name);
        Document documentRss = Jsoup.parse(rssString, "", Parser.xmlParser());
        Element rssElement = documentRss.selectFirst("rss");
        if (Objects.isNull(rssElement)) {
            throw new Bt4gException("No se encontró el elemento <rss> en el HTML");
        }
        Elements items = rssElement.getElementsByTag("item");
        for (Element item : items) {
            if (item.getElementsByTag("title").text().replaceAll(" ", "").equals(name.replaceAll(" ", ""))) {
                return item.getElementsByTag("link").first().text().split("urn:btih:")[1].split("&")[0];
            }
        }
        throw new Bt4gException("Not found hash");
    }

    private String getHash(String url) {
        String html = callWithFlaresolverr(url).getSolution().getResponse();
        return html.split("urn:btih:")[1].split("&")[0];
    }

    private RestClient configureRestClient() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        return RestClient.create(restTemplate);
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000); // 60 seconds
        factory.setReadTimeout(120000); // 120 seconds
        return factory;
    }

    private FlaresolverrIDTO callWithFlaresolverr(String url) {
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
            byte[] imageBytes = IOUtils.toByteArray(new URL(url));
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

}
