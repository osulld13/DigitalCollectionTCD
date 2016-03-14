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
    private int currentResultsPage = 0;
    private int resultsPerPage = 15;
    private String lastQuery = "";

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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

        initListView();


        mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Turn Progress Indicator on
                currentResultsPage = 0;
                lastQuery = query;
                new GetSearchResults().execute(query);
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
        // Get ListView and set its onItemClicked Listener
        mListView = (ListView) findViewById(R.id.searchListView);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDocumentView(position);
            }
        });

        /*mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int firstVisibleItem;
            int visibleItemCount;
            int totalItemCount;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // if the end of the scrollbar is reached
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                        (firstVisibleItem + visibleItemCount) == ((currentResultsPage + 1) * resultsPerPage )){
                    currentResultsPage += 1;
                    mProgressBar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Time to load new results!");
                    Log.d(TAG, lastQuery);
                    //getNewResults
                    //new GetSearchResults().execute(lastQuery);
                    //appendThemToTheListView
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.firstVisibleItem = firstVisibleItem;
                this.visibleItemCount = visibleItemCount;
                this.totalItemCount = totalItemCount;
            }

        });*/
    }

    // Creates an asynchronous task that gets the queries fedora for the results to the search
    private class GetSearchResults extends AsyncTask<String, Integer, List<Document>>{

        boolean appendToList = false;

        public void updateGetSearchResults(boolean appendToList){
            this.appendToList = appendToList;
        }

        protected List<Document> doInBackground(String... queries){
            try {

                String query = queries[0];

                String solrQuery = queryManager.constructSolrQuery(query, currentResultsPage, resultsPerPage);

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

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPreExecute() {
            //Turn Progress indicator off
            mProgressBar.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute (List<Document> result) {
            // if result retrieved
            if (result != null) {
                //Turn Progress indicator off
                setListToRetrievedDocuments(result);
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


    private void setListToRetrievedDocuments(List<Document> documentsRetrieved) {
        this.documentsRetrieved = documentsRetrieved;
        if (documentsRetrieved.size() == 0 && adapter != null){
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        else if(documentsRetrieved.size() != 0) {

            String[] documentIds = new String[documentsRetrieved.size()];
            adapter = new SearchResultsAdapter(this, documentsRetrieved);
            mListView.setAdapter(adapter);
        }
        else{
            mListView.setEmptyView(findViewById(android.R.id.empty));
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