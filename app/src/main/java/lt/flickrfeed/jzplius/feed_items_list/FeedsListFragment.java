package lt.flickrfeed.jzplius.feed_items_list;

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
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import lt.flickrfeed.jzplius.common.DownloadAndParseXML;
import lt.flickrfeed.jzplius.common.NetworkState;
import lt.flickrfeed.jzplius.feed_item.FeedItem;
import lt.jzplius.flickrfeed.R;

/**
 * This class parses XML feeds from flickr
 * Given an InputStream representation of a feed, it returns a ArrayList of FeedItem,
 * where each list element represents a single post in the XML feed.
 */
public class FeedsListFragment extends ListFragment{

    private static final String ERROR_TAG = "PhotosListFragment.java";

    private View mViewTransparentBackground;
    private SearchView mSearchView;
    // Specific font typefaces
    private Typeface mTfRobotoMedium;
    private Typeface mTfRobotoRegular;
    FeedItemsListViewAdapter mPostListAdapter = null;
    private ProgressBar mProgressBarFeedLoad;
    private DownloadAndParseXML<FlickrFeedXmlParser, ArrayList<FeedItem>> mDownloadFeedItems;
    private boolean mIsSearchSubmitted = false;
    private String mUrl;
    // Identify if items are currently being downloaded
    private boolean mIsBeingDownloaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mTfRobotoMedium = Typeface.createFromAsset(getActivity().getAssets(), fontPathRobotoMedium);
        mTfRobotoRegular = Typeface.createFromAsset(getActivity().getAssets(), fontPathRobotoRegular);
        mDownloadFeedItems = new DownloadFeedItems();

        mUrl = getString(R.string.url_get_flickr_photo_feed);

        // Handle internet connection, if no network available
        if (NetworkState.sIsConnected) {
            // Execute asynchronous task to load photos feed
            downloadFlickrFeed(mUrl);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_of_items, container, false);
        // Retrieve transparent background view, whose visibility will be visible
        // on action bar search event
        mViewTransparentBackground = view.findViewById(R.id.view_transparent_background);
        mViewTransparentBackground.setVisibility(View.VISIBLE);
        mProgressBarFeedLoad = (ProgressBar) view.findViewById(R.id.progress_bar_feed_load);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // When 4-th of 5 feed blocks is reached, download additional data
                if (view.getLastVisiblePosition() >= view.getCount() - 2) {
                    downloadFlickrFeed(mUrl);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (NetworkState.sIsConnected && mPostListAdapter == null) {
            // Execute asynchronous task to load photos feed
            downloadFlickrFeed(mUrl);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu, retrieve item's and view's references
        inflater.inflate(R.menu.menu_search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);
    }

    // Execute asynchronous task to load photos feed
    private void downloadFlickrFeed(String url) {
        if (!mIsBeingDownloaded) {
            mIsBeingDownloaded = !mIsBeingDownloaded;
            new DownloadFlickrFeedXmlTask().execute(url);
        }
    }

    // Implementation of AsyncTask used to download XML feed from Flickr.
    private class DownloadFlickrFeedXmlTask extends AsyncTask<String, Void, ArrayList<FeedItem>> {

        // Actions to be performed in background thread
        @Override
        protected ArrayList<FeedItem> doInBackground(String... urls) {
            try {
                // From given URL XML's parse and return list of FeedItems
                return mDownloadFeedItems.loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                Log.e(ERROR_TAG + " IOException", e.toString());
            } catch (XmlPullParserException e) {
                Log.e(ERROR_TAG + " XmlPullParserException", e.toString());
            }
            return null;
        }

        // Actions to be performed in main thread, after actions 
        // are completed in background thread
        @Override
        protected void onPostExecute(ArrayList<FeedItem> items) {
            // Use list of retrieved FeedItems
            if (items != null && items.size() >= 4) {
                // If a search query result, then reset the adapter,
                // otherwise append it with new data
                if (mIsSearchSubmitted || mPostListAdapter == null) {
                    mIsSearchSubmitted = false;
                    // Set a adapter to listView, to pair view items with
                    // their corresponding data
                    mPostListAdapter = new FeedItemsListViewAdapter(
                            getActivity(),
                            items,
                            mTfRobotoMedium,
                            mTfRobotoRegular);
                    setListAdapter(mPostListAdapter);
                } else {
                    mPostListAdapter.getItems().addAll(items);
                }

                mPostListAdapter.notifyDataSetChanged();
            } else {
                // In case when no (or <= 3) results are returned inform
                // that feed search by tag didn't gave any results.
                // (One view block consists of 4 feeds)
                Toast.makeText(
                        getActivity(),
                        getActivity().getResources().getString(R.string.no_results_match_query),
                        Toast.LENGTH_LONG)
                        .show();
            }
            mViewTransparentBackground.setVisibility(View.INVISIBLE);
            mProgressBarFeedLoad.setVisibility(View.INVISIBLE);
            mIsBeingDownloaded = !mIsBeingDownloaded;
        }
    }

    // Setup various states handlers for search view
    private void setupSearchView(MenuItem searchItem) {
        searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        // Handler indicating if user is focused on search field
        mSearchView.setOnQueryTextFocusChangeListener(new OnFocusChangeListener() {

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
        mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
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
                mIsSearchSubmitted = true;
                mUrl = getString(R.string.url_get_flickr_photo_feed) + "?tags=" + query;
                downloadFlickrFeed(mUrl);

                // Reset user focus, so that results could be seen in full screen
                mSearchView.clearFocus();

                return false;
            }

            // Handler following every letter change on SearchView
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Set search text hint
        mSearchView.setQueryHint(getString(R.string.search_query_hint));
    }

    // Depending on search field focus state
    // hide or show transparent background on ListView
    private void handleTransparentBackground(String visibilityTask) {
        if (visibilityTask.equals("show") && !mViewTransparentBackground.isShown()) {
            mViewTransparentBackground.setVisibility(View.VISIBLE);
        } else {
            mViewTransparentBackground.setVisibility(View.INVISIBLE);
        }
    }

    // Downloads XML from Flickr feed, parses it and returns ArrayList<FeedItem>
    private class DownloadFeedItems extends DownloadAndParseXML<FlickrFeedXmlParser, ArrayList<FeedItem>> {

        /**
         * In subclasses override this method to provide specific
         * XMLPullParser, that extends BaseXmlParser
         */
        @Override
        protected FlickrFeedXmlParser setParser() {
            return new FlickrFeedXmlParser("feed");
        }
    }
}
