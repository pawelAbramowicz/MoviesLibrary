package com.example.pawel.moviesapp.WatchListScreen;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.pawel.moviesapp.MovieDetailsScreen.Activity_MovieDetails;
import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.DataBase;
import com.example.pawel.moviesapp.Utilities.Models.MovieModel;
import com.example.pawel.moviesapp.Utilities.MoviesGridViewAdapter;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by Paweł on 2017-05-30.
 */

public class Fragment_WatchList extends Fragment {

    private MoviesGridViewAdapter customAdapter;
    private List<MovieModel> movieModels = new ArrayList<MovieModel>();
    @BindView(R.id.watchList_gridView)
    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_watch_list, parent, false);//
        ButterKnife.bind(this, view);//
        return view; ///
///


/// return inflater.inflate(R.layout.fragment_watch_list, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        // gridView = (GridView) getView().findViewById(R.id.watchList_gridView);
        movieModels = getWatchListFromDB();

        customAdapter = new MoviesGridViewAdapter(getActivity().getApplicationContext(), R.layout.element_image_with_text_row, movieModels);
        gridView.setAdapter(customAdapter);
        registerForContextMenu(gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MovieModel p = (MovieModel) gridView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), Activity_MovieDetails.class);
                intent.putExtra(Variables.INTENT_MOVIE_ID, p.getId());
                intent.putExtra(Variables.INTENT_MOVIE_TITLE, p.getTitle());
                intent.putExtra(Variables.INTENT_MOVIE_POSTAR_PATH, p.getPosterPath());
                startActivity(intent);
            }
        });
    }

    private List<MovieModel> getWatchListFromDB() {
        DataBase dataBase = new DataBase(getActivity().getApplicationContext()).open();
        List<MovieModel> movieModelList = dataBase.getAllMovies();
        dataBase.close();
        return movieModelList;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info;
        try {
            // Casts the incoming data object into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        int position = info.position;
        MovieModel movieModel = (MovieModel) gridView.getItemAtPosition(position);

        if (v.getId() == R.id.watchList_gridView) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_watch_list, menu);
            menu.setHeaderTitle(movieModel.getTitle());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        switch (item.getItemId()) {
            case R.id.deleteFavouriteMovie_menu:
                MovieModel p = (MovieModel) gridView.getItemAtPosition(position);
                //delete movie from watch list
                DataBase dataBase = new DataBase(getActivity().getApplicationContext()).open();
                int deleteResult = dataBase.deleteMovie(p.getId());
                String result = dataBase.deleteMovieResultInterpreter(deleteResult);
                dataBase.close();
                //display toast
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT);
                toast.show();
                //remove movie from list and notify adapter
                movieModels.remove(position);
                customAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
