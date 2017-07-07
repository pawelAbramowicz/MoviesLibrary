package com.example.pawel.moviesapp.MovieDetailsScreen;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pawel.moviesapp.ActorDetailsScreen.Activity_ActorDetails;
import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.Models.UniqueModel;
import com.example.pawel.moviesapp.Utilities.UniqueAdapter;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.people.PersonCast;

/**
 * Created by Paweł on 2017-05-28.
 */

public class Fragment_ActorsInMovieDetails extends Fragment {

    private UniqueAdapter customAdapter;
    private List<UniqueModel> uniqueModelList = new ArrayList<>();

    @BindView(R.id.recyclerViewer)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unique_for_every_list_with_image_and_title, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setRetainInstance(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        customAdapter = new UniqueAdapter(getActivity().getApplicationContext(), uniqueModelList);
        recyclerView.setAdapter(customAdapter);

        customAdapter.setOnItemClickListener(new UniqueAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                UniqueModel uniqueModel = customAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), Activity_ActorDetails.class);
                intent.putExtra(Variables.INTENT_ACTOR_ID, uniqueModel.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });

    }

    public void updateActors(List<PersonCast> cast) {
        for (final PersonCast personCast : cast) {
            UniqueModel uniqueMoel = new UniqueModel(personCast.getId(),
                    personCast.getName() + "\n" + getString(R.string.as) + "\n" + personCast.getCharacter(),
                    personCast.getProfilePath());
            uniqueModelList.add(uniqueMoel);
        }


        customAdapter.notifyDataSetChanged();
    }
}
