package com.example.pawel.moviesapp.MovieDetails;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.ItemCreatorOnUniqueList;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * Created by Paweł on 2017-06-25.
 */

public class Fragment_SimilarMovies extends Fragment {
    private int page = 1;
    private AsyncCaller asyncCaller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.unique_for_every_list_with_image_and_title, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        int movieID = getActivity().getIntent().getIntExtra(Variables.INTENT_MOVIE_ID, 0);

        if (movieID != 0) {
            asyncCaller = new AsyncCaller();
            asyncCaller.execute(movieID);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (asyncCaller != null)
            asyncCaller.cancel(true);
    }

    private class AsyncCaller extends AsyncTask<Integer, Void, List<MovieDb>> {

        @Override
        protected List<MovieDb> doInBackground(Integer... integers) {
            if (!asyncCaller.isCancelled()) {
                int movieID = integers[0];
                //returns similar movies
                TmdbMovies tmdbMovies = new TmdbApi(Variables.API_KEY).getMovies();
                MovieResultsPage movieResultsPage = tmdbMovies.getSimilarMovies(movieID, Variables.LANGUAGE, page);
                return movieResultsPage.getResults();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<MovieDb> result) {
            if (!asyncCaller.isCancelled() && result != null && result.size() != 0) {
                //set title for this section
                TextView textViewTitle = (TextView) getView().findViewById(R.id.unique_title_textView);
                textViewTitle.setText(R.string.similarMovies);
                textViewTitle.setVisibility(View.VISIBLE);

                LinearLayout yourLayout = (LinearLayout) getView().findViewById(R.id.unique_linearLayout);
                ItemCreatorOnUniqueList itemCreatorOnUniqueList = new ItemCreatorOnUniqueList(yourLayout, getActivity());

                //add similar movies to the linear layout
                for (final MovieDb movieDb : result) {
                    View view = itemCreatorOnUniqueList.createItem(movieDb.getTitle(), movieDb.getPosterPath());

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

                    yourLayout.addView(view);
                }
            }
        }
    }
}


