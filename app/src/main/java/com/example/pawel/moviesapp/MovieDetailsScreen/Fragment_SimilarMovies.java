package com.example.pawel.moviesapp.MovieDetailsScreen;

import android.app.Fragment;
import android.content.Intent;
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
import com.example.pawel.moviesapp.Utilities.Models.UniqueModel;
import com.example.pawel.moviesapp.Utilities.UniqueAdapter;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    private List<UniqueModel> uniqueModelList = new ArrayList<>();
    private UniqueAdapter customAdapter;

    @BindView(R.id.unique_title_textView)
    TextView textViewTitle;
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
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        customAdapter = new UniqueAdapter(getActivity().getApplicationContext(), uniqueModelList);
        recyclerView.setAdapter(customAdapter);

        customAdapter.setOnItemClickListener(new UniqueAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                UniqueModel uniqueModel = customAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), Activity_MovieDetails.class);
                intent.putExtra(Variables.INTENT_MOVIE_ID, uniqueModel.getId());
                intent.putExtra(Variables.INTENT_MOVIE_TITLE, uniqueModel.getTitle());
                intent.putExtra(Variables.INTENT_MOVIE_POSTAR_PATH, uniqueModel.getPosterPath());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position, View v) {
            }
        });


        int movieID = getActivity().getIntent().getIntExtra(Variables.INTENT_MOVIE_ID, 0);
        if (movieID != 0) {
            //detect internet and show the data
            if(InternetConnection.isNetworkStatusAvialable (getActivity().getApplicationContext())) {
                asyncCaller = new AsyncCaller();
                asyncCaller.execute(movieID);
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
                textViewTitle.setText(R.string.similarMovies);
                textViewTitle.setVisibility(View.VISIBLE);

                for (final MovieDb movieDb : result) {
                    UniqueModel uniqueModel = new UniqueModel(movieDb.getId(), movieDb.getTitle(), movieDb.getPosterPath());
                    uniqueModelList.add(uniqueModel);
                }

                customAdapter.notifyDataSetChanged();
            }
        }
    }
}


