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
import android.widget.ListView;
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
import lt.flickrfeed.justplius.app.network.XmlParser;
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
    private SearchView mSearchView;
    private Typeface tfRobotoMedium;
    private Typeface tfRobotoRegular;
    
    private View viewTransparentBackground;
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View view = inflater.inflate(R.layout.list_of_items, container, false);
        // Retrieve transparent background view, whose visibility will be handled during a runtime
        viewTransparentBackground = (View) view.findViewById(R.id.ViewTransparentBackground);
        
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Activate action bar with search capability on a Fragment
        setHasOptionsMenu(true);
        
        /* There is no memory efficient way to add TypeFaces
		 * to TextViews within XML layout, so they are
		 * set dynamically while layout inflation
		 * https://code.google.com/p/android/issues/detail?id=9904 
		 */
        
		// Font path
		String fontPathRobotoMedium = "fonts/roboto_medium.ttf";
		String fontPathRobotoRegular = "fonts/roboto_regular.ttf";

        // Loading Font Face
		tfRobotoMedium = Typeface.createFromAsset(getActivity().getAssets(), fontPathRobotoMedium);
		tfRobotoRegular = Typeface.createFromAsset(getActivity().getAssets(), fontPathRobotoRegular);
		
		feedItems = new ArrayList<FeedItem>();   
 
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        //restore last state for checked position.
        /*if (savedInstanceState != null) {
            currentListPosition = savedInstanceState.getInt("curChoice", 0);
        }*/
        
        // Execute asynchronous task to load photos feed
        String url = getString(R.string.url_get_flickr_photo_feed);
     	new DownloadFlickrFeedXmlTask().execute(url);

    }
 
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("curChoice", currentListPosition);
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
    
    // Handle list item's click by opening photo on full screen 
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {    	
    	//mainActivity.post_id = driverPosts.get(position).getId();
    }
    
    // Implementation of AsyncTask used to download XML feed from  flickr.
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
	        	
	        	postListAdapter = new FeedItemsListViewAdapter(getActivity(), items, tfRobotoMedium, tfRobotoRegular);
    	        postsFragment.setListAdapter(postListAdapter);
    	        postListAdapter.notifyDataSetChanged();

        	} else {
        		
        		// In case when no (or <= 3) results are returned inform
        		// that feed search by tag didn't gave any results
        		Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_results_match_query), Toast.LENGTH_LONG).show();
        		
        	}        	
        }
    }

    // Downloads XML from flickr feed, parses it and returns list of FeedItems
    private ArrayList<FeedItem> loadXmlFromNetwork(String urlString) 
    		throws XmlPullParserException, IOException {
    	  
    	// Parser for XML content
    	XmlParser xmlParser = new XmlParser();
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

        /*SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            SearchableInfo info = searchManager.getSearchableInfo(getActivity().getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }*/
        
        // Handler indicating if user is focused on search field
        mSearchView.setOnQueryTextFocusChangeListener(new OnFocusChangeListener () {

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
        
        mSearchView.setOnQueryTextListener(new OnQueryTextListener (){

        	// Handle search query after "Enter" was pressed
			@Override
			public boolean onQueryTextSubmit(String query) {
				
				// Depending on search fields' focus state
				// hide or show transparent background on ListView
		    	handleTransparentBackground("hide");
		    	
		    	// Replace spaces with commas 
				query = query.replaceAll(" ", ", ");
				// In case the query is with commas
				query = query.replaceAll(",,", ",");
				
				// Execute asynchronous task to reload photo
				// feed based on inserted tags

		        String url = getString(R.string.url_get_flickr_photo_feed);
		     	new DownloadFlickrFeedXmlTask().execute(url + "?tags=" + query);
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
    
    // Depending on search fields' focus state
	// hide or show transparent background on ListView
    private void handleTransparentBackground(String visibilityTask) {
    	
		if (visibilityTask.equals("show") && !isTransparentBackgroundShown) {
			viewTransparentBackground.setVisibility(View.VISIBLE);
			isTransparentBackgroundShown = true;
		} else {
			viewTransparentBackground.setVisibility(View.INVISIBLE);
			isTransparentBackgroundShown = false;
		}
				
	};
    
}
