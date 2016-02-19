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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.net.URLEncoder;


/**
 * Created by osulld13 on 07/02/16.
 */
public class QueryManager {

    private final String TAG = QueryManager.class.getSimpleName();

    public String constructSolrQuery(String freeQuery){
        String query = "http://library02.tchpc.tcd.ie:8080/solr/dris/select?indent=on&version=2.2&q=subject_lctgm%3AMaps&fq=" +
                urlQueryAdapter(freeQuery) +
                "&start=0&rows=1000&fl=*%2Cscore&qt=standard&wt=standard&explainOther=&hl.fl=";
        return query;
    }

    public Bitmap getImageResource(String drisFolderNum, String pid){
        URL url = getImageResourceURL(drisFolderNum, pid);
        Bitmap bmp = null;
        try {
             bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (java.io.IOException e){
            e.printStackTrace();
        }
        return bmp;
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

    private String readStringFromInputStream(InputStream inputStream) throws IOException {
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