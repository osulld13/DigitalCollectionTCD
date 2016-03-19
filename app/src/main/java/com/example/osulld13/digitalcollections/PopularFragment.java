package com.example.osulld13.digitalcollections;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PopularFragment extends Fragment {

    private final String TAG = PopularFragment.class.getSimpleName();

    private GridView mGridView;
    private QueryManager queryManager;
    private List<Document> documentsRetrieved;
    private AlertDialog.Builder builder;
    private SearchResultsAdapter adapter;
    private ResponseJSONParser responseJSONParser;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular, container, false);

        responseJSONParser = new ResponseJSONParser();
        queryManager = new QueryManager();
        builder = new AlertDialog.Builder(getContext());

        mGridView = (GridView) rootView.findViewById(R.id.popularGridView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                goToDocumentView(position);
            }
        });

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.popularProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        /*GetSearchResults getSearchResults = new GetSearchResults();
        getSearchResults.execute("cork");*/
        GetPopularDocuments getPopularDocuments = new GetPopularDocuments();
        getPopularDocuments.execute();
        return rootView;
    }

    // Creates an asynchronous task that gets the queries fedora for the results to the search
    private class GetPopularDocuments extends AsyncTask<Void, Void, ArrayList<String[]>> {


        protected ArrayList<String[]> doInBackground(Void... params){
            try {
                if (android.os.Debug.isDebuggerConnected()) {
                    android.os.Debug.waitForDebugger();
                }
                String query = queryManager.constructPopularItemsQuery();
                InputStream responseStream = queryManager.queryUrlForDataStream((String) query);
                ArrayList<String[]> documentList = null;
                try {
                    //documentList = responseXMLParser.parseSearchResponse(responseStream);
                    //String response = queryManager.readStringFromInputStream(responseStream);
                    //Log.d(TAG, response);
                    documentList = responseJSONParser.parsePopularData(responseStream);
                } catch (java.io.IOException e){
                    // Add error dialogue
                    e.printStackTrace();
                }/* catch (XmlPullParserException e){
                    // Add error dialogue
                    e.printStackTrace();
                }*/
                // Assign the retrieved documents to the documentsRetrieved List
                return documentList;
            } catch (java.lang.RuntimeException e){
                return null;
            }
        }

        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute (ArrayList<String[]> result) {
            // if result retrieved
            if (result != null) {
               //Do stuff
                GetMetadataForPopularDocuments getMetadataForPopularDocuments = new GetMetadataForPopularDocuments();
                getMetadataForPopularDocuments.execute(result);
            }
            // if no result retrieved
            else {
                mProgressBar.setVisibility(View.INVISIBLE);
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

    private class GetMetadataForPopularDocuments extends AsyncTask<List<String []>, Void, List<Document>>{

        List<String[]> objects;

        protected List<Document> doInBackground(List<String[]>... params){
            if (android.os.Debug.isDebuggerConnected()) {
                android.os.Debug.waitForDebugger();
            }

            objects = params[0];

            List<Document> result = new ArrayList<Document>();

            for(String [] object : params[0]){
                String query = queryManager.constructDocMetadataQuery(object[0]);
                InputStream responseStream = queryManager.queryUrlForDataStream((String) query);
                String [] documentMedata = {"", ""};
                try {
                    documentMedata = responseJSONParser.getPopularItemMetadata(responseStream);
                }catch (java.io.IOException e){
                    return null;
                }
                Document doc = new Document(object[0], object[1], documentMedata[0], documentMedata[1]);
                result.add(doc);
            }
            return result;
        }

        protected void onPostExecute(List<Document> documents){
            if(documents != null){
                setListToRetrievedDocuments(documents, false);
            }
            mProgressBar.setVisibility(View.INVISIBLE);
        }

    }

    private void setListToRetrievedDocuments(List<Document> documentsRetrieved, boolean appendToList) {
        // if this is the first search
        if (documentsRetrieved.size() == 0 && adapter != null){
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        // If this is a call to append to the list and the queryId is the same as when we sent the request, we append the
        // data to the list
        else if(appendToList == true){
            //Log.d(TAG, "Will append to the list");
            this.documentsRetrieved.addAll(documentsRetrieved);
            adapter.notifyDataSetChanged();
        }
        else if(documentsRetrieved.size() != 0) {
            this.documentsRetrieved = documentsRetrieved;
            adapter = new SearchResultsAdapter(getContext(), documentsRetrieved, R.layout.item_grid_element, 1, AppConstants.backgroundDark);
            mGridView.setAdapter(adapter);
        }
    }

    private void goToDocumentView(int listPosition){
        Intent documentViewIntent = new Intent(getContext(), DocumentView.class);
        documentViewIntent.putExtra(AppConstants.documentTransferString, documentsRetrieved.get(listPosition).toArray());
        startActivity(documentViewIntent);
    }
}
