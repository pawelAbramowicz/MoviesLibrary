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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.people.PersonPeople;

/**
 * Created by Paweł on 2017-05-29.
 */

public class ActorsGridViewAdapter extends ArrayAdapter<PersonPeople> {

    public ActorsGridViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ActorsGridViewAdapter(Context context, int resource, List<PersonPeople> items) {
        super(context, resource, items);
    }

    static class ViewHolder {
        @BindView(R.id.elementImageTextRow_poster)
        ImageView imageViewPoster;
        @BindView(R.id.elementImageTextRow_text)
        TextView textViewName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.element_image_with_text_row, null);
            holder = new ViewHolder(view);

            //holder.imageViewPoster = (ImageView) view.findViewById(R.id.elementImageTextRow_poster);
           // holder.textViewName = (TextView) view.findViewById(R.id.elementImageTextRow_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        PersonPeople personPeople = getItem(position);

        if (personPeople != null) {
            holder.textViewName.setText(personPeople.getName());
            String imagePath = Variables.IMAGE_ADDRESS + Variables.IMAGE_SIZE_w342 + personPeople.getProfilePath();

            Picasso.with(getContext()).load(imagePath).into(holder.imageViewPoster);
        }
        return view;
    }
}
