package com.example.pawel.moviesapp.MainScreen;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pawel.moviesapp.ActorDetails.Activity_ActorDetails;
import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.ItemCreatorOnUniqueList;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.people.Person;

/**
 * Created by Paweł on 2017-05-29.
 */

public class Fragment_MostPopularActors extends Fragment {

    private ProgressDialog progress;
    private AsyncCaller asyncCaller;
    private updateFragments mCallback;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.unique_for_every_list_with_image_and_title, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setRetainInstance(true);

        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) getView().findViewById(R.id.unique_horizontalScrollView);
        horizontalScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                float maxScrollX = horizontalScrollView.getChildAt(0).getMeasuredWidth() - horizontalScrollView.getMeasuredWidth();
                if (horizontalScrollView.getScrollX() == maxScrollX) {
                    asyncCaller = new AsyncCaller();
                    asyncCaller.execute();
                }
            }
        });

        asyncCaller = new AsyncCaller();
        asyncCaller.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (asyncCaller != null)
            asyncCaller.cancel(true);
    }

    public interface updateFragments {
        public void displayFragments();
    }

    private class AsyncCaller extends AsyncTask<Void, Void, List<Person>> {

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(getActivity());
            progress.setMessage(getString(R.string.pleaseWait));
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
        }

        @Override
        protected List<Person> doInBackground(Void... voids) {
            if (!asyncCaller.isCancelled()) {
                //returns most popular actors
                TmdbPeople actors = new TmdbApi(Variables.API_KEY).getPeople();
                TmdbPeople.PersonResultsPage movie1 = actors.getPersonPopular(page);
                List<Person> personList = movie1.getResults();
                page += 1;
                return personList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Person> personList) {
            if (!asyncCaller.isCancelled() && personList != null) {
                //set title for this section
                TextView textViewTitle = (TextView) getView().findViewById(R.id.unique_title_textView);
                textViewTitle.setText(R.string.mostPopularActors);
                textViewTitle.setVisibility(View.VISIBLE);

                LinearLayout yourLayout = (LinearLayout) getView().findViewById(R.id.unique_linearLayout);
                ItemCreatorOnUniqueList itemCreatorOnUniqueList = new ItemCreatorOnUniqueList(yourLayout, getActivity());

                //add most pupular actors to the linear layout
                for (final Person person : personList) {
                    View view = itemCreatorOnUniqueList.createItem(person.getName(), person.getProfilePath());

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), Activity_ActorDetails.class);
                            intent.putExtra(Variables.INTENT_ACTOR_ID, person.getId());
                            startActivity(intent);
                        }
                    });

                    yourLayout.addView(view);
                }
                if (mCallback != null) {
                    mCallback.displayFragments();
                }
                progress.dismiss();
            }
        }
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
