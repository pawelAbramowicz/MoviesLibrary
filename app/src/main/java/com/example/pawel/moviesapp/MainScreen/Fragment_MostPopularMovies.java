package com.example.pawel.moviesapp.MainScreen;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawel.moviesapp.MovieDetails.Activity_MovieDetails;
import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.DataBase;
import com.example.pawel.moviesapp.Utilities.ItemCreatorOnUniqueList;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * Created by Paweł on 2017-05-26.
 */

public class Fragment_MostPopularMovies extends Fragment {

    private int page = 1;
    private AsyncCaller asyncCaller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.unique_for_every_list_with_image_and_title, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setRetainInstance(true);

        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) getView().findViewById(R.id.unique_horizontalScrollView);
        horizontalScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                float maxScrollX = horizontalScrollView.getChildAt(0).getMeasuredWidth() - horizontalScrollView.getMeasuredWidth();
                if (horizontalScrollView.getScrollX() == maxScrollX) {
                    asyncCaller = new AsyncCaller();
                    asyncCaller.execute();
                }
            }
        });

        asyncCaller = new AsyncCaller();
        asyncCaller.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (asyncCaller != null)
            asyncCaller.cancel(true);
    }

    private class AsyncCaller extends AsyncTask<Void, Void, List<MovieDb>> {

        @Override
        protected List<MovieDb> doInBackground(Void... voids) {
            if (!asyncCaller.isCancelled()) {
                //returns most popular movies
                TmdbMovies movies = new TmdbApi(Variables.API_KEY).getMovies();
                MovieResultsPage movie1 = movies.getPopularMovies(Variables.LANGUAGE, page);
                List<MovieDb> movieDbList = movie1.getResults();
                page += 1;
                return movieDbList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieDb> movieList) {
            if (!asyncCaller.isCancelled() && movieList != null && movieList.size() != 0) {
                //set title for this section
                TextView textViewTitle = (TextView) getView().findViewById(R.id.unique_title_textView);
                textViewTitle.setText(R.string.mostPopularMovies);
                textViewTitle.setVisibility(View.VISIBLE);

                LinearLayout yourLayout = (LinearLayout) getView().findViewById(R.id.unique_linearLayout);
                ItemCreatorOnUniqueList itemCreatorOnUniqueList = new ItemCreatorOnUniqueList(yourLayout, getActivity());

                //add most pupular movies to the linear layout
                for (final MovieDb movieDb : movieList) {
                    View view = itemCreatorOnUniqueList.createItem(movieDb.getTitle(), movieDb.getPosterPath());

                    //go to movie details after click
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), Activity_MovieDetails.class);
                            intent.putExtra(Variables.INTENT_MOVIE_ID, movieDb.getId());
                            intent.putExtra(Variables.INTENT_MOVIE_TITLE, movieDb.getTitle());
                            intent.putExtra(Variables.INTENT_MOVIE_POSTAR_PATH, movieDb.getPosterPath());
                            startActivity(intent);
                        }
                    });

                    //action on long click - add/remove movie from watch list
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            DataBase dataBase = new DataBase(getActivity().getApplicationContext()).open();
                            if (dataBase.isContaining(movieDb.getId())) { //if contains delete movie from watch list
                                int result = dataBase.deleteMovie(movieDb.getId());
                                String text = dataBase.deleteMovieResultInterpreter(result);
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT);
                                toast.show();
                            } else {  //if does not contain add movie to watch list
                                long result = dataBase.insertMovie(movieDb.getId(), movieDb.getTitle(), movieDb.getPosterPath());
                                String text = dataBase.insertMovieResultInterpreter(result);
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            dataBase.close();
                            return true;
                        }
                    });
                    yourLayout.addView(view);
                }
            }
        }
    }
}