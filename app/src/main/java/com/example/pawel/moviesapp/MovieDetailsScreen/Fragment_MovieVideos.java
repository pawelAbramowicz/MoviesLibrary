package com.example.pawel.moviesapp.MovieDetailsScreen;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.Models.UniqueModel;
import com.example.pawel.moviesapp.Utilities.UniqueAdapter;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.Video;

/**
 * Created by Paweł on 2017-06-15.
 */

public class Fragment_MovieVideos extends Fragment {

    private UniqueAdapter customAdapter;
    private List<UniqueModel> uniqueModelList = new ArrayList<>();

    @BindView(R.id.recyclerViewer)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unique_for_every_list_with_image_and_title, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setRetainInstance(true);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        customAdapter = new UniqueAdapter(getActivity().getApplicationContext(), uniqueModelList);
        recyclerView.setAdapter(customAdapter);

        customAdapter.setOnItemClickListener(new UniqueAdapter.ClickListener() {

            @Override
            public void onItemClick(int position, View v) {
                UniqueModel uniqueModel = customAdapter.getItem(position);

                Uri uri = Uri.parse(Variables.YOUTUBE_ADDRESS + uniqueModel.getPosterPath()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });
    }

    public void updateVideosList(List<Video> videoList) {

        for (final Video video : videoList) {
            UniqueModel uniqueModel = new UniqueModel(0, "Video", video.getKey());
            uniqueModelList.add(uniqueModel);
        }

        customAdapter.notifyDataSetChanged();
    }
}
