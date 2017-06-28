package com.example.pawel.moviesapp.MovieDetails;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.ItemCreatorOnUniqueList;
import com.example.pawel.moviesapp.Utilities.Variables;

import java.util.List;

import info.movito.themoviedbapi.model.Video;

/**
 * Created by Paweł on 2017-06-15.
 */

public class Fragment_MovieVideos extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.unique_for_every_list_with_image_and_title, parent, false);
    }

    //do zmian jeszcze
    public void updateVideosList(List<Video> videoList) {
        LinearLayout yourLayout = (LinearLayout) getView().findViewById(R.id.unique_linearLayout);
        ItemCreatorOnUniqueList itemCreatorOnUniqueList = new ItemCreatorOnUniqueList(yourLayout, getActivity());

        for (final Video video : videoList) {
            ///zmienic image path na obrazek yt z resources
            View view = itemCreatorOnUniqueList.createItem(video.getKey(), "aaddres bledy");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(Variables.YOUTUBE_ADDRESS + video.getKey()); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

            yourLayout.addView(view);
        }
    }
}
