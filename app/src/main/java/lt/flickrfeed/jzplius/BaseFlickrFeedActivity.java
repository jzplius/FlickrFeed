package lt.flickrfeed.jzplius;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import lt.flickrfeed.jzplius.common.BaseActivity;
import lt.flickrfeed.jzplius.common.NetworkState;
import lt.jzplius.flickrfeed.R;

/**
 * This class provides base activity for subclasses. It extends control of network
 * connectivity state, instantiates ImageLoader and provides abstract functionality
 * for Fragments management.
 */
public abstract class BaseFlickrFeedActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize image Loader for first time
        initImageLoader(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // All later internet connection checks are handled in onStart()
        if (NetworkState.sIsConnected) {
            // Inflate prepared to inflate Fragment
            inflateFragment();
        } else {
            // Handle internet connection, if no network available
            NetworkState.handleIfNoNetworkAvailable(this);
        }
    }

    // This configuration tuning is custom. You can tune every option, you may tune
    // some of them, or you can create default configuration by
    // ImageLoaderConfiguration.createDefault(this); method.
    private void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                //.writeDebugLogs() // Remove for release app
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    // Abstract method, that returns prepared to inflate Fragment
    protected abstract Fragment setFragment();

    // Inflate prepared to inflate Fragment
    protected void inflateFragment() {
        // Check if Fragment is already inflated
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment == null) {
            // Inflate newly prepared Fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, setFragment())
                    .commit();
        }
    }
}
