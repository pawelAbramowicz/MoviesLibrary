package com.example.pawel.moviesapp.ActorDetailsScreen;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.pawel.moviesapp.Fragment_Loading_Page;
import com.example.pawel.moviesapp.R;

/**
 * Created by Paweł on 2017-05-29.
 */

public class Activity_ActorDetails extends AppCompatActivity implements Fragment_ActorDetails.updateFragments {

    // @BindView (R.id.activityActorDetails_actorFragment)
    Fragment_ActorDetails actorDetails_Fragment;
   //   @BindView(R.id.activityActorDetails_actorPlayedInMoviesFragment)
    Fragment_ActorPlayedInMovie actorPlayedInMovies_Fragment;
    //   @BindView(R.id.activityActorDetails_actorImagesFragment)
    Fragment_ActorPhotos actorPhotos_Fragment;
   //   @BindView(R.id.activityActorDetails_loadingFragment)
    Fragment_Loading_Page loadingPage_Fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_details);
        //  ButterKnife.bind(this);

         actorDetails_Fragment = (Fragment_ActorDetails) getFragmentManager().findFragmentById(R.id.activityActorDetails_actorFragment);
        actorPlayedInMovies_Fragment = (Fragment_ActorPlayedInMovie) getFragmentManager().findFragmentById(R.id.activityActorDetails_actorPlayedInMoviesFragment);
        actorPhotos_Fragment = (Fragment_ActorPhotos) getFragmentManager().findFragmentById(R.id.activityActorDetails_actorImagesFragment);
        loadingPage_Fragment = (Fragment_Loading_Page) getFragmentManager().findFragmentById(R.id.activityActorDetails_loadingFragment);

     /*   FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(actorDetails_Fragment).hide(actorPlayedInMovies_Fragment).hide(actorPhotos_Fragment)
                .show(loadingPage_Fragment)
                .commit();*/
    }

    @Override
    public void displayFragments() {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(loadingPage_Fragment)
                .show(actorDetails_Fragment).show(actorPlayedInMovies_Fragment).show(actorPhotos_Fragment)
                .commit();
    }
}
