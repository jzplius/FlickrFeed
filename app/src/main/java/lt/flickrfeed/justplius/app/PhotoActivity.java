package lt.flickrfeed.justplius.app;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import lt.flickrfeed.justplius.app.imports.ConnectionChangeReceiver;
import lt.flickrfeed.justplius.app.imports.NetworkState;
import lt.justplius.flickrfeed.R;

/**
 * If there is intent EXTRA_URL_STRING provided, activity reads it's value and bypasses it
 * to fragment. Otherwise activity automatically closes.
 */
public class PhotoActivity extends FragmentActivity {

    // Indicators showing if fragment is inflated
    private static final String KEY_IS_FRAGMENT_INFLATED = "is_fragment_inflated";
    private boolean isFragmentInflated = false;
    private PhotoFragment fragment;

    public static final String EXTRA_URL_PHOTO = "lt.flickrfeed.justplius.app.url_photo";
    public static final String EXTRA_URL_PHOTO_NO_SECRET = "lt.flickrfeed.justplius.app.url_photo_no_secret";
    public static final String EXTRA_PHOTO_ID = "lt.flickrfeed.justplius.app.photo_id";
    public static final String EXTRA_URL_PHOTO_LARGE = "lt.flickrfeed.justplius.app.url_photo_large";

    // Receiver handling internet connection change events
    protected ConnectionChangeReceiver ccr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_container);

        Bundle extras = getIntent().getExtras();

        // If there is no EXTRAS given, then return to previous activity
        if (extras == null) {

            finish();

        } else {

            // Pass EXTRAS as a bundle parameters to fragment and start it
            String photoUrl = extras.getString(EXTRA_URL_PHOTO, "https://www.flickr.com/images/buddyicon.gif");
            String photoId = extras.getString(EXTRA_PHOTO_ID);
            String photoUrlNoSecret = extras.getString(EXTRA_URL_PHOTO_NO_SECRET);
            String photoUrlLarge = extras.getString(EXTRA_URL_PHOTO_LARGE);

            fragment = PhotoFragment.newInstance(photoUrl, photoId, photoUrlNoSecret, photoUrlLarge);

            if (NetworkState.isConnected) {

                inflateFragment();

            } else {

                //handle internet connection, if no network available
                NetworkState.handleIfNoNetworkAvailable(getApplicationContext());

            }

        }

        // Register connection change receiver
        ccr = ConnectionChangeReceiver.getInstance();

    }

    /**
     * Use this method to create a new instance of
     * this Activities  using the provided parameters.
     * returns a new instance of fragment PhotoFragment.
     */
    public static Intent newInstance(Context context, String finalUrlPhoto, String finalPhotoId,
                                     String finalPhotoUrlNoSecret, String finalPhotoUrlLarge) {

        Intent intent = new Intent(context.getApplicationContext(), PhotoActivity.class);
        intent.putExtra(PhotoActivity.EXTRA_URL_PHOTO, finalUrlPhoto);
        intent.putExtra(PhotoActivity.EXTRA_PHOTO_ID, finalPhotoId);
        intent.putExtra(PhotoActivity.EXTRA_URL_PHOTO_NO_SECRET, finalPhotoUrlNoSecret);
        intent.putExtra(PhotoActivity.EXTRA_URL_PHOTO_LARGE, finalPhotoUrlLarge);

        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register receiver in onResume() activity callback
        ccr.registerBroadcastReceiver(getApplicationContext());

        if (NetworkState.isConnected && !isFragmentInflated) {

            // inflate new PhotosListFragment
            inflateFragment();

        } else {

            //handle internet connection, if no network available
            NetworkState.handleIfNoNetworkAvailable(getApplicationContext());

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister receiver in onPause() activity callback
        ccr.unregisterBroadcastReceiver(getApplicationContext());

    }

    private void inflateFragment() {

        // inflate new PhotoFragment
        getFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
        isFragmentInflated = true;

    }

    // Save information, whether fragment has been inflated
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_IS_FRAGMENT_INFLATED, isFragmentInflated);
    }

    // Retrieve information, whether fragment has been inflated
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {

            isFragmentInflated = savedInstanceState.getBoolean(KEY_IS_FRAGMENT_INFLATED);

        }
    }

}
