package com.example.pawel.moviesapp.MovieDetailsScreen;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.InternetConnection;
import com.example.pawel.moviesapp.Utilities.ItemCreatorOnUniqueList;
import com.example.pawel.moviesapp.Utilities.ListConverterToString;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.people.PersonCast;

public class Fragment_MovieDetails extends Fragment {

    private AsyncCaller asyncCaller;
    private updateFragments mCallback;

    @BindView(R.id.titleResult)
    TextView titleResult;
    @BindView(R.id.originalTitleResult)
    TextView originalTitleResult;
    @BindView(R.id.productionAndTimeResult)
    TextView productionAndTimeResult;
    @BindView(R.id.genreResult)
    TextView genreResult;
    @BindView(R.id.descriptionResult)
    TextView descriptionResult;
    @BindView(R.id.countryResult)
    TextView countryResult;
    @BindView(R.id.boxOfficeResult)
    TextView boxOfficeResult;
    @BindView(R.id.ratingResult)
    TextView ratingResult;
    @BindView(R.id.languageResult)
    TextView languageResult;
    @BindView(R.id.imagePosterResult)
    ImageView imagePosterResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //hide fragment before everything will be loaded
        getView().setVisibility(View.GONE);

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

    private class AsyncCaller extends AsyncTask<Integer, Void, MovieDb> {

        @Override
        protected MovieDb doInBackground(Integer... integers) {
            if (!asyncCaller.isCancelled()) {
                int movieID = integers[0];
                //returns information about movie
                TmdbMovies tmdbMovies = new TmdbApi(Variables.API_KEY).getMovies();
                MovieDb movie = tmdbMovies.getMovie(movieID, Variables.LANGUAGE, TmdbMovies.MovieMethod.values());
                return movie;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final MovieDb result) {
            if (!asyncCaller.isCancelled() && result != null) {
                //set title for Action Bar
                getActivity().setTitle(result.getTitle());

                //set data for TextViews
                titleResult.setText(result.getTitle());//title
                originalTitleResult.setText("(" + result.getOriginalTitle() + ")"); //original title
                productionAndTimeResult.setText(result.getReleaseDate() + "  " + result.getRuntime() + " min"); //runtime
                genreResult.setText(ListConverterToString.genreListToString(result.getGenres()));//genre
                descriptionResult.setText(result.getOverview()); //description
                countryResult.setText(getString(R.string.production) + " " + ListConverterToString.countryListToString(result.getProductionCountries())); //production countries
                boxOfficeResult.setText(getString(R.string.boxOffice) + " " + ListConverterToString.budgetConvert(result.getBudget()));  //boxoffice
                ratingResult.setText(getString(R.string.rating) + " " + String.valueOf(result.getVoteAverage()) + "/10");  //vote average
                Locale locale = new Locale(result.getOriginalLanguage(), "");
                languageResult.setText(getString(R.string.language) + " " + locale.getDisplayLanguage());//languages

                //set movie image and add listener to increase it after click
                ItemCreatorOnUniqueList itemCreatorOnUniqueList = new ItemCreatorOnUniqueList(getActivity());
                itemCreatorOnUniqueList.setImage(imagePosterResult, result.getPosterPath());
                itemCreatorOnUniqueList.addIncreasingImageListener(result.getPosterPath());

                if (mCallback != null) {
                    mCallback.sendActors(result.getCast());      //forward list to the fragment
                    mCallback.sendVideos(result.getVideos());    //forward list to the fragment
                    mCallback.displayFragments();                //hide loading fragment and show others
                    mCallback.checkContaining();                 //depends on the result show/hide button add/remove movie from watch list
                }
            }
        }
    }

    public interface updateFragments {
        public void sendActors(List<PersonCast> cast);

        public void sendVideos(List<Video> videoMovieList);

        public void checkContaining();

        public void displayFragments();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (updateFragments) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (updateFragments) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        mCallback = null; // avoid leaking
        super.onDetach();
    }
}
