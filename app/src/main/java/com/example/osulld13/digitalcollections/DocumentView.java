package com.example.osulld13.digitalcollections;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import uk.co.senab.photoview.PhotoViewAttacher;

import java.util.List;

public class DocumentView extends AppCompatActivity {

    private final String TAG = QueryManager.class.getSimpleName();
    private String[] docInfo; // Pid, DrisFolderNumber, Text, Genre, Lang, TypeOfResource
    private PhotoViewAttacher mAttacher;
    private ImageView mImageView;
    private QueryManager mQueryManager = new QueryManager();
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);

        //Retrieves doc info passed from previous activity
        docInfo = getIntent().getStringArrayExtra(AppConstants.documentTransferString);

        // Set up toolbar
        setUpToolbar();

        // Add progress bar to XML views and then call to make visible
        mProgressBar = (ProgressBar) findViewById(R.id.documentViewProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mImageView = (ImageView) findViewById(R.id.documentViewImageView);
        mAttacher = new PhotoViewAttacher(mImageView);

        GetDocumentImage getImage = new GetDocumentImage();
        getImage.execute(docInfo[0], docInfo[1]);
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

    private void setUpToolbar() {
        // Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(docInfo[2]);
    }

    // Creates an asynchronous task that gets the image for the document view
    private class GetDocumentImage extends AsyncTask<String, Integer, Bitmap> {

        protected Bitmap doInBackground(String... docInfo){
            if(android.os.Debug.isDebuggerConnected()){
                android.os.Debug.waitForDebugger();
            }
            return mQueryManager.getImageResource(docInfo[1], docInfo[0]);
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPreExecute() {
            //Turn Progress indicator off
            mProgressBar.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute (Bitmap result) {
            //Turn Progress indicator off
            if(android.os.Debug.isDebuggerConnected()){
                android.os.Debug.waitForDebugger();
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            mImageView.setImageBitmap(result);
            mAttacher.update();
        }
    }

}
