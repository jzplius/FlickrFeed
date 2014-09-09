package lt.flickrfeed.jzplius.feed_photo;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import lt.flickrfeed.jzplius.common.DownloadAndParseXML;
import lt.jzplius.flickrfeed.R;

/**
 * This fragment loads small quality photo. Then while using given EXTRAS
 * it downloads original size photo which is unlimited
 */
public class PhotoFragment extends Fragment {

    // The fragment initialization parameters
    public static final String ARGUMENT_URL_STRING = "url_string";
    public static final String ARGUMENT_PHOTO_ID = "url_id";
    public static final String ARGUMENT_URL_STRING_NO_SECRET = "url_string_no_secret";
    public static final String ARGUMENT_URL_STRING_LARGE_PHOTO = "url_string_large_photo";

    // Handles one images' loading complete event
    protected ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptionsNormal;
    private Drawable mImageDrawable;
    // View elements
    private ImageViewTouch mImageViewTouch;
    private ProgressBar mProgressBar;
    private String mUrlPhoto;
    private String mUrlPhotoNoSecret;
    private String mUrlPhotoLarge;
    private String mPhotoId;
    private boolean mIsOriginalPhotoDownloaded = false;
    private DownloadAndParseXML<FlickrPhotoInfoXmlParser, String> mDownloadPhotoInfo;

    /**
     * Use this method to create a new instance of
     * this fragment using the provided parameters.
     * returns a new instance of fragment PhotoFragment.
     */
    public static PhotoFragment newInstance(String urlPhoto, String id
            , String mUrlPhotoNoSecret, String mUrlPhotoLarge) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_URL_STRING, urlPhoto);
        args.putString(ARGUMENT_PHOTO_ID, id);
        args.putString(ARGUMENT_URL_STRING_NO_SECRET, mUrlPhotoNoSecret);
        args.putString(ARGUMENT_URL_STRING_LARGE_PHOTO, mUrlPhotoLarge);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve bundle args
        if (getArguments() != null) {
            mUrlPhoto = getArguments().getString(ARGUMENT_URL_STRING);
            mUrlPhotoNoSecret = getArguments().getString(ARGUMENT_URL_STRING_NO_SECRET);
            mPhotoId = getArguments().getString(ARGUMENT_PHOTO_ID);
            mUrlPhotoLarge = getArguments().getString(ARGUMENT_URL_STRING_LARGE_PHOTO);
        }

        // Set image rendering option to required ones
        mOptionsNormal = new DisplayImageOptions.Builder()
                .delayBeforeLoading(100)
                .showImageOnFail(R.drawable.buddyicon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        mDownloadPhotoInfo = new DownloadPhotoInfo();

        // Retain state while on configuration changes
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        mImageViewTouch = (ImageViewTouch) view.findViewById(R.id.imageView);
        mImageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBarPhotoLoad);

        // Shows loading progress while original photo is not downloaded
        if (!mIsOriginalPhotoDownloaded) {
            mProgressBar.setVisibility(View.VISIBLE);
            // Set photo for ImageView
            mImageLoader.displayImage(mUrlPhoto, mImageViewTouch, mOptionsNormal
                    , new AnimateFirstDisplayListener());

            // Execute asynchronous task to load original photo information
            String url = getString(R.string.url_get_flickr_photo_information) + mPhotoId;
            new GetPhotoInformationTask().execute(url);
        } else {
            // If photo is already downloaded simple add it to view
            mImageViewTouch.setImageDrawable(mImageDrawable);
        }

        return view;
    }

    // Inner class, that handles one image loading complete event, animates it's inflate
    private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // Set delay time to animate after photo has been downloaded
                    FadeInBitmapDisplayer.animate(imageView, 300);
                    displayedImages.add(imageUri);
                }
                mImageDrawable = mImageViewTouch.getDrawable();
            }
        }
    }

    // Inner class, that handles original-sized image loading complete event,
    // animates it's inflate
    private class AnimateSecondDisplayListener extends SimpleImageLoadingListener {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                // Inform about to huge original photo and download a smaller
                // version (up to 1024px on longest side)
                if (loadedImage.getWidth() >= 4000 || loadedImage.getHeight() >= 3200) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.photo_too_big_trying_to_load_smaller),
                            Toast.LENGTH_LONG).show();

                    // Repeat load of image with smaller resolution
                    mImageLoader.displayImage(mUrlPhotoLarge, mImageViewTouch, mOptionsNormal,
                            new AnimateSecondDisplayListener());

                    return;
                }

                mProgressBar.setVisibility(View.INVISIBLE);
                mIsOriginalPhotoDownloaded = true;
                mImageDrawable = mImageViewTouch.getDrawable();

                List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 100);
                    displayedImages.add(imageUri);
                }
                mImageDrawable = mImageViewTouch.getDrawable();
            }
        }
    }

    // Implementation of AsyncTask used to get information about photo.
    private class GetPhotoInformationTask extends AsyncTask<String, Void, String> {
        // Actions to be performed in background thread
        @Override
        protected String doInBackground(String... urls) {
            String TAG = "PhotoFragment.java";
            try {
                // From given URL string's XML parse and return list of NamaValuePairs
                return mDownloadPhotoInfo.loadXmlFromNetwork(urls[0]);
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
        protected void onPostExecute(String urlPostfix) {
            // Use list of retrieved FeedItems
            if (urlPostfix != null) {
                // Generate URL for original image download
                String url = mUrlPhotoNoSecret + urlPostfix;
                // Set photo for ImageView and let imageLoaded download it
                mImageLoader.displayImage(url, mImageViewTouch, mOptionsNormal,
                        new AnimateSecondDisplayListener());
            } else {
                // Inform that high quality photo is not present
                Toast.makeText(getActivity(),
                        getActivity().getResources().getString(R.string.photo_not_available), Toast.LENGTH_LONG).show();
            }
        }
    }

    // Downloads XML from Flickr feed, parses it and returns a
    // postfix of original photo's URL in String
    private class DownloadPhotoInfo extends DownloadAndParseXML<FlickrPhotoInfoXmlParser, String> {

        /**
         * In subclasses override this method to provide specific
         * XMLPullParser, that extends BaseXmlParser
         */
        @Override
        protected FlickrPhotoInfoXmlParser setParser() {
            return new FlickrPhotoInfoXmlParser("rsp");
        }
    }
}
