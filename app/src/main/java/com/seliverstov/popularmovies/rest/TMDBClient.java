package com.seliverstov.popularmovies.rest;

import android.util.Log;

import com.seliverstov.popularmovies.rest.model.Movie;
import com.seliverstov.popularmovies.rest.model.Movies;
import com.seliverstov.popularmovies.rest.service.TMDBService;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by a.g.seliverstov on 12.10.2015.
 */
public class TMDBClient {
    public static final String LOG_TAG = TMDBClient.class.getSimpleName();
    public static final String BASE_URL = "https://api.themoviedb.org/";

    private TMDBService service;

    public TMDBClient(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(TMDBService.class);
    }

    public List<Movie> listMovies(String sort_by, int page) throws IOException {
        Call<Movies> call = this.service.getMovies(TMDBKey.API_KEY, sort_by, (page <= 0) ? 1 : page);
        Response<Movies> res = call.execute();
        Movies movies = res.body();
        return movies.getResults();
    }
}
