package com.example.pawel.moviesapp.Utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.Models.UniqueModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Paweł on 2017-07-05.
 */

public class UniqueAdapter extends RecyclerView.Adapter<UniqueAdapter.ViewHolder> {
    private ClickListener clickListener;
    private List<UniqueModel> uniqueModelList = new ArrayList<>();
    private Context context;

    // Pass in the contact array into the constructor
    public UniqueAdapter(Context context, List<UniqueModel> uniqueModelList) {
        this.uniqueModelList = uniqueModelList;
        this.context = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.element_image_with_text_row, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;

    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(UniqueAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        UniqueModel uniqueModel = uniqueModelList.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.text;
        ImageView imageViewPoster = viewHolder.poster;

        GifDrawable gifFromAssets = null;
        try {
            gifFromAssets = new GifDrawable(getContext().getResources(), Variables.LOADING_WHEELE_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set signature under poster
        textView.setText(uniqueModel.getTitle());
        //set poster
        String imagePath = Variables.IMAGE_ADDRESS + Variables.IMAGE_SIZE_w342 + uniqueModel.getPosterPath();
        Picasso.with(getContext()).load(imagePath).error(R.drawable.not_available_image)
                .placeholder(gifFromAssets).into(imageViewPoster);

    }

    @Override
    public int getItemCount() {
        return uniqueModelList.size();
    }

    public UniqueModel getItem(int position) {
        return uniqueModelList.get(position);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        @BindView(R.id.elementImageTextRow_poster)
        ImageView poster;
        @BindView(R.id.elementImageTextRow_text)
        TextView text;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemLongClick(getAdapterPosition(), view);
            return true;
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }
}