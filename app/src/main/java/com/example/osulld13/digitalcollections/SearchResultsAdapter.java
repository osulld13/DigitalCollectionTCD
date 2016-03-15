package com.example.osulld13.digitalcollections;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Donal on 12/03/2016.
 */

/*
* The adapter for organising the results of a search query into the search results list
*
* Used as guide: https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
* */
public class SearchResultsAdapter extends ArrayAdapter<Document> {

    private QueryManager mQueryManager;
    private int mLayout;
    private int mImageSize;
    private int mBackground;

    public SearchResultsAdapter(Context context, List<Document> results, int layout, int imageSize, int background){
        super(context, 0, results);
        mQueryManager = new QueryManager();
        mLayout = layout;
        mImageSize = imageSize;
        mBackground = background;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get data for this position
        Document document = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(mLayout, parent, false);
        }
        //assign imageView early to prevent scrolling effect
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.imageView);
        mImageView.setImageResource(R.drawable.background_place_holder_image_dark);
        if (mBackground == AppConstants.backGroundLight) {
            mImageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background_place_holder_image_light));
        }
        else if (mBackground == AppConstants.backGroundLight){
            mImageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background_place_holder_image_dark));
        }
        //Unique tag is added to each image to be checked again when adding the image in the thumbnail request
        mImageView.setTag(String.valueOf(position));
        // Lookup view for data population
        TextView mTitle = (TextView) convertView.findViewById(R.id.titleTextView);
        TextView mSubText = (TextView) convertView.findViewById(R.id.subTextView);
        // Populate the data into the template view using the data object
        mTitle.setText(capitalize(document.getText()));
        mSubText.setText(capitalize(document.getGenre()));
        // Get thumbnail image from url
        GetThumbnailImage getThumbnailImage = new GetThumbnailImage();
        getThumbnailImage.updateInfoSyncTask(document.getPid(), mImageView, mQueryManager, mImageSize, (String) mImageView.getTag()); // get small thumbnail
        getThumbnailImage.execute();
        // Return the completed view to render on screen
        return convertView;
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }



}
