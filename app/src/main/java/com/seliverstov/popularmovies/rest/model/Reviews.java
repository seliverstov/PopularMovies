package com.seliverstov.popularmovies.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 30.10.2015.
 */
public class Reviews extends BaseResult {
    @SerializedName("results")
    @Expose
    private List<Review> results = new ArrayList<>();

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }
}
