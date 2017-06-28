package com.example.pawel.moviesapp.MainScreen;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import com.example.pawel.moviesapp.Fragment_Loading_Page;
import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.SearchScreen.Activity_Search;
import com.example.pawel.moviesapp.Utilities.Variables;
import com.example.pawel.moviesapp.WatchListScreen.Activity_WatchList;

import java.util.Locale;

public class Activity_Main extends AppCompatActivity implements Fragment_MostPopularActors.updateFragments {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set language from locale - descriptions of movies in that language are displayed
        Variables.LANGUAGE = Locale.getDefault().getLanguage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_screen_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.app_bar_search:
                intent = new Intent(getApplicationContext(), Activity_Search.class);
                startActivity(intent);
                return true;
            case R.id.app_bar_toWatch:
                intent = new Intent(getApplicationContext(), Activity_WatchList.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void displayFragments() {
        Fragment_Loading_Page loadingPageFragment = (Fragment_Loading_Page) getFragmentManager().findFragmentById(R.id.activityMain_loadingFragment);

        //hide loading fragment
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(loadingPageFragment).commit();

        //show other fragments
        ScrollView scrollView = (ScrollView) findViewById(R.id.activityMain_scrollView);
        scrollView.setVisibility(View.VISIBLE);
    }
}
