package com.example.osulld13.digitalcollections;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
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

    private final int charsInListItemString = 30;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setUpToolbar();

        // Add progress bar to XML views and then call to make visible
        mProgressBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        // Get search bar and set listener for searching
        mSearchBar = (SearchView) findViewById(R.id.searchView);

        // Initialize Query constructor
        queryManager = new QueryManager();

        // Initialize response XML parser
        responseXMLParser = new ResponseXMLParser();

        // Get ListView
        mListView = (ListView) findViewById(R.id.searchListView);


        mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                initializeOnQueryTextListener(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void initializeOnQueryTextListener(String query) {
        // Turn Progress Indicator on
        mProgressBar.setVisibility(View.VISIBLE);
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
        documentsRetrieved = documentList;

        setListToRetrievedDocuments();

        //Turn Progress indicator off
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void setListToRetrievedDocuments() {
        String[] documentIds = new String[documentsRetrieved.size()];
        int i = 0;
        for( Document doc : documentsRetrieved ){
            documentIds[i] = doc.getText().substring(0, charsInListItemString);
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

}