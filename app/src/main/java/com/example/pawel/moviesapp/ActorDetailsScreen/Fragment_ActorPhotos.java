package com.example.pawel.moviesapp.ActorDetailsScreen;

import android.app.Dialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.InternetConnection;
import com.example.pawel.moviesapp.Utilities.ItemCreatorOnUniqueList;
import com.example.pawel.moviesapp.Utilities.Models.UniqueModel;
import com.example.pawel.moviesapp.Utilities.UniqueAdapter;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.Artwork;

/**
 * Created by Paweł on 2017-06-23.
 */

public class Fragment_ActorPhotos extends Fragment {

    private AsyncCaller asyncCaller;
    private Dialog dialogWindow;
    private List<UniqueModel> uniqueModelList = new ArrayList<>();
    private UniqueAdapter customAdapter;

    @BindView(R.id.recyclerViewer)
    RecyclerView recyclerView;
    @BindView(R.id.unique_title_textView)
    TextView titleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unique_for_every_list_with_image_and_title, parent, false);
        //bind layout with variables
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //set recycler view as horizontal linear layout
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        //set adapter for recyclerView
        customAdapter = new UniqueAdapter(getActivity().getApplicationContext(), uniqueModelList);
        recyclerView.setAdapter(customAdapter);

        //add listeners
        customAdapter.setOnItemClickListener(new UniqueAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                UniqueModel uniqueModel = customAdapter.getItem(position);

                //image increasing after click
                ItemCreatorOnUniqueList itemCreatorOnUniqueList = new ItemCreatorOnUniqueList(getActivity());
                itemCreatorOnUniqueList.addIncreasingImageListener(v, uniqueModel.getPosterPath());
            }

            @Override
            public void onItemLongClick(int position, View v) {
            }
        });

        int movieId = getActivity().getIntent().getIntExtra(Variables.INTENT_ACTOR_ID, 0);
        if (movieId != 0) {
            //detect internet and show the data
            if(InternetConnection.isNetworkStatusAvialable (getActivity().getApplicationContext())) {
                asyncCaller = new AsyncCaller();
                asyncCaller.execute(movieId);
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
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
                titleTextView.setText(R.string.pictures);
                titleTextView.setVisibility(View.VISIBLE);

                //add actor photos to the list
                for (final Artwork artwork : result) {
                    UniqueModel uniqueModel = new UniqueModel(0, "", artwork.getFilePath());
                    uniqueModelList.add(uniqueModel);
                }


                //notify adapter about new elements on the list
                customAdapter.notifyDataSetChanged();
            }
        }
    }
}


