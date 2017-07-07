package com.example.pawel.moviesapp.MainScreen;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.example.pawel.moviesapp.ActorDetailsScreen.Activity_ActorDetails;
import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.InternetConnection;
import com.example.pawel.moviesapp.Utilities.Models.UniqueModel;
import com.example.pawel.moviesapp.Utilities.UniqueAdapter;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    private UniqueAdapter customAdapter;
    private List<UniqueModel> uniqueModelList = new ArrayList<>();
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    @BindView(R.id.recyclerViewer)
    RecyclerView recyclerView;
    @BindView(R.id.unique_title_textView)
    TextView textViewTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unique_for_every_list_with_image_and_title, parent, false);//
        ButterKnife.bind(this, view);//
        return view; ///
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
                UniqueModel uniqueModel = customAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), Activity_ActorDetails.class);
                intent.putExtra(Variables.INTENT_ACTOR_ID, uniqueModel.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position, View v) {

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
                textViewTitle.setText(R.string.mostPopularActors);
                textViewTitle.setVisibility(View.VISIBLE);

                //add most pupular actors to the linear layout
                for (final Person person : personList) {
                    UniqueModel uniqueMoel = new UniqueModel(person.getId(), person.getName(), person.getProfilePath());
                    uniqueModelList.add(uniqueMoel);
                }

                customAdapter.notifyDataSetChanged();

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
