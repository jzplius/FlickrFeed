package lt.flickrfeed.justplius.app;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import lt.flickrfeed.justplius.app.feed.FeedItem;
import lt.flickrfeed.justplius.app.feed.FeedItemsListViewAdapter;
import lt.flickrfeed.justplius.app.imports.NetworkState;
import lt.flickrfeed.justplius.app.network.FlickrFeedXmlParser;
import lt.justplius.flickrfeed.R;

/**
 * This class parses XML feeds from flickr
 * Given an InputStream representation of a feed, it returns a ArrayList of FeedItem,
 * where each list element represents a single entry (post) in the XML feed.
 */

public class PhotosListFragment extends ListFragment {

    // Indicates whether transparent background is shown on ListView
    public static boolean isTransparentBackgroundShown = false;

    private static String TAG = "PhotosListFragment.java";

    // Feed results array
    private ArrayList<FeedItem> feedItems;
    // Adapter linking ListView item and FeedItem
    private FeedItemsListViewAdapter postListAdapter;

    private ImageLoader imageLoader;

    private PhotosListFragment postsFragment = this;
    private SearchView searchView;
    private Typeface tfRobotoMedium;
    private Typeface tfRobotoRegular;

    private View viewTransparentBackground;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle internet connection, if no network available
        NetworkState.handleIfNoNetworkAvailable(getActivity().getApplicationContext());

        // Activate action bar with search capability on a Fragment
        setHasOptionsMenu(true);

        /* There is no memory efficient way to add TypeFaces
         * to TextViews within XML layout, so they are
		 * set dynamically while on layout inflation
		 * https://code.google.com/p/android/issues/detail?id=9904
		 */

        // Font path
        String fontPathRobotoMedium = "fonts/roboto_medium.ttf";
        String fontPathRobotoRegular = "fonts/roboto_regular.ttf";

        // Loading Font Face
        tfRobotoMedium = Typeface.createFromAsset(getActivity().getAssets(), fontPathRobotoMedium);
        tfRobotoRegular = Typeface.createFromAsset(getActivity().getAssets(), fontPathRobotoRegular);

        feedItems = new ArrayList<FeedItem>();

        // Execute asynchronous task to load photos feed
        String url = getString(R.string.url_get_flickr_photo_feed);
        new DownloadFlickrFeedXmlTask().execute(url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_of_items, container, false);

        // Retrieve transparent background view, whose visibility will be visible
        // on action bar search event
        viewTransparentBackground = (View) view.findViewById(R.id.ViewTransparentBackground);

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu, retrieve item's and view's references
        inflater.inflate(R.menu.menu_search_view, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        setupSearchView(searchItem);

    }

    // Implementation of AsyncTask used to download XML feed from Flickr.
    private class DownloadFlickrFeedXmlTask extends AsyncTask<String, Void, ArrayList<FeedItem>> {

        // Actions to be performed in background thread
        @Override
        protected ArrayList<FeedItem> doInBackground(String... urls) {

            try {

                // From given URL string's XML parse and return list of FeedItems
                return loadXmlFromNetwork(urls[0]);

            } catch (IOException e) {
                Log.e(TAG + " IOException: ", e.toString());
            } catch (XmlPullParserException e) {
                Log.e(TAG + "XmlPullParserException: ", e.toString());
            }

            return null;
        }

        // Actions to be performed in main thread, after actions 
        // are completed in background thread
        @Override
        protected void onPostExecute(ArrayList<FeedItem> items) {

            // Use list of retrieved FeedItems
            if (items != null && items.size() >= 4) {

                // Set a adapter to listView, to pair view items with their corresponding data
                postListAdapter = new FeedItemsListViewAdapter(getActivity(), items, tfRobotoMedium, tfRobotoRegular);
                postsFragment.setListAdapter(postListAdapter);
                postListAdapter.notifyDataSetChanged();

            } else {

                // In case when no (or <= 3) results are returned inform
                // that feed search by tag didn't gave any results.
                // (One view block consists of 4 feeds)
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_results_match_query), Toast.LENGTH_LONG).show();

            }
        }
    }

    // Downloads XML from Flickr feed, parses it and returns list of FeedItems
    private ArrayList<FeedItem> loadXmlFromNetwork(String urlString)
            throws XmlPullParserException, IOException {

        // Parser for XML content
        FlickrFeedXmlParser xmlParser = new FlickrFeedXmlParser();
        ArrayList<FeedItem> feedItems = null;
        InputStream stream = null;

        try {

            // Download feed from given URL string
            stream = downloadUrl(urlString);
            // Parse given stream and return list of FeedItems
            feedItems = xmlParser.parse(stream);

        } finally {

            if (stream != null) {
                stream.close();
            }

        }

        // XmlParser returns a List (called "FeedItems") of FeedItem objects.
        // Each FeedItem object represents a single post in the XML feed.        
        return feedItems;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();

        return stream;
    }

    // Setup various states handlers for search view
    private void setupSearchView(MenuItem searchItem) {

        searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        // Handler indicating if user is focused on search field
        searchView.setOnQueryTextFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                // Depending on search fields' focus state
                // hide or show transparent background on ListView
                if (hasFocus) {
                    handleTransparentBackground("show");
                } else {
                    handleTransparentBackground("hide");
                }

            }

        });

        // Handle query submit event
        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            // Handle search query after "Enter" was pressed
            @Override
            public boolean onQueryTextSubmit(String query) {

                // Depending on search fields' focus state
                // hide or show transparent background on ListView
                handleTransparentBackground("hide");

                // Replace spaces with commas, to set each word as
                // a separate search tag

                query = query.replaceAll(" ", ", ");
                // In case the initial query was with commas
                query = query.replaceAll(",,", ",");

                // Execute asynchronous task to reload photo
                // feed based on inserted tags
                String url = getString(R.string.url_get_flickr_photo_feed);
                new DownloadFlickrFeedXmlTask().execute(url + "?tags=" + query);
                // Reset user focus, so that results could be seen in full screen
                searchView.clearFocus();

                return false;
            }

            // Handler following every letter change on SearchView
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        // Set search text hint
        searchView.setQueryHint(getString(R.string.search_query_hint));
    }

    // Depending on search field focus state
    // hide or show transparent background on ListView
    private void handleTransparentBackground(String visibilityTask) {

        if (visibilityTask.equals("show") && !isTransparentBackgroundShown) {
            viewTransparentBackground.setVisibility(View.VISIBLE);
            isTransparentBackgroundShown = true;
        } else {
            viewTransparentBackground.setVisibility(View.INVISIBLE);
            isTransparentBackgroundShown = false;
        }

    }

    ;

}
