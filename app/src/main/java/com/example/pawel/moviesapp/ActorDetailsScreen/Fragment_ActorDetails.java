package com.example.pawel.moviesapp.ActorDetailsScreen;

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
import com.example.pawel.moviesapp.Utilities.Variables;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.people.PersonPeople;

/**
 * Created by Paweł on 2017-05-29.
 */

public class Fragment_ActorDetails extends Fragment {

    private AsyncCaller asyncCaller;
    private updateFragments mCallback;

    @BindView(R.id.actorDetails_name)
      TextView name;
    @BindView(R.id.actorDetails_biography)
      TextView biography;
    @BindView(R.id.actorDetails_birthday)
      TextView birthday;
    @BindView(R.id.actorDetails_birthplace)
      TextView birthplace;
    @BindView(R.id.actorDetails_imagePoster)
      ImageView imagePoster;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actor_details, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //hide fragment before everything will be loaded
        //  getView().setVisibility(View.GONE);

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

    private class AsyncCaller extends AsyncTask<Integer, Void, PersonPeople> {

        @Override
        protected PersonPeople doInBackground(Integer... integers) {
            if (!asyncCaller.isCancelled()) {
                int id = integers[0];
                //returns all information about actor
                TmdbPeople people = new TmdbApi(Variables.API_KEY).getPeople();
                PersonPeople person = people.getPersonInfo(id, TmdbPeople.TMDB_METHOD_PERSON);
                return person;
            }
            return null;
        }


        @Override
        protected void onPostExecute(final PersonPeople result) {
            if (!asyncCaller.isCancelled() && result != null) {
                //set title for action bar
                getActivity().setTitle(result.getName());

                //set data for TextViews
                name.setText(result.getName());
                biography.setText(result.getBiography());
                birthday.setText(result.getBirthday());
                birthplace.setText(result.getBirthplace());
                if (result.getDeathday() != null && !result.getDeathday().equals("")) {
                    birthday.setText(result.getBirthday() + " - " + result.getDeathday());
                }

                //set actor image and add listener to increase it after click
                ItemCreatorOnUniqueList itemCreatorOnUniqueList = new ItemCreatorOnUniqueList(getActivity());
                itemCreatorOnUniqueList.setImage(imagePoster, result.getProfilePath());
                itemCreatorOnUniqueList.addIncreasingImageListener(result.getProfilePath());

                if (mCallback != null) {
                    //hide loading fragment and show others
                    mCallback.displayFragments();
                }
            }
        }
    }

    public interface updateFragments {
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
        mCallback = null; //   avoid leaking
        super.onDetach();
    }
}
