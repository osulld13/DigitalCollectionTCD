package com.example.osulld13.digitalcollections;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

    public SearchResultsAdapter(Context context, List<Document> results){
        super(context, 0, results);
        mQueryManager = new QueryManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get data for this position
        Document document = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_result, parent, false);
        }

        // Lookup view for data population
        TextView mTitle = (TextView) convertView.findViewById(R.id.searchResultTitleTextView);
        TextView mSubText = (TextView) convertView.findViewById(R.id.searchResultSubTextView);
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.searchResultImageView);

        // Populate the data into the template view using the data object
        mTitle.setText(capitalize(document.getText()));
        mSubText.setText(capitalize(document.getGenre()));

        GetThumbnailImage getThumbnailImage = new GetThumbnailImage();
        getThumbnailImage.updateInfoSyncTask(document.getPid(), mImageView, mQueryManager, 0); // get small thumbnail
        getThumbnailImage.execute();

        // Return the completed view to render on screen
        return convertView;
    }

    @Override

    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }


}
