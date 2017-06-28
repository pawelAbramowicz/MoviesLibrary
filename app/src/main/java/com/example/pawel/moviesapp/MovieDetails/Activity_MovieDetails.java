package com.example.pawel.moviesapp.MovieDetails;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pawel.moviesapp.Fragment_Loading_Page;
import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.DataBase;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.List;

import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.people.PersonCast;


public class Activity_MovieDetails extends AppCompatActivity implements Fragment_MovieDetails.updateFragments {

    private Button addToWatchListButton, removeFromWatchListButton;
    private Fragment_MovieVideos movieVideos_Fragment;
    private Fragment_ActorsInMovieDetails actorsInMovieDetails_Fragment;
    private Fragment_MovieDetails movieDetails_Fragment;
    private Fragment_SimilarMovies similarMovies_Fragment;
    private Fragment_Loading_Page loadingPage_Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //find all used fragments
        movieVideos_Fragment = (Fragment_MovieVideos) getFragmentManager().findFragmentById(R.id.activityMovieDetails_videosFramgnet);
        actorsInMovieDetails_Fragment = (Fragment_ActorsInMovieDetails) getFragmentManager().findFragmentById(R.id.activityMovieDetails_actorsFragment);
        movieDetails_Fragment = (Fragment_MovieDetails) getFragmentManager().findFragmentById(R.id.activityMovieDetails_detailsFragment);
        similarMovies_Fragment = (Fragment_SimilarMovies) getFragmentManager().findFragmentById(R.id.activityMovieDetails_similarFragment);
        loadingPage_Fragment = (Fragment_Loading_Page) getFragmentManager().findFragmentById(R.id.activityMovieDetails_loadingFragment);

        addToWatchListButton = (Button) findViewById(R.id.activityMovieDetails_addToWatchListButton);
        removeFromWatchListButton = (Button) findViewById(R.id.activityMovieDetails_removeFromWatchListButton);

        //adds actions after button click - add/remove movie from database
        addListenersToButtons();

        //replace fragments
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(movieVideos_Fragment).hide(actorsInMovieDetails_Fragment).hide(movieDetails_Fragment).hide(movieDetails_Fragment).hide(similarMovies_Fragment)
                .show(loadingPage_Fragment)
                .commit();
    }

    private void addListenersToButtons() {
        //add movie to the watch list
        addToWatchListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //get information from intent
                int movieID = getIntent().getIntExtra(Variables.INTENT_MOVIE_ID, 0);
                String title = getIntent().getStringExtra(Variables.INTENT_MOVIE_TITLE);
                String path = getIntent().getStringExtra(Variables.INTENT_MOVIE_POSTAR_PATH);

                //insert movie into data base
                DataBase dataBase = new DataBase(getApplicationContext()).open();
                long number = dataBase.insertMovie(movieID, title, path);
                String result = dataBase.insertMovieResultInterpreter(number);
                dataBase.close();

                //display result on toast
                Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT);
                toast.show();

                //check movie containing in data base to show or hide add/remove button
                checkContaining();
            }
        });

        //remove movie from watch list
        removeFromWatchListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //get information from intent
                int movieID = getIntent().getIntExtra(Variables.INTENT_MOVIE_ID, 0);

                //remove movie from data base
                DataBase dataBase = new DataBase(getApplicationContext()).open();
                int number = dataBase.deleteMovie(movieID);
                String result = dataBase.deleteMovieResultInterpreter(number);
                dataBase.close();

                //display result on toast
                Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT);
                toast.show();

                //check movie containing in data base to show or hide add/remove button
                checkContaining();
            }
        });
    }

    @Override
    public void sendActors(List<PersonCast> castList) {
        //forward list to the fragment
        Fragment_ActorsInMovieDetails frag = (Fragment_ActorsInMovieDetails) getFragmentManager().findFragmentById(R.id.activityMovieDetails_actorsFragment);
        frag.updateActors(castList);
    }

    @Override
    public void sendVideos(List<Video> videoList) {
        //forward list to the fragment
        Fragment_MovieVideos frag = (Fragment_MovieVideos) getFragmentManager().findFragmentById(R.id.activityMovieDetails_videosFramgnet);
        frag.updateVideosList(videoList);
    }

    @Override
    public void displayFragments() {
        //hide loading fragment and show other fragments
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(loadingPage_Fragment)
                .show(movieVideos_Fragment).show(movieDetails_Fragment).show(actorsInMovieDetails_Fragment).show(movieDetails_Fragment).show(similarMovies_Fragment)
                .commit();

        // RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activityDetailsFilm_relativeLayout);
        // relativeLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void checkContaining() {
        //check if movie is already saved in data base and show or hide add/remove button
        int movieID = getIntent().getIntExtra(Variables.INTENT_MOVIE_ID, 0);
        DataBase dataBase = new DataBase(getApplicationContext()).open();
        if (dataBase.isContaining(movieID)) {
            removeFromWatchListButton.setVisibility(View.VISIBLE);
            addToWatchListButton.setVisibility(View.GONE);
        } else {
            removeFromWatchListButton.setVisibility(View.GONE);
            addToWatchListButton.setVisibility(View.VISIBLE);
        }
        dataBase.close();
    }
}