package com.seliverstov.popularmovies.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 30.10.2015.
 */
public class Videos extends BaseResult {
    @SerializedName("results")
    @Expose
    private List<Video> results = new ArrayList<>();

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }
}
