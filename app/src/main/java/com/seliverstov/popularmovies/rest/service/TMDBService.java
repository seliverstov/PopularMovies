package com.seliverstov.popularmovies.rest.service;

import com.seliverstov.popularmovies.rest.model.BaseResult;
import com.seliverstov.popularmovies.rest.model.Movies;
import com.seliverstov.popularmovies.rest.model.Reviews;
import com.seliverstov.popularmovies.rest.model.Videos;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by a.g.seliverstov on 12.10.2015.
 */
public interface TMDBService {
    @GET("/3/discover/movie")
    Call<Movies> getMovies(@Query("api_key") String api_key, @Query("sort_by") String sort, @Query("page") int page);

    @GET("/3/movie/{id}/reviews ")
    Call<Reviews> getReviews(@Path("id") String id, @Query("api_key") String api_key);

    @GET("3/movie/{id}/videos")
    Call<Videos> getVideos(@Path("id") String id, @Query("api_key") String api_key);
}
