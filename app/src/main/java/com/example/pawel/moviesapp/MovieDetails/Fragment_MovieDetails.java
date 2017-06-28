package com.example.pawel.moviesapp.MovieDetails;

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

import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.ItemCreatorOnUniqueList;
import com.example.pawel.moviesapp.Utilities.ListConverterToString;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.List;
import java.util.Locale;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.people.PersonCast;

public class Fragment_MovieDetails extends Fragment {

    private AsyncCaller asyncCaller;
    private updateFragments mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_details, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //hide fragment before everything will be loaded
        getView().setVisibility(View.GONE);

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
                ((TextView) getView().findViewById(R.id.wyniktytul)).setText                        //title
                        (result.getTitle());
                ((TextView) getView().findViewById(R.id.wynikoryginalnytytul)).setText              //original title
                        ("(" + result.getOriginalTitle() + ")");
                ((TextView) getView().findViewById(R.id.wynikrokprodukcjiiczas)).setText            //runtime
                        (result.getReleaseDate() + "  " + result.getRuntime() + " min");
                ((TextView) getView().findViewById(R.id.wynikgatunek)).setText                      //genre
                        (ListConverterToString.genreListToString(result.getGenres()));
                ((TextView) getView().findViewById(R.id.wynikopis)).setText                         //description
                        (result.getOverview());
                ((TextView) getView().findViewById(R.id.wynikKraj)).setText                         //production countries
                        (getString(R.string.production) + " " + ListConverterToString.countryListToString(result.getProductionCountries()));
                ((TextView) getView().findViewById(R.id.wynikBoxOffice)).setText                    //boxoffice
                        (getString(R.string.boxOffice) + " " + ListConverterToString.budgetConvert(result.getBudget()));
                ((TextView) getView().findViewById(R.id.wynikImdbID)).setText                       //vote average
                        (getString(R.string.rating) + " " + String.valueOf(result.getVoteAverage()) + "/10");

                Locale locale = new Locale(result.getOriginalLanguage(), "");
                ((TextView) getView().findViewById(R.id.wynikJezyk)).setText                        //languages
                        (getString(R.string.language) + " " + locale.getDisplayLanguage());

                //set movie image and add listener to increase it after click
                ImageView posterImageView = (ImageView) getView().findViewById(R.id.imagePoster);
                ItemCreatorOnUniqueList itemCreatorOnUniqueList = new ItemCreatorOnUniqueList(getActivity());
                itemCreatorOnUniqueList.setImage(posterImageView, result.getPosterPath());
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
