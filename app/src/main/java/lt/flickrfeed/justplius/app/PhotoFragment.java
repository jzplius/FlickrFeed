package lt.flickrfeed.justplius.app;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import lt.flickrfeed.justplius.app.feed.FeedItem;
import lt.flickrfeed.justplius.app.imports.NetworkState;
import lt.flickrfeed.justplius.app.network.FlickrPhotoInfoXmlParser;
import lt.justplius.flickrfeed.R;

/**
 * This fragment loads small quality photo. Then while using given EXTRAS
 * it downloads original size photo which is unlimited
 */

public class PhotoFragment extends Fragment {

    private static String TAG = "PhotoFragment.java";

    // The fragment initialization parameters
    public static final String ARGUMENT_URL_STRING = "url_string";
    public static final String ARGUMENT_PHOTO_ID = "url_id";
    public static final String ARGUMENT_URL_STRING_NO_SECRET = "url_string_no_secret";
    public static final String ARGUMENT_URL_STRING_LARGE_PHOTO = "url_string_large_photo";

    // Handles one images' loading complete event
    private ImageLoadingListener animateFirstListener;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions optionsNormal;
    private Drawable imageDrawable;

    // View elements
    private ImageViewTouch imageViewTouch;
    private ProgressBar progressBar;
    private String urlPhoto;
    private String urlPhotoNoSecret;
    private String urlPhotoLarge;
    private String photoId;

    private boolean isOriginalPhotoDownloaded = false;

    /**
     * Use this method to create a new instance of
     * this fragment using the provided parameters.
     * returns a new instance of fragment PhotoFragment.
     */
    public static PhotoFragment newInstance(String urlPhoto, String id, String urlPhotoNoSecret, String urlPhotoLarge) {

        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_URL_STRING, urlPhoto);
        args.putString(ARGUMENT_PHOTO_ID, id);
        args.putString(ARGUMENT_URL_STRING_NO_SECRET, urlPhotoNoSecret);
        args.putString(ARGUMENT_URL_STRING_LARGE_PHOTO, urlPhotoLarge);

        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle internet connection, if no network available
        NetworkState.handleIfNoNetworkAvailable(getActivity().getApplicationContext());

        // Retrieve bundle args
        if (getArguments() != null) {

            urlPhoto = getArguments().getString(ARGUMENT_URL_STRING);
            urlPhotoNoSecret = getArguments().getString(ARGUMENT_URL_STRING_NO_SECRET);
            photoId = getArguments().getString(ARGUMENT_PHOTO_ID);
            urlPhotoLarge = getArguments().getString(ARGUMENT_URL_STRING_LARGE_PHOTO);

        }

        // Set image rendering option to required ones
        optionsNormal = new DisplayImageOptions.Builder()
                .delayBeforeLoading(100)
                .showImageOnFail(R.drawable.buddyicon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        // Retain state while on configuration changes
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        imageViewTouch = (ImageViewTouch) view.findViewById(R.id.imageView);
        imageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarPhotoLoad);

        // Shows loading progress while original photo is not downloaded
        if (!isOriginalPhotoDownloaded) {

            progressBar.setVisibility(View.VISIBLE);
            // Set photo for ImageView
            imageLoader.displayImage(urlPhoto, imageViewTouch, optionsNormal, new AnimateFirstDisplayListener(progressBar));

            // Execute asynchronous task to load original photo information
            String url = getString(R.string.url_get_flickr_photo_information) + photoId;

            new GetPhotoInformationTask().execute(url);

        } else {

            // If photo is already downloaded simple add it to view
            imageViewTouch.setImageDrawable(imageDrawable);

        }

        return view;
    }

    // Inner class, that handles one image loading complete event, animates it's inflate
    private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        private ProgressBar progressBar;

        AnimateFirstDisplayListener(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

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
                imageDrawable = imageViewTouch.getDrawable();
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
                    imageLoader.displayImage(urlPhotoLarge, imageViewTouch, optionsNormal,
                            new AnimateSecondDisplayListener());

                    return;
                }

                progressBar.setVisibility(View.INVISIBLE);
                isOriginalPhotoDownloaded = true;
                imageDrawable = imageViewTouch.getDrawable();

                List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 100);
                    displayedImages.add(imageUri);
                }
                imageDrawable = imageViewTouch.getDrawable();
            }

        }
    }

    // Implementation of AsyncTask used to get information about photo.
    private class GetPhotoInformationTask extends AsyncTask<String, Void, ArrayList<NameValuePair>> {

        // Actions to be performed in background thread
        @Override
        protected ArrayList<NameValuePair> doInBackground(String... urls) {

            try {

                // From given URL string's XML parse and return list of NamaValuePairs
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
        protected void onPostExecute(ArrayList<NameValuePair> items) {

            // Use list of retrieved FeedItems
            if (items != null) {

                String oSecret = "";
                String format = "";

                for (NameValuePair nvp : items) {
                    switch (nvp.getName()) {
                        case "originalsecret":
                            oSecret = nvp.getValue();
                            break;
                        case "originalformat":
                            format = nvp.getValue();
                            break;
                    }
                }

                // Generate URL for original image download
                String url = urlPhotoNoSecret + "_" + oSecret + "_o." + format;

                // Set photo for ImageView and let imageLoaded download it
                imageLoader.displayImage(url, imageViewTouch, optionsNormal, new AnimateSecondDisplayListener());

            } else {

                // Inform that high quality photo is not present
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.photo_not_available), Toast.LENGTH_LONG).show();

            }
        }
    }

    // Downloads XML from flickr feed, parses it and returns list of FeedItems
    private ArrayList<NameValuePair> loadXmlFromNetwork(String urlString)
            throws XmlPullParserException, IOException {

        // Parser for XML content
        FlickrPhotoInfoXmlParser xmlParser = new FlickrPhotoInfoXmlParser();
        ArrayList<NameValuePair> items = new ArrayList<NameValuePair>();
        InputStream stream = null;

        try {

            // Download feed from given URL string
            stream = downloadUrl(urlString);
            // Parse given stream and return list of FeedItems
            items = xmlParser.parse(stream);

        } finally {

            if (stream != null) {
                stream.close();
            }

        }

        // XmlParser returns a List of NameValuePair objects.
        // Each NameValuePair object represents a portion of photo
        return items;
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

}
