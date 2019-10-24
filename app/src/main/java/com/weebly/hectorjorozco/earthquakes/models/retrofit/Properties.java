
package com.weebly.hectorjorozco.earthquakes.models.retrofit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "mag",
    "place",
    "time",
    "updated",
    "tz",
    "url",
    "detail",
    "felt",
    "cdi",
    "mmi",
    "alert",
    "status",
    "tsunami",
    "sig",
    "net",
    "code",
    "ids",
    "sources",
    "types",
    "nst",
    "dmin",
    "rms",
    "gap",
    "magType",
    "type",
    "title"
})
public class Properties {

    @JsonProperty("mag")
    private double mag;
    @JsonProperty("place")
    private String place;
    @JsonProperty("time")
    private long time;
    @JsonProperty("updated")
    private long updated;
    @JsonProperty("tz")
    private int tz;
    @JsonProperty("url")
    private String url;
    @JsonProperty("detail")
    private String detail;
    @JsonProperty("felt")
    private int felt;
    @JsonProperty("cdi")
    private double cdi;
    @JsonProperty("mmi")
    private double mmi;
    @JsonProperty("alert")
    private String alert;
    @JsonProperty("status")
    private String status;
    @JsonProperty("tsunami")
    private int tsunami;
    @JsonProperty("sig")
    private int sig;
    @JsonProperty("net")
    private String net;
    @JsonProperty("code")
    private String code;
    @JsonProperty("ids")
    private String ids;
    @JsonProperty("sources")
    private String sources;
    @JsonProperty("types")
    private String types;
    @JsonProperty("nst")
    private int nst;
    @JsonProperty("dmin")
    private double dmin;
    @JsonProperty("rms")
    private double rms;
    @JsonProperty("gap")
    private double gap;
    @JsonProperty("magType")
    private String magType;
    @JsonProperty("type")
    private String type;
    @JsonProperty("title")
    private String title;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Properties() {
    }

    public Properties(long mag, String place, long time, long updated, int tz, String url,
                      String detail, int felt, double cdi, double mmi, String alert, String status,
                      int tsunami, int sig, String net, String code, String ids, String sources,
                      String types, int nst, double dmin, double rms, double gap, String magType,
                      String type, String title) {
        super();
        this.mag = mag;
        this.place = place;
        this.time = time;
        this.updated = updated;
        this.tz = tz;
        this.url = url;
        this.detail = detail;
        this.felt = felt;
        this.cdi = cdi;
        this.mmi = mmi;
        this.alert = alert;
        this.status = status;
        this.tsunami = tsunami;
        this.sig = sig;
        this.net = net;
        this.code = code;
        this.ids = ids;
        this.sources = sources;
        this.types = types;
        this.nst = nst;
        this.dmin = dmin;
        this.rms = rms;
        this.gap = gap;
        this.magType = magType;
        this.type = type;
        this.title = title;
    }

    @JsonProperty("mag")
    public double getMag() {
        return mag;
    }

    @JsonProperty("mag")
    public void setMag(double mag) {
        this.mag = mag;
    }

    @JsonProperty("place")
    public String getPlace() {
        return place;
    }

    @JsonProperty("place")
    public void setPlace(String place) {
        this.place = place;
    }

    @JsonProperty("time")
    public long getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(long time) {
        this.time = time;
    }

    @JsonProperty("updated")
    public long getUpdated() {
        return updated;
    }

    @JsonProperty("updated")
    public void setUpdated(long updated) {
        this.updated = updated;
    }

    @JsonProperty("tz")
    public int getTz() {
        return tz;
    }

    @JsonProperty("tz")
    public void setTz(int tz) {
        this.tz = tz;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("detail")
    public String getDetail() {
        return detail;
    }

    @JsonProperty("detail")
    public void setDetail(String detail) {
        this.detail = detail;
    }

    @JsonProperty("felt")
    public int getFelt() {
        return felt;
    }

    @JsonProperty("felt")
    public void setFelt(int felt) {
        this.felt = felt;
    }

    @JsonProperty("cdi")
    public double getCdi() {
        return cdi;
    }

    @JsonProperty("cdi")
    public void setCdi(double cdi) {
        this.cdi = cdi;
    }

    @JsonProperty("mmi")
    public double getMmi() {
        return mmi;
    }

    @JsonProperty("mmi")
    public void setMmi(double mmi) {
        this.mmi = mmi;
    }

    @JsonProperty("alert")
    public String getAlert() {
        return alert;
    }

    @JsonProperty("alert")
    public void setAlert(String alert) {
        this.alert = alert;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("tsunami")
    public int getTsunami() {
        return tsunami;
    }

    @JsonProperty("tsunami")
    public void setTsunami(int tsunami) {
        this.tsunami = tsunami;
    }

    @JsonProperty("sig")
    public int getSig() {
        return sig;
    }

    @JsonProperty("sig")
    public void setSig(int sig) {
        this.sig = sig;
    }

    @JsonProperty("net")
    public String getNet() {
        return net;
    }

    @JsonProperty("net")
    public void setNet(String net) {
        this.net = net;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("ids")
    public String getIds() {
        return ids;
    }

    @JsonProperty("ids")
    public void setIds(String ids) {
        this.ids = ids;
    }

    @JsonProperty("sources")
    public String getSources() {
        return sources;
    }

    @JsonProperty("sources")
    public void setSources(String sources) {
        this.sources = sources;
    }

    @JsonProperty("types")
    public String getTypes() {
        return types;
    }

    @JsonProperty("types")
    public void setTypes(String types) {
        this.types = types;
    }

    @JsonProperty("nst")
    public int getNst() {
        return nst;
    }

    @JsonProperty("nst")
    public void setNst(int nst) {
        this.nst = nst;
    }

    @JsonProperty("dmin")
    public double getDmin() {
        return dmin;
    }

    @JsonProperty("dmin")
    public void setDmin(double dmin) {
        this.dmin = dmin;
    }

    @JsonProperty("rms")
    public double getRms() {
        return rms;
    }

    @JsonProperty("rms")
    public void setRms(double rms) {
        this.rms = rms;
    }

    @JsonProperty("gap")
    public double getGap() {
        return gap;
    }

    @JsonProperty("gap")
    public void setGap(double gap) {
        this.gap = gap;
    }

    @JsonProperty("magType")
    public String getMagType() {
        return magType;
    }

    @JsonProperty("magType")
    public void setMagType(String magType) {
        this.magType = magType;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

}
