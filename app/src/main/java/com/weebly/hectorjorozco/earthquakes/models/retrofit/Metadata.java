
package com.weebly.hectorjorozco.earthquakes.models.retrofit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "generated",
    "url",
    "title",
    "status",
    "api",
    "limit",
    "offset",
    "count"
})
public class Metadata {

    @JsonProperty("generated")
    private long generated;
    @JsonProperty("url")
    private String url;
    @JsonProperty("title")
    private String title;
    @JsonProperty("status")
    private int status;
    @JsonProperty("api")
    private String api;
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("offset")
    private int offset;
    @JsonProperty("count")
    private int count;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Metadata() {
    }

    public Metadata(long generated, String url, String title, int status, String api, int limit, int offset, int count) {
        super();
        this.generated = generated;
        this.url = url;
        this.title = title;
        this.status = status;
        this.api = api;
        this.limit = limit;
        this.offset = offset;
        this.count = count;
    }

    @JsonProperty("generated")
    public long getGenerated() {
        return generated;
    }

    @JsonProperty("generated")
    public void setGenerated(long generated) {
        this.generated = generated;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("status")
    public long getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = status;
    }

    @JsonProperty("api")
    public String getApi() {
        return api;
    }

    @JsonProperty("api")
    public void setApi(String api) {
        this.api = api;
    }

    @JsonProperty("limit")
    public long getLimit() {
        return limit;
    }

    @JsonProperty("limit")
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @JsonProperty("offset")
    public long getOffset() {
        return offset;
    }

    @JsonProperty("offset")
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @JsonProperty("count")
    public long getCount() {
        return count;
    }

    @JsonProperty("count")
    public void setCount(int count) {
        this.count = count;
    }

}
