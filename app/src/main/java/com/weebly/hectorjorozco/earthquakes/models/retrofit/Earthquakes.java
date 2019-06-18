
package com.weebly.hectorjorozco.earthquakes.models.retrofit;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "metadata",
    "features",
    "bbox"
})
public class Earthquakes {

    @JsonProperty("type")
    private String type;
    @JsonProperty("metadata")
    private Metadata metadata;
    @JsonProperty("features")
    private List<Feature> features = null;
    @JsonProperty("bbox")
    private List<Double> bbox = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Earthquakes() {
    }

    /**
     * 
     * @param bbox
     * @param features
     * @param type
     * @param metadata
     */
    public Earthquakes(String type, Metadata metadata, List<Feature> features, List<Double> bbox) {
        super();
        this.type = type;
        this.metadata = metadata;
        this.features = features;
        this.bbox = bbox;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("metadata")
    public Metadata getMetadata() {
        return metadata;
    }

    @JsonProperty("metadata")
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @JsonProperty("features")
    public List<Feature> getFeatures() {
        return features;
    }

    @JsonProperty("features")
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    @JsonProperty("bbox")
    public List<Double> getBbox() {
        return bbox;
    }

    @JsonProperty("bbox")
    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

}
