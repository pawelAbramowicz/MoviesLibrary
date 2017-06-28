package com.example.pawel.moviesapp.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pawel.moviesapp.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import pl.droidsonroids.gif.GifDrawable;

public class ListAdapter extends ArrayAdapter<MovieDb> {

   private Context context;

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public ListAdapter(Context context, int resource, List<MovieDb> items) {
        super(context, resource, items);
        this.context = context;
    }

    static class ViewHolder {
        TextView title;
        TextView releaseDate;
        TextView genre;
        ImageView imageViewPoster;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder holder = null;

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.element_search_movie_row, null);
            holder = new ViewHolder();

            holder.title = (TextView) view.findViewById(R.id.elelementSearchRow_title);
            holder.releaseDate = (TextView) view.findViewById(R.id.elelementSearchRow_releaseDate);
            holder.genre = (TextView) view.findViewById(R.id.elelementSearchRow_genre);
            holder.imageViewPoster = (ImageView) view.findViewById(R.id.elelementSearchRow_poster);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MovieDb movieDb = getItem(position);

        if (movieDb != null) {
            holder.title.setText(movieDb.getTitle());
            holder.releaseDate.setText(movieDb.getReleaseDate());
            holder.genre.setText(ListConverterToString.genreListToString(movieDb.getGenres()));

            String imagePath = Variables.IMAGE_ADDRESS + Variables.IMAGE_SIZE_w342 + movieDb.getPosterPath();

            GifDrawable gifFromAssets = null;
            try {
                gifFromAssets = new GifDrawable(context.getResources(), Variables.LOADING_WHEELE_ID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Picasso.with(context).load(imagePath).error(R.drawable.not_available_image).
                    placeholder(gifFromAssets).into(holder.imageViewPoster);
        }
        return view;
    }
}