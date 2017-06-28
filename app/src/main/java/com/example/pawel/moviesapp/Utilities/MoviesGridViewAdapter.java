package com.example.pawel.moviesapp.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.Models.MovieModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Paweł on 2017-05-31.
 */

public class MoviesGridViewAdapter extends ArrayAdapter<MovieModel> {

    public MoviesGridViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public MoviesGridViewAdapter(Context context, int resource, List<MovieModel> items) {
        super(context, resource, items);
    }


    static class ViewHolder {
        ImageView posterImageView;
        TextView titleTextView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.element_image_with_text_row, null);
            holder = new ViewHolder();

            holder.posterImageView = (ImageView) view.findViewById(R.id.elementImageTextRow_poster);
            holder.titleTextView = (TextView) view.findViewById(R.id.elementImageTextRow_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MovieModel movieModel = getItem(position);

        if (movieModel != null) {
            holder.titleTextView.setText(movieModel.getTitle());
            String imagePath = Variables.IMAGE_ADDRESS + Variables.IMAGE_SIZE_w342 + movieModel.getPosterPath();

            GifDrawable gifFromAssets = null;
            try {
                gifFromAssets = new GifDrawable(getContext().getResources(), Variables.LOADING_WHEELE_ID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Picasso.with(getContext()).load(imagePath).error(R.drawable.not_available_image)
                    .placeholder(gifFromAssets).into(holder.posterImageView);
        }
        return view;
    }
}
