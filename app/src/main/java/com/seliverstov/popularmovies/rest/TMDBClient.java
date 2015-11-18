package com.seliverstov.popularmovies.rest;

import android.util.Log;

import com.seliverstov.popularmovies.rest.model.Movie;
import com.seliverstov.popularmovies.rest.model.BaseResult;
import com.seliverstov.popularmovies.rest.model.Movies;
import com.seliverstov.popularmovies.rest.model.Review;
import com.seliverstov.popularmovies.rest.model.Reviews;
import com.seliverstov.popularmovies.rest.model.Video;
import com.seliverstov.popularmovies.rest.model.Videos;
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
    public static final String SORT_ORDER_POPULARIRY_DESC = "popularity.desc";
    public static final String SORT_ORDER_VOTE_AVERAGE_DESC = "vote_average.desc";

    private TMDBService service;

    public TMDBClient(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(TMDBService.class);
    }

    public List<Movie> listMovies(String sort_by, int page) throws IOException {
        Call<Movies> call = this.service.getMovies(TMDBKey.API_KEY, sort_by, (page <= 0) ? 1 : page);
        Response<Movies> res = call.execute();
        Log.i(LOG_TAG,res.raw().request().urlString());
        Movies result = res.body();
        return result.getResults();
    }

    public List<Review> listReviews(String movieId)  throws IOException {
        Call<Reviews> call = this.service.getReviews(movieId, TMDBKey.API_KEY);
        Response<Reviews> res =  call.execute();
        Log.i(LOG_TAG,res.raw().request().urlString());
        Reviews result = res.body();
        return result.getResults();
    }

    public List<Video> listVideos(String movieId)  throws IOException {
        Call<Videos> call = this.service.getVideos(movieId, TMDBKey.API_KEY);
        Response<Videos> res =  call.execute();
        Log.i(LOG_TAG,res.raw().request().urlString());
        Videos result = res.body();
        return result.getResults();
    }
}
