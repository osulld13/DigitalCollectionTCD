package com.example.osulld13.digitalcollections;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Created by osulld13 on 07/02/16.
 */
public class QueryManager {

    private final String TAG = QueryManager.class.getSimpleName();

    public String constructSolrQuery(String freeQuery){
        String query = "http://library02.tchpc.tcd.ie:8080/solr/dris/select?indent=on&version=2.2&q=subject_lctgm%3A[*%20TO%20*]&fq=" +
                urlQueryAdapter(freeQuery) +
                "&start=0&rows=15&fl=*%2Cscore&qt=standard&wt=standard&explainOther=&hl.fl=";
        return query;
    }

    public String constructListOfObjectsInCollectionQuery(String drisFolderNumber){
        String query =  "http://digitalcollections.tcd.ie/orderedListOfObjectsInCollection.php?folder=" + urlQueryAdapter(drisFolderNumber);
        return query;
    }

    public String constructDocMetadataQuery(String pId){
        String query = "http://digitalcollections.tcd.ie/home/getMeta.php?pid=" + pId;
        return query;
    }

    public Bitmap getImageResource(String drisFolderNum, String pid){
        URL url = getImageResourceURL(drisFolderNum, pid);
        Bitmap bmp = null;
        try {
            // Make sure image complies with memory limits

            // Load in info
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);

            // Calculate whether to load in resampled image
            options.inSampleSize = calculateInSampleSize(options,
                    AppConstants.documentImageWidth,
                    AppConstants.documentImageHeight);

            //Load in resampled image
            options.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);

        } catch (java.io.IOException e){
            e.printStackTrace();
        }
        return bmp;
    }

    public Bitmap getImageThumbnailResource(String pid){
        URL url = getResourceThumbnailURL(pid);
        Bitmap bmp = null;
        try {
            // Make sure image complies with memory limits

            // Load in info
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);

            // Calculate whether to load in resampled image
            options.inSampleSize = calculateThumbnailSampleSize(options,
                    AppConstants.thumbnailImageWidth,
                    AppConstants.thumbnailImageHeight);

            //Load in resampled image
            options.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);

        } catch (java.io.IOException e){
            e.printStackTrace();
        }
        return bmp;
    }

    // Code got from http://developer.android.com/training/displaying-bitmaps/load-bitmap.html#load-bitmap
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 4;
            }
        }

        return inSampleSize;
    }

    // Code got from http://developer.android.com/training/displaying-bitmaps/load-bitmap.html#load-bitmap
    public static int calculateThumbnailSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private URL getImageResourceURL(String drisFolderNum, String pid){
        String urlString =  "http://digitalcollections.tcd.ie/content/"+drisFolderNum+"/jpeg/"+pid+"_LO.jpg";
        URL url = null;
        try {
            url =  new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, url.toString());
        return url;
    }

    private URL getResourceThumbnailURL(String pid){
        //String urlString =  "http://digitalcollections.tcd.ie/covers_220/"+pid+"_LO.jpg";
        String urlString =  "http://digitalcollections.tcd.ie/covers_thumbs/"+pid+"_LO.jpg";
        URL url = null;
        try {
            url =  new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, url.toString());
        return url;
    }

    public InputStream queryDigitalRepositoryAsync(String url){

        // Have one (or more) threads ready to do the async tasks. Do this during startup of your app.
        ExecutorService executor = Executors.newFixedThreadPool(1);

        InputStream responseStream = null;
        try {
            // Fire a request.
            Future<Response> response = executor.submit(new Request(new URL(url)));

            // Do your other tasks here (will be processed immediately, current thread won't block).

            // Get the response (here the current thread will block until response is returned).
            responseStream = response.get().getBody();

        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch(java.lang.InterruptedException e){
            e.printStackTrace();
        }
        catch (java.util.concurrent.ExecutionException e){
            e.printStackTrace();
        }
        catch (java.io.IOException e){
            e.printStackTrace();
        }

        // Shutdown the threads during shutdown of your app.
        executor.shutdown();

        return responseStream;

    }

    public String readStringFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }

    // Adapts queries to make them safe for HTTP transfer
    private String urlQueryAdapter(String query){
        // replace spaces with
        try {
            query = query.toLowerCase();
            query = query.replaceAll("[^a-zA-Z0-9\\s]", "");
            query = URLEncoder.encode(query, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e){
            // add error dialogue
            e.printStackTrace();
        }
        return query;
    }
}