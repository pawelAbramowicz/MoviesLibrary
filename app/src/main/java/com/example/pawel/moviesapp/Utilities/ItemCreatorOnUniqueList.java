package com.example.pawel.moviesapp.Utilities;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pawel.moviesapp.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Paweł on 2017-06-19.
 */

public class ItemCreatorOnUniqueList {
    private Activity activity;
    private LinearLayout yourLayout;
    private LayoutInflater inflater;
    private ImageView imageViewPoster;

    public ItemCreatorOnUniqueList(LinearLayout yourLayout, Activity activity) {
        this.activity = activity;
        this.yourLayout = yourLayout;
        inflater = LayoutInflater.from(activity.getApplicationContext());
    }

    public ItemCreatorOnUniqueList(Activity activity) {
        this.activity = activity;
    }


    public void setImage(ImageView imageView, String imagePath) {
        imageViewPoster = imageView;
        GifDrawable gifFromAssets = null;
        try {
            gifFromAssets = new GifDrawable(activity.getResources(), Variables.LOADING_WHEELE_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = Variables.IMAGE_ADDRESS + Variables.IMAGE_SIZE_w342 + imagePath;

        Picasso.with(activity.getApplicationContext()).load(path).error(R.drawable.not_available_image).
                placeholder(gifFromAssets).into(imageView);
    }

    /**
     * @param text      a title below image
     * @param imagePath path to the image
     * @return
     */
    public View createItem(String text, String imagePath) {
        View view = inflater.inflate(R.layout.element_image_with_text_row, yourLayout, false);
        imageViewPoster = (ImageView) view.findViewById(R.id.elementImageTextRow_poster);
        TextView textViewName = (TextView) view.findViewById(R.id.elementImageTextRow_text);

        textViewName.setText(text);

        GifDrawable gifFromAssets = null;
        try {
            gifFromAssets = new GifDrawable(activity.getResources(), Variables.LOADING_WHEELE_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String path = Variables.IMAGE_ADDRESS + Variables.IMAGE_SIZE_w342 + imagePath;
        Picasso.with(activity.getApplicationContext()).load(path).error(R.drawable.not_available_image).
                placeholder(gifFromAssets).into(imageViewPoster);

        return view;
    }


    public void addIncreasingImageListener(final String path) {
        imageViewPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialogWindow = new Dialog(activity);
                //set no title on dialog window
                dialogWindow.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                //set layout for dialog window
                dialogWindow.setContentView(activity.getLayoutInflater().inflate(R.layout.dialog_image_increase, null));

                ImageView imageViewIncreased = (ImageView) dialogWindow.findViewById(R.id.dialogImageIncrease_increasedImage);
                String imagePath = Variables.IMAGE_ADDRESS + Variables.IMAGE_SIZE_w780 + path;

                GifDrawable gifFromAssets = null;
                try {
                    gifFromAssets = new GifDrawable(activity.getResources(), Variables.LOADING_WHEELE_ID);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Picasso.with(activity.getApplicationContext()).load(imagePath).error(R.drawable.not_available_image)
                        .placeholder(gifFromAssets).into(imageViewIncreased);

                //dismiss dialog on image click
                imageViewIncreased.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogWindow.dismiss();
                    }
                });

                //dismiss dialog on button click
                Button okButton = (Button) dialogWindow.findViewById(R.id.dialogImageIncreaseZ_buttonCloseImage);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogWindow.dismiss();
                    }
                });

                //display dialog
                dialogWindow.show();
            }
        });
    }
}
