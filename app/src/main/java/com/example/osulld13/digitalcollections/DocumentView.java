package com.example.osulld13.digitalcollections;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentView extends AppCompatActivity {

    private final String TAG = QueryManager.class.getSimpleName();
    private String[] docInfo; // Pid, DrisFolderNumber, Text, Genre, Lang, TypeOfResource
    ArrayList<String> docPageIds;
    private PhotoViewAttacher mAttacher;
    private ImageView mImageView;
    private QueryManager mQueryManager = new QueryManager();
    private ProgressBar mProgressBar;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);

        //Retrieves doc info passed from previous activity
        docInfo = getIntent().getStringArrayExtra(AppConstants.documentTransferString);

        // Set up toolbar
        setUpToolbar();

        //Add alert dialogue builder
        builder = new AlertDialog.Builder(DocumentView.this);

        // Add progress bar to XML views and then call to make visible
        mProgressBar = (ProgressBar) findViewById(R.id.documentViewProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mImageView = (ImageView) findViewById(R.id.documentViewImageView);
        mAttacher = new PhotoViewAttacher(mImageView);

        GetDocumentImage getImage = new GetDocumentImage();
        getImage.execute(docInfo[0], docInfo[1]);

        GetDocumentData getData = new GetDocumentData();
        getData.execute(docInfo[1]);
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
            try {
                if (android.os.Debug.isDebuggerConnected()) {
                    android.os.Debug.waitForDebugger();
                }
                return mQueryManager.getImageResource(docInfo[1], docInfo[0]);
            } catch(java.lang.RuntimeException e){
                return null;
            }
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

            // If result has been retrieved
            if (result != null) {
                mImageView.setImageBitmap(result);
                mAttacher.update();
            }

            // If no result retrieved
            else {
                builder.setMessage(R.string.network_error_message)
                        .setTitle(R.string.network_error_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
                final AlertDialog networkErrorDialog = builder.create();
                networkErrorDialog.show();
            }
        }
    }

    // Creates an asynchronous task that gets the data for the document
    private class GetDocumentData extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... info){
            try {
                if (android.os.Debug.isDebuggerConnected()) {
                    android.os.Debug.waitForDebugger();
                }
                String listQuery = mQueryManager.constructListOfObjectsInCollectionQuery(info[0]);
                Log.d(TAG, listQuery);
                InputStream responseStream = mQueryManager.queryDigitalRepositoryAsync(listQuery);
                String response = null;
                try {
                    response = mQueryManager.readStringFromInputStream(responseStream);
                } catch (java.io.IOException e){
                    e.printStackTrace();
                }
                return response;
            } catch(java.lang.RuntimeException e){
                return null;
            }
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPreExecute() {
            //Turn Progress indicator off
        }

        protected void onPostExecute (String result) {
            //Turn Progress indicator off
            if(android.os.Debug.isDebuggerConnected()){
                android.os.Debug.waitForDebugger();
            }

            // If result has been retrieved
            if (result != null) {
                Log.d(TAG, result);
                String [] data = result.split(AppConstants.listOfObjectsInDocumentDelimeter);

                ArrayList<String> resultData = new ArrayList<String>(Arrays.asList(data));
                resultData.remove(0);

                docPageIds = resultData;

            }

            /*// If no result retrieved
            else {
                builder.setMessage(R.string.network_error_message)
                        .setTitle(R.string.network_error_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
                final AlertDialog networkErrorDialog = builder.create();
                networkErrorDialog.show();
            }*/
        }
    }

}
