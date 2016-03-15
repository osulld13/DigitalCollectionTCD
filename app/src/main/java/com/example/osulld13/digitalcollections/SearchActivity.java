package com.example.osulld13.digitalcollections;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    private final String TAG = SearchActivity.class.getSimpleName();

    private SearchView mSearchBar;
    private List<Document> documentsRetrieved;
    private SearchResultsAdapter adapter;
    private QueryManager queryManager;
    private ResponseXMLParser responseXMLParser;
    private ListView mListView;
    private AlertDialog.Builder builder;
    private ProgressBar mProgressBar;

    private int currentQueryId = 0;
    private int currentResultsPage = 0;
    private int resultsPerPage = AppConstants.resultsPerSearchPage;
    private int searchResultsPaginateDistance = AppConstants.searchResultsPaginateDistance;
    private String lastQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Set up toolbar
        setUpToolbar();
        //Add alert dialogue builder
        builder = new AlertDialog.Builder(SearchActivity.this);
        // Add progress bar to XML views and then call to make visible
        mProgressBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        // Get search bar and set listener for searching
        mSearchBar = (SearchView) findViewById(R.id.searchView);
        mSearchBar.onActionViewExpanded();
        // Initialize Query constructor
        queryManager = new QueryManager();
        // Initialize response XML parser
        responseXMLParser = new ResponseXMLParser();
        //InitializeListView
        initListView();
        // add query listener for search bar
        mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null){
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }
                // Turn Progress Indicator on
                currentQueryId += 1;
                currentResultsPage = 0;
                lastQuery = query;
                GetSearchResults getSearchResults = new GetSearchResults();
                getSearchResults.updateGetSearchResults(false, currentQueryId, currentResultsPage);
                getSearchResults.execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO: Implement search suggestions
                return false;
            }
        });
    }

    private void initListView() {
        // Get ListView and
        mListView = (ListView) findViewById(R.id.searchListView);
        // set its onItemClicked Listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDocumentView(position);
            }
        });
        // set the scroll listener
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            int firstVisibleItem;
            int visibleItemCount;
            int totalItemCount;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // if the end of the scrollbar is reached
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                        (firstVisibleItem + visibleItemCount) >= (((currentResultsPage + 1) * resultsPerPage)) - searchResultsPaginateDistance) {
                    currentResultsPage += 1;
                    GetSearchResults getSearchResults = new GetSearchResults();
                    getSearchResults.updateGetSearchResults(true, currentQueryId, currentResultsPage);
                    getSearchResults.execute(lastQuery);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.firstVisibleItem = firstVisibleItem;
                this.visibleItemCount = visibleItemCount;
                this.totalItemCount = totalItemCount;
            }
        });
    }

    // Creates an asynchronous task that gets the queries fedora for the results to the search
    private class GetSearchResults extends AsyncTask<String, Integer, List<Document>>{

        // Append to list and query Id will be used if a paging request is being made
        boolean appendToList = false;
        int queryId;
        int resultsPage;

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

        protected void onPreExecute() {
            //Turn Progress indicator off
            mProgressBar.setVisibility(View.VISIBLE);
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
            mProgressBar.setVisibility(View.INVISIBLE);
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
        else if(appendToList == true && queryId == currentQueryId){
            //Log.d(TAG, "Will append to the list");
            this.documentsRetrieved.addAll(documentsRetrieved);
            adapter.notifyDataSetChanged();
        }
        else if(documentsRetrieved.size() != 0) {
            this.documentsRetrieved = documentsRetrieved;
            String[] documentIds = new String[documentsRetrieved.size()];
            adapter = new SearchResultsAdapter(this, documentsRetrieved);
            mListView.setAdapter(adapter);
        }
    }

    private void setUpToolbar() {
        // Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.search_activity_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void goToDocumentView(int listPosition){
        Intent documentViewIntent = new Intent(this, DocumentView.class);
        documentViewIntent.putExtra(AppConstants.documentTransferString, documentsRetrieved.get(listPosition).toArray());
        startActivity(documentViewIntent);
    }

}