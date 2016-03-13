package com.example.osulld13.digitalcollections;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DocumentDetailViewActivity extends AppCompatActivity {

    private final String TAG = DocumentDetailViewActivity.class.getSimpleName();

    ArrayList<String> documentDetails; // title, origin_place, publisher, date, language, abstract, access_condition, pid

    ImageView mImageView;

    TextView mTitleLabelTextView;
    TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail_view);
        setUpToolbar();

        // get document details data passed from documentView
        documentDetails = getIntent().getStringArrayListExtra(AppConstants.documentDetailTransferString);

        // Initialize doc detail elements
        initializeDetailElements();

    }

    private void initializeDetailElements(){

        // Assign the Image view
        mImageView = (ImageView) findViewById(R.id.detailViewImageView);
        GetThumbnailImage getThumbnailImage = new GetThumbnailImage();
        getThumbnailImage.updateInfoSyncTask(documentDetails.get(7), mImageView, new QueryManager(), 1); // get larger thumbnail
        getThumbnailImage.execute();


        // Assign the Title Views
        mTitleLabelTextView = (TextView) findViewById(R.id.detailViewTitleTextViewLabel);
        mTitleTextView = (TextView) findViewById(R.id.detailViewTitleTextView);
        setTextInDetailTextViews(documentDetails.get(0), mTitleLabelTextView, mTitleTextView);


    }

    private void setTextInDetailTextViews(String detailVal, TextView label, TextView detailView) {
        // If a detail val exists assign it to its text fields, otherwise set its corresponding fields to invisible
        if (!detailVal.equals("")) {
            mTitleTextView.setText(detailVal);
        }
        else {
            mTitleTextView.setVisibility(View.INVISIBLE);
            mTitleLabelTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
