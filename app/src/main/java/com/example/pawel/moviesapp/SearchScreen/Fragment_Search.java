package com.example.pawel.moviesapp.SearchScreen;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawel.moviesapp.MovieDetailsScreen.Activity_MovieDetails;
import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.InternetConnection;
import com.example.pawel.moviesapp.Utilities.ListAdapter;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * Created by Paweł on 2017-05-26.
 */

public class Fragment_Search extends Fragment {
    private int preLast;
    private ListAdapter customAdapter;
    private List<MovieDb> moviesListElements = new ArrayList<MovieDb>();
    private int page = 1;
    private String previousSearchingTitle = " ";
    private String actualSearchingTitle = "";
    private AsyncCaller asyncCaller;

    @BindView(R.id.search_noResultText)
    TextView textView;
    @BindView(R.id.search_searchField)
    TextView searchField;
    @BindView(R.id.list_view_search_list)
    ListView yourListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.search_searchButton)
    void search() {
        //hide keyboard and search movie after search button click
        hideKeyboard(getActivity());
        searchMovie();
    }

    @OnClick(R.id.search_clearButton)
    void clearTextField() {
        //clear edit searchField after click
        searchField.setText("");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setRetainInstance(true);

        customAdapter = new ListAdapter(getActivity().getApplicationContext(), R.layout.element_search_movie_row, moviesListElements);
        yourListView.setAdapter(customAdapter);

        yourListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                //called when reached last item
                if (lastItem == totalItemCount) {
                    if (preLast != lastItem) {
                        preLast = lastItem;
                        showMoviesFromNextPage();
                    }
                }
            }
        });

        //hide keyboard and search movie after search button click on keyboard
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard(getActivity());
                    searchMovie();
                    return true;
                }
                return false;
            }
        });
    }

    @OnItemClick(R.id.list_view_search_list)
    public void startMovieDetailsActivity(int position) {
        MovieDb movie = (MovieDb) yourListView.getAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), Activity_MovieDetails.class);
        intent.putExtra(Variables.INTENT_MOVIE_ID, movie.getId());
        intent.putExtra(Variables.INTENT_MOVIE_TITLE, movie.getTitle());
        intent.putExtra(Variables.INTENT_MOVIE_POSTAR_PATH, movie.getPosterPath());
        startActivity(intent);
    }


    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //if no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showMoviesFromNextPage() {
        //detect internet and show the data
        if(InternetConnection.isNetworkStatusAvialable (getActivity().getApplicationContext())) {
            asyncCaller = new AsyncCaller();
            asyncCaller.execute(actualSearchingTitle);
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchMovie() {
        actualSearchingTitle = searchField.getText().toString();

        if (!previousSearchingTitle.equals(actualSearchingTitle) && !actualSearchingTitle.equals("")) {
            //move selection to the top
            yourListView.setSelection(0);
            //clear list and move to first page
            moviesListElements.clear();
            page = 1;
            //search movies
            //detect internet and show the data
            if(InternetConnection.isNetworkStatusAvialable (getActivity().getApplicationContext())) {
                asyncCaller = new AsyncCaller();
                asyncCaller.execute(actualSearchingTitle);
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
        previousSearchingTitle = actualSearchingTitle;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (asyncCaller != null)
            asyncCaller.cancel(true);
    }

    private class AsyncCaller extends AsyncTask<String, Void, List<MovieDb>> {

        @Override
        protected List<MovieDb> doInBackground(String... strings) {

            if (!asyncCaller.isCancelled()) {
                //returns searching movies
                TmdbSearch tmdbSearch = new TmdbApi(Variables.API_KEY).getSearch();
                MovieResultsPage movieResultsPage = tmdbSearch.searchMovie(strings[0], null, Variables.LANGUAGE, true, page);
                List<MovieDb> movieDbList = movieResultsPage.getResults();
                page += 1;
                return movieDbList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieDb> result) {
            if (!asyncCaller.isCancelled() && result != null && result.size() != 0) {
                for (MovieDb movieDb : result) {
                    moviesListElements.add(movieDb);
                }
                customAdapter.notifyDataSetChanged();

                //display or hide information about no search result
                if (moviesListElements.isEmpty())
                    textView.setVisibility(View.VISIBLE);
                else
                    textView.setVisibility(View.GONE);
            }
        }
    }
}
