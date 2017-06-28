package com.example.pawel.moviesapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Paweł on 2017-06-16.
 */

public class Fragment_Loading_Page extends Fragment {

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.loading_page, parent, false);
    }
}
