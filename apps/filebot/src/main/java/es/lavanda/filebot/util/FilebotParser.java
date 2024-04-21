package es.lavanda.filebot.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.lavanda.filebot.model.Filebot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FilebotParser {

    // @Autowired
    // private NotificationService notificationService;

    public List<Filebot> parseHtml(String html) {
        log.info("Parsing html");
        List<Filebot> filebots = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element table = doc.selectFirst("table");
        Iterator<Element> row = table.select("tr").iterator();
        row.next();
        while (row.hasNext()) {
            Iterator<Element> ite = ((Element) row.next()).select("td").iterator();
            Filebot filebot = new Filebot();
            filebot.setOriginalName(ite.next().text());
            filebot.setNewName(ite.next().text());
            filebot.setNewLocation(ite.next().text());
            filebots.add(filebot);
            if (filebot.getNewLocation().contains("Unsorted")) {
                filebot.setUnsorted(true);
                // notificationService.send(SnsTopic.TELEGRAM_MESSAGE, "Unsorted file " + filebot.getOriginalName(),
                //         "filebot-parser");
            }
        }
        return filebots;
    }

}
