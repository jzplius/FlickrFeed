package lt.flickrfeed.justplius.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import lt.flickrfeed.justplius.app.imports.BackStackDoubleTapExit;
import lt.flickrfeed.justplius.app.imports.ConnectionChangeReceiver;
import lt.flickrfeed.justplius.app.imports.NetworkState;
import lt.justplius.flickrfeed.R;

/**
 * This activity inflates a PhotosListFragment, which handles most of the app job. It also
 * initializes and instantiates ImageLoader (a included library)
 */

public class PhotoFeedActivity extends FragmentActivity {

    private static String TAG = "PhotoFeedActivity.java";

    // Indicators showing if fragment is inflated
    private static final String KEY_IS_FRAGMENT_INFLATED = "is_fragment_inflated";
    private boolean isFragmentInflated = false;

    // Object responsible for images loading
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    // Receiver handling internet connection change events
    protected ConnectionChangeReceiver ccr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            // Require action bar to be visible on view
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

            setContentView(R.layout.fragment_container);

            // Initialize image Loader for first time
            initImageLoader(getApplicationContext());

            // At starting activity perform initial internet connection check.
            // Internet connection changes will he handled by BroadcastReceiver
            // which is executed at background thread
            if (NetworkState.isNetworkAvailable(getApplicationContext())) {

                // inflate new PhotosListFragment
                inflateFragment();

            } else {

                //handle internet connection, if no network available
                NetworkState.handleIfNoNetworkAvailable(getApplicationContext());

            }

            // Register connection change receiver
            ccr = ConnectionChangeReceiver.getInstance();

        }
    }

    // Returns instantiated imageLoader to child elements / adapters
    public ImageLoader getImageLoader() {

        return imageLoader;
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

    // This configuration tuning is custom. You can tune every option, you may tune
    // some of them, or you can create default configuration by
    // ImageLoaderConfiguration.createDefault(this); method.
    public static void initImageLoader(Context context) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

    }


    // Disable on back button pressed event by default
    // and exit from app on back button pressed twice
    @Override
    public void onBackPressed() {

        // If the button has been pressed twice go to the main screen of phone
        new BackStackDoubleTapExit(getApplicationContext());

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

    private void inflateFragment() {

        // inflate new PhotosListFragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new PhotosListFragment()).commit();
        isFragmentInflated = true;

    }

}
