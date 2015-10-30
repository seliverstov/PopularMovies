package com.seliverstov.popularmovies.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Alexander on 30.10.2015.
 */
public class Video implements Serializable{
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("iso_639_1")
    @Expose
    private String iso6391;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("type")
    @Expose
    private String type;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getIso6391() {
        return iso6391;
    }


    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }


    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getSite() {
        return site;
    }


    public void setSite(String site) {
        this.site = site;
    }


    public Integer getSize() {
        return size;
    }


    public void setSize(Integer size) {
        this.size = size;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }
}
