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
    public static final String BASE_URL = "https://api.themoviedb.org/";
    public static final String POPULAR_MOVIES = "popularity.desc";
    public static final String MOST_RATED_MOVIES = "vote_average.desc";

    private TMDBService service;


    public TMDBService getService(){
        return service;
    }

    public TMDBClient(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(TMDBService.class);
    }

    public List<Movie> listMovies(String sort_by, int page){
        try{
            Call<Movies> call = this.service.getMovies(TMDBKey.API_KEY, sort_by, (page <= 0) ? 1 : page);
            Response<Movies> res = call.execute();
            Log.i(this.getClass().getSimpleName(),res.raw().request().urlString());
            Movies movies = res.body();
            return movies.getResults();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Movie> listMostPopularMovies(int page){
        return listMovies(POPULAR_MOVIES,page);
    }

    public List<Movie> listMostRatedMovies(int page){
        return listMovies(MOST_RATED_MOVIES,page);
    }

    public static void main(String[] args) throws IOException {
        TMDBClient tmdb = new TMDBClient();
        System.out.println(tmdb.listMostRatedMovies(1));
        System.out.println(tmdb.listMostPopularMovies(1));
    }

}
