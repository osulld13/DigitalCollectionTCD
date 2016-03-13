package com.example.osulld13.digitalcollections;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by Donal on 13/03/2016.
 */
// Creates an asynchronous task that gets the image for the document view
public class GetThumbnailImage extends AsyncTask<Void, Void, Bitmap> {

    private String pId;
    private ImageView imageView;
    private QueryManager mQueryManager;
    private int size;

    public void updateInfoSyncTask(String pId, ImageView imageView, QueryManager queryManager, int size){
        this.pId = pId;
        this.imageView = imageView;
        this.mQueryManager = queryManager;
        this.size = size;
    }

    protected Bitmap doInBackground(Void... params){
        try {
            if (android.os.Debug.isDebuggerConnected()) {
                android.os.Debug.waitForDebugger();
            }
            return mQueryManager.getImageThumbnailResource(pId, size);
        } catch(java.lang.RuntimeException e){
            return null;
        }
    }

    protected void onPostExecute (Bitmap result) {
        //Turn Progress indicator off
        if(android.os.Debug.isDebuggerConnected()){
            android.os.Debug.waitForDebugger();
        }

        // If result has been retrieved
        if (result != null) {
            this.imageView.setImageBitmap(result);
        }

    }
}