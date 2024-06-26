package es.lavanda.downloader.bt4g.model;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@XmlRootElement(name = "rss")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rss {
    @XmlElement(name = "channel")
    private Channel channel;

    @Data
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Channel {
        private String title;
        private String description;
        private String link;

        @XmlElement(name = "item")
        private List<Item> item;

        @Data
        @NoArgsConstructor
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Item {
            private String title;
            private String link;

            @XmlElement(name = "guid")
            private Guid guid;
            private String pubDate;
            private String description;

            @Data
            @NoArgsConstructor
            @XmlAccessorType(XmlAccessType.FIELD)
            public static class Guid {
                @XmlAttribute(name = "isPermaLink")
                private boolean isPermaLink;

                @XmlValue
                private String value;
            }
        }
    }
}