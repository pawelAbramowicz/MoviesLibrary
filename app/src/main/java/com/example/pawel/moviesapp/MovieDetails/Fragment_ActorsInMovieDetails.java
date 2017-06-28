package com.example.pawel.moviesapp.MovieDetails;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.pawel.moviesapp.ActorDetails.Activity_ActorDetails;
import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.ItemCreatorOnUniqueList;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.List;

import info.movito.themoviedbapi.model.people.PersonCast;

/**
 * Created by Paweł on 2017-05-28.
 */

public class Fragment_ActorsInMovieDetails extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.unique_for_every_list_with_image_and_title, parent, false);
    }

    public void updateActors(List<PersonCast> cast) {

        LinearLayout yourLayout = (LinearLayout) getView().findViewById(R.id.unique_linearLayout);
        ItemCreatorOnUniqueList itemCreatorOnUniqueList = new ItemCreatorOnUniqueList(yourLayout, getActivity());

        //add actors played in movie to the linear layout
        for (final PersonCast personCast : cast) {
            View view = itemCreatorOnUniqueList.createItem
                    (personCast.getName() + "\n" + getString(R.string.as) + "\n" + personCast.getCharacter(), personCast.getProfilePath());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), Activity_ActorDetails.class);
                    intent.putExtra(Variables.INTENT_ACTOR_ID, personCast.getId());
                    startActivity(intent);
                }
            });

            yourLayout.addView(view);
        }
    }
}
