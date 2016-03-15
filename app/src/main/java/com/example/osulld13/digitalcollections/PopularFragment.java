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
import android.widget.AdapterView;
import android.widget.GridView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.util.List;

public class PopularFragment extends Fragment {

    private GridView mGridView;
    private QueryManager queryManager;
    private List<Document> documentsRetrieved;
    private AlertDialog.Builder builder;
    private SearchResultsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular, container, false);

        queryManager = new QueryManager();
        builder = new AlertDialog.Builder(getContext());

        mGridView = (GridView) rootView.findViewById(R.id.popularGridView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                goToDocumentView(position);
            }
        });

        GetSearchResults getSearchResults = new GetSearchResults();
        getSearchResults.execute("cork");
        return rootView;
    }

    // Test Code taken from SearchActivity
    // Creates an asynchronous task that gets the queries fedora for the results to the search
    private class GetSearchResults extends AsyncTask<String, Integer, List<Document>> {

        // Append to list and query Id will be used if a paging request is being made
        boolean appendToList = false;
        int queryId;
        int resultsPage;
        int resultsPerPage = 20 ;
        ResponseXMLParser responseXMLParser = new ResponseXMLParser();

        public void updateGetSearchResults(boolean appendToList, int queryId, int resultsPage){
            this.appendToList = appendToList;
            this.queryId = queryId;
            this.resultsPage = resultsPage;
        }

        protected List<Document> doInBackground(String... queries){
            try {
                if (android.os.Debug.isDebuggerConnected()) {
                    android.os.Debug.waitForDebugger();
                }
                String query = queries[0];
                String solrQuery = queryManager.constructSolrQuery(query, this.resultsPage, resultsPerPage);
                InputStream responseStream = queryManager.queryDigitalRepositoryAsync((String) solrQuery);
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

        protected void onPostExecute (List<Document> result) {
            // if result retrieved
            if (result != null) {
                //Turn Progress indicator off
                setListToRetrievedDocuments(result, this.appendToList, this.queryId);
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
        }
    }

    private void setListToRetrievedDocuments(List<Document> documentsRetrieved, boolean appendToList, int queryId) {
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
