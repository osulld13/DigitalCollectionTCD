package com.example.osulld13.digitalcollections;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {

    private final String TAG = BookmarksActivity.class.getSimpleName();

    private ListView mListView;
    private ProgressBar mProgressBar;
    private DigitalCollectionsDbHelper mDbHelper;
    private ArrayList<Document> bookmarks;
    private SearchResultsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        setUpToolbar();
        bookmarks = new ArrayList<Document>();
        mListView = (ListView) findViewById(R.id.bookmarksListView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                goToDocumentView(position);
            }
        });
        mDbHelper = new DigitalCollectionsDbHelper(BookmarksActivity.this);
        mProgressBar = (ProgressBar) findViewById(R.id.bookmarkProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        GetBookmarksTask getBookmarksTask = new GetBookmarksTask();
        getBookmarksTask.execute();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(AppConstants.BookmarksActivityTitle);
    }

    private class GetBookmarksTask extends AsyncTask<Void, Void, ArrayList<Document>>{

        @Override
        protected ArrayList<Document> doInBackground(Void... params) {
            ArrayList<Document> bookmarks = getBookmarks();
            return bookmarks;
        }

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Document> bookmarks){
            mProgressBar.setVisibility(View.INVISIBLE);
            // Add bookmarks to listView
            setListToRetrievedDocuments(bookmarks, false);
        }
    }

    private ArrayList<Document> getBookmarks(){
        // Get bookmarks
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String query = "SELECT * FROM bookmark ORDER BY time DESC";
        Cursor c = db.rawQuery(query, null);

        int indexPid = c.getColumnIndex(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_PID);
        int indexFolder = c.getColumnIndex(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_FOLDER_NUMBER);
        int indexTitle = c.getColumnIndex(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_TITLE);
        int indexGenre = c.getColumnIndex(DigitalCollectionsContract.CollectionBookmark.COLUMN_NAME_GENRE);

        ArrayList<Document> bookmarks = new ArrayList<Document>();
        while (c.moveToNext()){
            String pid = c.getString(indexPid);
            String folder = c.getString(indexFolder);
            String title = c.getString(indexTitle);
            String genre = c.getString(indexGenre);
            bookmarks.add(new Document(pid, folder, title, genre));
        }

        return bookmarks;
    }

    private void setListToRetrievedDocuments(ArrayList<Document> bookmarks, boolean appendToList) {
        // if this is the first search
        if (bookmarks.size() == 0 && adapter != null){
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        // If this is a call to append to the list and the queryId is the same as when we sent the request, we append the
        // data to the list
        else if(appendToList == true){
            //Log.d(TAG, "Will append to the list");
            this.bookmarks.addAll(bookmarks);
            adapter.notifyDataSetChanged();
        }
        else if(bookmarks.size() != 0) {
            this.bookmarks = bookmarks;
            adapter = new SearchResultsAdapter(BookmarksActivity.this, bookmarks, R.layout.item_search_result, 1, AppConstants.backgroundDark);
            mListView.setAdapter(adapter);
        }
    }

    private void goToDocumentView(int listPosition){
        Intent documentViewIntent = new Intent(BookmarksActivity.this, DocumentView.class);
        documentViewIntent.putExtra(AppConstants.documentTransferString, bookmarks.get(listPosition).toArray());
        startActivity(documentViewIntent);
    }

}
