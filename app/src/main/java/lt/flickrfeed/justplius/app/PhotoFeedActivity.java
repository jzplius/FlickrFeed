package lt.flickrfeed.justplius.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import lt.flickrfeed.justplius.app.imports.NetworkState;
import lt.justplius.flickrfeed.R;

/**
 * This activity inflates a PhotosListFragment, which handles most of the app job. It also
 * initializes and instantiates ImageLoader (a included library)
 */


public class PhotoFeedActivity extends FragmentActivity {
	
	private static String TAG = "PhotoFeedActivity.java";   
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		
		setContentView(R.layout.activity_photo_feed);
				
		// Register connection change receiver
        new NetworkState.ConnectionChangeReceiver();
        
        // Initialize image Loader for first time
        initImageLoader(getApplicationContext());
        
        // inflate new PhotosListFragment
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PhotosListFragment()).commit();
		}
	}
	
	// Returns instantiated imageLoader to child elements / adapters
	public ImageLoader getImageLoader(){
		return imageLoader;
	}
	
	@Override
    public void onStart() {
        super.onStart();
       
        //TODO loadPage(); 
    }
	
	@Override
    public void onDestroy() {
		
        super.onDestroy();        
        //TODO unregister network state receiver
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
		
}
