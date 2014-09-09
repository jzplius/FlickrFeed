package lt.flickrfeed.jzplius.feed_photo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import lt.flickrfeed.jzplius.BaseFlickrFeedActivity;
import lt.flickrfeed.jzplius.common.NetworkState;
import lt.jzplius.flickrfeed.R;

/**
 * If there is intent EXTRA_URL_STRING provided, activity reads it's value and bypasses it
 * to fragment. Otherwise activity automatically closes.
 */
public class PhotoActivity extends BaseFlickrFeedActivity {
    public static final String EXTRA_URL_PHOTO = "lt.flickrfeed.jzplius.url_photo";
    public static final String EXTRA_URL_PHOTO_NO_SECRET = "lt.flickrfeed.jzplius.url_photo_no_secret";
    public static final String EXTRA_PHOTO_ID = "lt.flickrfeed.jzplius.photo_id";
    public static final String EXTRA_URL_PHOTO_LARGE = "lt.flickrfeed.jzplius.url_photo_large";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        // If there is no EXTRAS given, then return to previous activity
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
        } else if (NetworkState.sIsConnected) {
            inflateFragment();
        }
    }

    @Override
    protected Fragment setFragment() {
        // Pass EXTRAS as a bundle parameters to fragment
        Bundle extras = getIntent().getExtras();
        String photoUrl = extras.getString(EXTRA_URL_PHOTO, "https://www.flickr.com/images/buddyicon.gif");
        String photoId = extras.getString(EXTRA_PHOTO_ID);
        String photoUrlNoSecret = extras.getString(EXTRA_URL_PHOTO_NO_SECRET);
        String photoUrlLarge = extras.getString(EXTRA_URL_PHOTO_LARGE);

        return PhotoFragment.newInstance(photoUrl, photoId, photoUrlNoSecret, photoUrlLarge);
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
}
