package com.seliverstov.popularmovies.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 30.10.2015.
 */
public class Movies extends BaseResult {
    @SerializedName("results")
    @Expose
    private List<Movie> results = new ArrayList<>();

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
