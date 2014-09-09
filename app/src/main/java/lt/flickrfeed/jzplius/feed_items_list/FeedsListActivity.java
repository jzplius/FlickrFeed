package lt.flickrfeed.jzplius.feed_items_list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

import lt.flickrfeed.jzplius.BaseFlickrFeedActivity;
import lt.flickrfeed.jzplius.common.BackStackDoubleTapExit;
import lt.flickrfeed.jzplius.common.NetworkState;
import lt.jzplius.flickrfeed.R;

/**
 * This activity inflates a PhotosListFragment, which handles most of the app job.
 */
public class FeedsListActivity extends BaseFlickrFeedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Require action bar to be visible on view
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.fragment_container);

        // At Launcher Activity perform initial internet connection check.
        // Later on internet connection changes will he handled automatically
        // by BroadcastReceiver which is executed at background thread
        NetworkState.sIsConnected = NetworkState.isNetworkAvailable(this);
        if (NetworkState.sIsConnected) {
            // Inflate new PhotosListFragment
            inflateFragment();
        }
    }

    @Override
    protected Fragment setFragment() {
        return new FeedsListFragment();
    }

    // Disable on back button pressed event by default
    // and exit from app on back button pressed twice
    @Override
    public void onBackPressed() {
        // If the button has been pressed twice go to the main screen of phone
        new BackStackDoubleTapExit(this);
    }
}
