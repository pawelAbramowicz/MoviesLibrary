package com.example.pawel.moviesapp.MainScreen;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawel.moviesapp.MovieDetailsScreen.Activity_MovieDetails;
import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.DataBase;
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
 * Created by Paweł on 2017-05-26.
 */

public class Fragment_MostPopularMovies extends Fragment {

    private int page = 1;
    private AsyncCaller asyncCaller;
    private List<UniqueModel> uniqueModelList = new ArrayList<>();
    private UniqueAdapter customAdapter;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

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
        setRetainInstance(true);

        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);
        customAdapter = new UniqueAdapter(getActivity().getApplicationContext(), uniqueModelList);
        recyclerView.setAdapter(customAdapter);

        customAdapter.setOnItemClickListener(new UniqueAdapter.ClickListener() {


            @Override
            public void onItemClick(int position, View v) {
                //go to movie details after click

                UniqueModel uniqueModel = customAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), Activity_MovieDetails.class);
                intent.putExtra(Variables.INTENT_MOVIE_ID, uniqueModel.getId());
                intent.putExtra(Variables.INTENT_MOVIE_TITLE, uniqueModel.getTitle());
                intent.putExtra(Variables.INTENT_MOVIE_POSTAR_PATH, uniqueModel.getPosterPath());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position, View v) {
                //action on long click - add/remove movie from watch list

                UniqueModel uniqueModel = customAdapter.getItem(position);

                DataBase dataBase = new DataBase(getActivity().getApplicationContext()).open();
                if (dataBase.isContaining(uniqueModel.getId())) { //if contains delete movie from watch list
                    int result = dataBase.deleteMovie(uniqueModel.getId());
                    String text = dataBase.deleteMovieResultInterpreter(result);
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                } else {  //if does not contain add movie to watch list
                    long result = dataBase.insertMovie(uniqueModel.getId(), uniqueModel.getTitle(), uniqueModel.getPosterPath());
                    String text = dataBase.insertMovieResultInterpreter(result);
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                }
                dataBase.close();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx > 0) { //check for scroll right

                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        // End has been reached

                        Log.i("Yaeye!", "end called");

                        // Do something
                        //detect internet and show the data
                        if(InternetConnection.isNetworkStatusAvialable (getActivity().getApplicationContext())) {
                            asyncCaller = new AsyncCaller();
                            asyncCaller.execute();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                        loading = true;
                    }
                }
            }
        });


        //detect internet and show the data
        if (InternetConnection.isNetworkStatusAvialable(getActivity().getApplicationContext())) {
            asyncCaller = new AsyncCaller();
            asyncCaller.execute();
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
        }
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
                textViewTitle.setText(R.string.mostPopularMovies);
                textViewTitle.setVisibility(View.VISIBLE);

                for (final MovieDb movieDb : movieList) {
                    UniqueModel uniqueModel = new UniqueModel(movieDb.getId(), movieDb.getTitle(), movieDb.getPosterPath());
                    uniqueModelList.add(uniqueModel);
                }

                customAdapter.notifyDataSetChanged();
            }
        }
    }
}