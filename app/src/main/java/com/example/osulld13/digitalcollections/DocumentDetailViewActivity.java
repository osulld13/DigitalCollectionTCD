package com.example.osulld13.digitalcollections;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DocumentDetailViewActivity extends AppCompatActivity {

    private final String TAG = DocumentDetailViewActivity.class.getSimpleName();

    ArrayList<String> documentDetails; // title, origin_place, publisher, date, language, abstract, access_condition, pid

    ImageView mImageView;

    RelativeLayout mTitleLayout;
    RelativeLayout mOriginLayout;
    RelativeLayout mPublisherLayout;
    RelativeLayout mDateLayout;
    RelativeLayout mLanguageLayout;
    RelativeLayout mAbstractLayout;
    RelativeLayout mAccessConditionLayout;

    TextView mTitleLabelTextView;
    TextView mTitleTextView;
    TextView mOriginLabelTextView;
    TextView mOriginTextView;
    TextView mPublisherLabelTextView;
    TextView mPublisherTextView;
    TextView mDateLabelTextView;
    TextView mDateTextView;
    TextView mLanguageLabelTextView;
    TextView mLanguageTextView;
    TextView mAbstractLabelTextView;
    TextView mAbstractTextView;
    TextView mAccessConditionLabelTextView;
    TextView mAccessConditionTextView;

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
        getThumbnailImage.updateInfoSyncTask(documentDetails.get(7), mImageView, new QueryManager(), 1, null); // get larger thumbnail
        getThumbnailImage.execute();

        // Assign the Title Views
        mTitleLayout= (RelativeLayout) findViewById(R.id.detailViewTitleContainer);
        mTitleLabelTextView = (TextView) findViewById(R.id.detailViewTitleTextViewLabel);
        mTitleTextView = (TextView) findViewById(R.id.detailViewTitleTextView);
        setTextInDetailTextViews(documentDetails.get(0), mTitleLabelTextView, mTitleTextView, mTitleLayout);

        // Assign the Origin Views
        mOriginLayout = (RelativeLayout) findViewById(R.id.detailViewOriginPlaceContainer);
        mOriginLabelTextView = (TextView) findViewById(R.id.detailViewOriginLabelTextView);
        mOriginTextView = (TextView) findViewById(R.id.detailViewOriginTextView);
        setTextInDetailTextViews(documentDetails.get(1), mOriginLabelTextView, mOriginTextView, mOriginLayout);

        // Assign the Publisher Views
        mPublisherLayout = (RelativeLayout) findViewById(R.id.detailViewPublisherContainer);
        mPublisherLabelTextView = (TextView) findViewById(R.id.detailViewPublisherLabelTextView);
        mPublisherTextView = (TextView) findViewById(R.id.detailViewPublisherTextView);
        setTextInDetailTextViews(documentDetails.get(2), mPublisherLabelTextView, mPublisherTextView, mPublisherLayout);

        // Assign the Date Views
        mDateLayout = (RelativeLayout) findViewById(R.id.detailViewDateContainer);
        mDateLabelTextView = (TextView) findViewById(R.id.detailViewDateLabelTextView);
        mDateTextView = (TextView) findViewById(R.id.detailViewDateTextView);
        setTextInDetailTextViews(documentDetails.get(3), mDateLabelTextView, mDateTextView, mDateLayout);

        // Assign the Language Views
        mLanguageLayout = (RelativeLayout) findViewById(R.id.detailViewLanguageContainer);
        mLanguageLabelTextView = (TextView) findViewById(R.id.detailViewLanguageLabelTextView);
        mLanguageTextView = (TextView) findViewById(R.id.detailViewLanguageTextView);
        setTextInDetailTextViews(documentDetails.get(4), mLanguageLabelTextView, mLanguageTextView, mLanguageLayout);

        // Assign the Abstract Views
        mAbstractLayout = (RelativeLayout) findViewById(R.id.detailViewAbstractContainer);
        mAbstractLabelTextView = (TextView) findViewById(R.id.detailViewAbstractLabelTextView);
        mAbstractTextView = (TextView) findViewById(R.id.detailViewAbstractTextView);
        setTextInDetailTextViews(documentDetails.get(5), mAbstractLabelTextView, mAbstractTextView, mAbstractLayout);

        // Assign the Access Condition Views
        mAccessConditionLayout = (RelativeLayout) findViewById(R.id.detailViewAccessConditionContainer);
        mAccessConditionLabelTextView = (TextView) findViewById(R.id.detailViewAccessConditionLabelTextView);
        mAccessConditionTextView = (TextView) findViewById(R.id.detailViewAccessConditionTextView);
        setTextInDetailTextViews(documentDetails.get(6), mAccessConditionLabelTextView, mAccessConditionTextView, mAccessConditionLayout);

    }

    private void setTextInDetailTextViews(String detailVal, TextView label, TextView detailView, RelativeLayout layout) {
        // If a detail val exists assign it to its text fields, otherwise set its corresponding fields to invisible
        if (!detailVal.equals("")) {
            detailView.setText(detailVal);
        }
        else {
            label.setVisibility(View.GONE);
            detailView.setVisibility(View.GONE);
            if (layout != null){
                layout.setVisibility(View.GONE);
            }
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
