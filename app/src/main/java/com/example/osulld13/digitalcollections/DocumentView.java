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

import java.util.List;

public class DocumentView extends AppCompatActivity {

    private final String TAG = QueryManager.class.getSimpleName();
    private String[] docInfo; // Pid, DrisFolderNumber, Text, Genre, Lang, TypeOfResource
    private ImageView mImageView;
    private QueryManager mQueryManager = new QueryManager();
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);
        // Set up toolbar
        setUpToolbar();

        //Retrieves doc info passed from previous activity
        docInfo = getIntent().getStringArrayExtra(AppConstants.documentTransferString);

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
    }

    // Creates an asynchronous task that gets the image for the document view
    private class GetDocumentImage extends AsyncTask<String, Integer, Bitmap> {

        protected Bitmap doInBackground(String... docInfo){
            Log.d(TAG, docInfo[1]);
            Log.d(TAG, docInfo[0]);
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
            mImageView.setImageBitmap(result);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

}
