package com.seliverstov.popularmovies.rest;

import android.test.AndroidTestCase;

import com.seliverstov.popularmovies.rest.model.Movie;
import com.seliverstov.popularmovies.rest.model.Review;
import com.seliverstov.popularmovies.rest.model.Video;

import java.util.List;

/**
 * Created by Alexander on 30.10.2015.
 */
public class TMDBClientTest extends AndroidTestCase {
    private static final String MOVIE_ID_FOR_REVIEWS = "257344";
    private static final String MOVIE_ID_FOR_VIDEOS = "211672";
    private static final String SORT_ORDER = "popularity.desc";
    TMDBClient client;

    public void setUp(){
        client = new TMDBClient();
    }

    public void testGetMovies() throws Throwable{
        List res = client.listMovies(SORT_ORDER, 1);
        assertNotNull(res);
        assertTrue(res.size()>0);
        Movie movie = (Movie)res.get(0);
        assertNotNull(movie.getId());
        assertNotNull(movie.getTitle());
        assertNotNull(movie.getOriginalTitle());
        assertNotNull(movie.getVoteAverage());
    }

    public void testGetReviews() throws Throwable{
        List res = client.listReviews(MOVIE_ID_FOR_REVIEWS);
        assertNotNull(res);
        assertTrue(res.size() > 0);
        assertEquals(res.size(),2);
        Review review =(Review)res.get(0);
        assertNotNull(review.getId());
        assertNotNull(review.getAuthor());
        assertNotNull(review.getContent());
        assertNotNull(review.getUrl());
    }

    public void testGetVideos() throws Throwable{
        List res = client.listVideos(MOVIE_ID_FOR_VIDEOS);
        assertNotNull(res);
        assertTrue(res.size()>0);
        Video video =(Video)res.get(0);
        assertNotNull(video.getId());
        assertNotNull(video.getIso6391());
        assertNotNull(video.getKey());
        assertNotNull(video.getName());
        assertNotNull(video.getSite());
        assertNotNull(video.getSite());
        assertNotNull(video.getType());
    }
}
