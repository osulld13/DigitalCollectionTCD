package com.example.osulld13.digitalcollections;

import java.lang.Integer;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    private final String TAG = QueryManager.class.getSimpleName();

    private SearchView mSearchBar;
    private List<Document> documentsRetrieved;
    private QueryManager queryManager;
    private ResponseXMLParser responseXMLParser;
    private ListView mListView;
    private AlertDialog.Builder builder;

    private final int charsInListItemString = 35;

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

        // Get ListView and set its onItemClicked Listener
        mListView = (ListView) findViewById(R.id.searchListView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDocumentView(position);
            }
        });

        mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Turn Progress Indicator on
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

    // Creates an asynchronous task that gets the queries fedora for the results to the search
    private class GetSearchResults extends AsyncTask<String, Integer, List<Document>>{

        protected List<Document> doInBackground(String... queries){
            try {

                String query = queries[0];

                String solrQuery = queryManager.constructSolrQuery(query);

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
        String[] documentIds = new String[documentsRetrieved.size()];
        int i = 0;
        for( Document doc : documentsRetrieved ){
            documentIds[i] = doc.getText().substring(0, charsInListItemString) + "...";
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SearchActivity.this,
                android.R.layout.simple_list_item_1,
                documentIds
        );

        mListView.setAdapter(adapter);
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