package com.seliverstov.popularmovies;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.seliverstov.popularmovies.rest.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by a.g.seliverstov on 12.10.2015.
 */
public class MoviesGridFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = inflater.getContext();
        View view = inflater.inflate(R.layout.fragment_movies_grid,container,false);
        final GridView gv = (GridView)view.findViewById(R.id.movies_grid);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });



        final ArrayAdapter<Movie> adapter = new ArrayAdapter<Movie>(context,android.R.layout.simple_list_item_1){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.i(this.getClass().toString(),"Get View for Item "+position);
                ImageView imageView;
                if (convertView==null) {
                    imageView = new ImageView(context);
                    imageView.setLayoutParams(new GridView.LayoutParams(342,513));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }else{
                    imageView = (ImageView)convertView;
                }

                Movie m = getItem(position);
                String url = "http://image.tmdb.org/t/p/w342/" + m.getPosterPath();
                Log.i(this.getClass().toString(), url);
                Picasso.with(context).load(url).into(imageView);

                return imageView;
            }
        };



        gv.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int page = 0;
            private int visibleTreshold = 2;
            private AsyncTask<Integer,Void,List<Movie>> loadTask;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (loadTask == null || loadTask.getStatus() == AsyncTask.Status.FINISHED){
                    if (totalItemCount - visibleItemCount <= (firstVisibleItem + visibleTreshold)) {
                        Log.i(this.getClass().getSimpleName(),"Load page "+page);
                        page++;
                        AsyncTask<Integer, Void, List<Movie>> aTask = new LoadMoviesTask(adapter);
                        aTask.execute(page);
                    }
                }

            }
        });

        gv.setAdapter(adapter);
        return view;
    }
}
