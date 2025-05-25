package cn.edu.xidian.tafei_mall.model.vo.Response.Image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LskyResponse {
    private boolean status;
    private String message;
    private ResponseData data;

    @Setter
    @Getter
    public static class ResponseData {
        private String key;
        private String name;
        private String pathname;
        @JsonProperty("origin_name")
        private String originName;
        private Double size;
        private String mimetype;
        private String extension;
        private String md5;
        private String sha1;
        private Links links;
    }

    @Getter
    public static class Links {
        @JsonProperty("url")
        private String url;
        @JsonProperty("html")
        private String html;
        @JsonProperty("bbcode")
        private String bbcode;
        @JsonProperty("markdown")
        private String markdown;
        @JsonProperty("markdown_with_link")
        private String markdownWithLink;
        @JsonProperty("thumbnail_url")
        private String thumbnailUrl;
    }
}