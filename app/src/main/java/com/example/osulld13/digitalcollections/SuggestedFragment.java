package com.example.osulld13.digitalcollections;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SuggestedFragment extends Fragment {

    private final String TAG = SuggestedFragment.class.getSimpleName();

    private QueryManager queryManager;
    private DigitalCollectionsDbHelper mDbHelper;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private SearchResultsAdapter adapter;
    private List<Document> documentsRetrieved;
    private AlertDialog.Builder builder;
    private ResponseXMLParser responseXMLParser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_suggested, container, false);
        // Initialize Query constructor
        queryManager = new QueryManager();
        // Initialize dbManager
        mDbHelper = new DigitalCollectionsDbHelper(getContext());
        //Add alert dialogue builder
        builder = new AlertDialog.Builder(getContext());
        // Initialize GridView
        mGridView = (GridView) rootView.findViewById(R.id.suggestedGridView);
        mGridView.setVisibility(View.INVISIBLE);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                goToDocumentView(position);
            }
        });

        responseXMLParser = new ResponseXMLParser();

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.suggestedProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mTextView = (TextView) rootView.findViewById(R.id.suggestedTextView);
        mTextView.setVisibility(View.INVISIBLE);

        documentsRetrieved = new ArrayList<>();
        adapter = new SearchResultsAdapter(getContext(), documentsRetrieved, R.layout.item_grid_element, 1, AppConstants.backgroundDark);
        mGridView.setAdapter(adapter);

        GetSuggestedQueries getSuggestedQueries = new GetSuggestedQueries();
        getSuggestedQueries.execute();

        return rootView;
    }

    private List<String> getPreviousSearches() {
        SQLiteDatabase db;// Read out database
        db = mDbHelper.getReadableDatabase();
        String sortOrder = DigitalCollectionsContract.CollectionQuery.COLUMN_NAME_TIME + " DESC";
        Cursor c = db.query(
                DigitalCollectionsContract.CollectionQuery.TABLE_NAME,  // The table to query
                null,                                                   // The columns to return
                null,                                                   // The columns for the WHERE clause
                null,                                                   // The values for the WHERE clause
                null,                                                   // don't group the rows
                null,                                                   // don't filter by row groups
                sortOrder                                               // The sort order
        );
        int index = c.getColumnIndex(DigitalCollectionsContract.CollectionQuery.COLUMN_NAME_TEXT);

        int retrievedQueries = 0;
        List<String> previousQueries = new ArrayList<String>();
        while(c.moveToNext() && retrievedQueries < AppConstants.numberOfSearchesForSuggestions) {
            String query =  c.getString(index);
            if(!previousQueries.contains(query)){
                previousQueries.add(query);
                retrievedQueries ++;
            }
        }
        return previousQueries;
    }

    private class GetSuggestedQueries extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            return getPreviousSearches();
        }

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<String> result){
            if(result.isEmpty()){
                mProgressBar.setVisibility(View.INVISIBLE);
                mTextView.setVisibility(View.VISIBLE);
            }
            else {
                for (String s : result) {
                    new GetSuggestedResults().execute(s);
                }
            }
        }
    }

    // Creates an asynchronous task that gets the queries fedora for the results to the search
    private class GetSuggestedResults extends AsyncTask<String, Integer, List<Document>> {

        // Append to list and query Id will be used if a paging request is being made
        boolean appendToList = true;
        String query;

        protected List<Document> doInBackground(String... queries){
            try {
                if (android.os.Debug.isDebuggerConnected()) {
                    android.os.Debug.waitForDebugger();
                }
                query = queries[0];
                String solrQuery = queryManager.constructSolrQuery(query, 0, AppConstants.resultsPerSuggestion);
                InputStream responseStream = queryManager.queryUrlForDataStream((String) solrQuery);
                List<Document> documentList = null;
                try {
                    documentList = responseXMLParser.parseSearchResponse(responseStream);
                } catch (java.io.IOException e){
                    // Add error dialogue
                    e.printStackTrace();
                } catch (XmlPullParserException e){
                    // Add error dialogue
                    e.printStackTrace();
                }
                // Assign the retrieved documents to the documentsRetrieved List
                return documentList;
            } catch (java.lang.RuntimeException e){
                return null;
            }
        }

        protected void onPreExecute() {
            //Turn Progress indicator off
            mProgressBar.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute (List<Document> result) {
            // if result retrieved
            if (result != null) {
                setListToRetrievedDocuments(result, this.appendToList);
            }
            // if no result retrieved
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
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void setListToRetrievedDocuments(List<Document> documentsRetrieved, boolean appendToList) {
        if(documentsRetrieved.size() != 0 && appendToList == true){
            this.documentsRetrieved.addAll(documentsRetrieved);
            adapter.notifyDataSetChanged();
            mGridView.setVisibility(View.VISIBLE);
        }
    }

    private void goToDocumentView(int listPosition){
        Intent documentViewIntent = new Intent(getContext(), DocumentView.class);
        documentViewIntent.putExtra(AppConstants.documentTransferString, documentsRetrieved.get(listPosition).toArray());
        startActivity(documentViewIntent);
    }

}
