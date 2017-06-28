package com.example.pawel.moviesapp.ActorDetails;

import android.app.Fragment;
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
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.Artwork;

/**
 * Created by Paweł on 2017-06-23.
 */

public class Fragment_ActorPhotos extends Fragment {

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

    private class AsyncCaller extends AsyncTask<Integer, Void, List<Artwork>> {

        @Override
        protected List<Artwork> doInBackground(Integer... integers) {
            if (!asyncCaller.isCancelled()) {
                int id = integers[0];
                //returns actor photos
                TmdbPeople people = new TmdbApi(Variables.API_KEY).getPeople();
                List<Artwork> artworkList = people.getPersonImages(id);
                return artworkList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<Artwork> result) {
            if (!asyncCaller.isCancelled() && result != null && result.size() != 0) {
                //set title for this section
                TextView titleTextView = (TextView) getView().findViewById(R.id.unique_title_textView);
                titleTextView.setText(R.string.pictures);
                titleTextView.setVisibility(View.VISIBLE);

                LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.unique_linearLayout);
                ItemCreatorOnUniqueList itemCreatorOnUniqueList = new ItemCreatorOnUniqueList(linearLayout, getActivity());

                //add actor photos to the linear layout
                for (final Artwork artwork : result) {
                    View view = itemCreatorOnUniqueList.createItem("", artwork.getFilePath());
                    itemCreatorOnUniqueList.addIncreasingImageListener(artwork.getFilePath());
                    linearLayout.addView(view);
                }
            }
        }
    }
}


