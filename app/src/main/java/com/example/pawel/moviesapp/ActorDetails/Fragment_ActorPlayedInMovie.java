package com.example.pawel.moviesapp.ActorDetails;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pawel.moviesapp.MovieDetails.Activity_MovieDetails;
import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.ItemCreatorOnUniqueList;
import com.example.pawel.moviesapp.Utilities.Variables;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.people.PersonCredit;
import info.movito.themoviedbapi.model.people.PersonCredits;

/**
 * Created by Paweł on 2017-06-02.
 */

public class Fragment_ActorPlayedInMovie extends Fragment {

    private AsyncCaller asyncCaller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.unique_for_every_list_with_image_and_title, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        int movieId = getActivity().getIntent().getIntExtra(Variables.INTENT_ACTOR_ID, 0);
        if (movieId != 0) {
            asyncCaller = new AsyncCaller();
            asyncCaller.execute(movieId);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (asyncCaller != null)
            asyncCaller.cancel(true);
    }

    private class AsyncCaller extends AsyncTask<Integer, Void, PersonCredits> {

        @Override
        protected PersonCredits doInBackground(Integer... integers) {
            if (!asyncCaller.isCancelled()) {
                int id = integers[0];
                //returns list of movies in which an actor played
                TmdbPeople people = new TmdbApi(Variables.API_KEY).getPeople();
                PersonCredits personCredits = people.getPersonCredits(id);
                return personCredits;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final PersonCredits result) {
            if (!asyncCaller.isCancelled() && result != null) {
                //set title for this section
                TextView textViewTitle = (TextView) getView().findViewById(R.id.unique_title_textView);
                textViewTitle.setText(R.string.filmography);
                textViewTitle.setVisibility(View.VISIBLE);

                LinearLayout yourLayout = (LinearLayout) getView().findViewById(R.id.unique_linearLayout);
                ItemCreatorOnUniqueList itemCreatorOnUniqueList = new ItemCreatorOnUniqueList(yourLayout, getActivity());

                //add movies to the linear layout
                for (final PersonCredit personCredit : result.getCast()) {
                    View view = itemCreatorOnUniqueList.createItem(personCredit.getMovieTitle(), personCredit.getPosterPath());
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), Activity_MovieDetails.class);
                            intent.putExtra(Variables.INTENT_MOVIE_ID, personCredit.getId());
                            intent.putExtra(Variables.INTENT_MOVIE_TITLE, personCredit.getMovieTitle());
                            intent.putExtra(Variables.INTENT_MOVIE_POSTAR_PATH, personCredit.getPosterPath());
                            startActivity(intent);
                        }
                    });
                    yourLayout.addView(view);
                }
            }
        }
    }
}



