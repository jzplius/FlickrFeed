package lt.flickrfeed.jzplius.feed_items_list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lt.flickrfeed.jzplius.feed_item.FeedItem;
import lt.flickrfeed.jzplius.feed_photo.PhotoActivity;
import lt.jzplius.flickrfeed.R;

/**
 * This adapter links single ListView item with 4 FeedItems. Adapter returns single
 * ListView item (grid_layout_block_of_feeds.xml) populated with 4 feeds information.
 * One ListView item (grid_layout_block_of_feeds.xml) consists of block of 4 feeds.
 * ListView item is returned on getView(int, View, ViewGroup) method
 */
public class FeedItemsListViewAdapter extends ArrayAdapter<FeedItem> {
    // Handles one images' loading complete event
    private ImageLoadingListener mAnimateDisplay = new AnimateDisplayListener();
    private ImageLoader mImageLoader;
    // Contains specified image rendering options
    private DisplayImageOptions mOptionsNormal;
    private DisplayImageOptions mOptionsRounded;

    // Contains all FeedItems
    private ArrayList<FeedItem> mItems;
    //private int mItemsCount;
    private Context mContext;

    // Special fonts for TextViews
    private Typeface mTfRobotoMedium;
    private Typeface mTfRobotoRegular;

    public FeedItemsListViewAdapter(Context context, ArrayList<FeedItem> items,
                                    Typeface tfRobotoMedium, Typeface tfRobotoRegular) {
        super(context, R.layout.grid_layout_block_of_feeds);

        mItems = items;
        mContext = context;
        // Set font style
        mTfRobotoMedium = tfRobotoMedium;
        mTfRobotoRegular = tfRobotoRegular;

        mImageLoader = ImageLoader.getInstance();
        // Set image rendering option to required ones
        mOptionsNormal = new DisplayImageOptions.Builder()
                .delayBeforeLoading(100)
                .showImageOnLoading(R.drawable.background_image)
                .showImageOnFail(R.drawable.buddyicon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        // Set image rendering option to required ones
        mOptionsRounded = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.buddyicon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(100))
                .build();
    }

    // Adapter returns single ListView item (grid_layout_block_of_feeds.xml) populated
    // with 4 feeds information. One ListView item  consists of block of 4 feeds
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // View holder for single ListView item
        final ViewHolder holder;
        // Feed items' real position in ListView
        int readPosition;

        // First time initialize ViewHolder, other times retrieve saved one
        if (convertView == null) {
            holder = new ViewHolder();

            // Inflate a "grid_layout_block_of_feeds.xml" layout into ListView's item
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.grid_layout_block_of_feeds, parent, false);

            // Retrieve references to views
            holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
            holder.textViewTitle1 = (TextView) convertView.findViewById(R.id.textViewTitle1);
            holder.textViewAuthor1 = (TextView) convertView.findViewById(R.id.textViewAuthor1);
            holder.imageViewIcon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
            holder.imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);
            holder.textViewTitle2 = (TextView) convertView.findViewById(R.id.textViewTitle2);
            holder.textViewAuthor2 = (TextView) convertView.findViewById(R.id.textViewAuthor2);
            holder.imageView3 = (ImageView) convertView.findViewById(R.id.imageView3);
            holder.textViewTitle3 = (TextView) convertView.findViewById(R.id.textViewTitle3);
            holder.textViewAuthor3 = (TextView) convertView.findViewById(R.id.textViewAuthor3);
            holder.imageView4 = (ImageView) convertView.findViewById(R.id.imageView4);
            holder.textViewTitle4 = (TextView) convertView.findViewById(R.id.textViewTitle4);
            holder.textViewAuthor4 = (TextView) convertView.findViewById(R.id.textViewAuthor4);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // As one ListView item contains 4 FeedItems, recalculate
        // read positions for indexing of FeedItems
        readPosition = position * 4;

        // Pair heading ListView item with FeedItem
        holder.imageViewIcon.setVisibility(View.VISIBLE);
        // First row, compared to others, has additional icon
        String urlIcon = mItems.get(readPosition).getUserIconUrl();
        mImageLoader.displayImage(urlIcon, holder.imageViewIcon, mOptionsRounded, mAnimateDisplay);
        handleFeed(readPosition++, holder.imageView1, holder.textViewTitle1, holder.textViewAuthor1);
        // Pair ListView item with FeedItem
        handleFeed(readPosition++, holder.imageView2, holder.textViewTitle2, holder.textViewAuthor2);
        // Pair ListView item with FeedItem
        handleFeed(readPosition++, holder.imageView3, holder.textViewTitle3, holder.textViewAuthor3);
        // Pair ListView item with FeedItem
        handleFeed(readPosition, holder.imageView4, holder.textViewTitle4, holder.textViewAuthor4);

        return convertView;
    }

    // Static ViewHolder for each row
    static class ViewHolder {
        ImageView imageView1;
        TextView textViewTitle1;
        TextView textViewAuthor1;
        ImageView imageViewIcon;
        ImageView imageView2;
        TextView textViewTitle2;
        TextView textViewAuthor2;
        ImageView imageView3;
        TextView textViewTitle3;
        TextView textViewAuthor3;
        ImageView imageView4;
        TextView textViewTitle4;
        TextView textViewAuthor4;
    }

    // Handles one feed
    public void handleFeed(int position, ImageView imageView
            , TextView textViewTitle, TextView textViewAuthor) {
        // Get string URL of photo, which indicates to desired size photo
        // (medium up to 640 px on longest side) in Flick server
        String urlPhoto = mItems.get(position).getPhotoUrl(FeedItem.PhotoSize.SMALL_500);
        // Load image with specified urlPhoto URL, with specified image options
        mImageLoader.displayImage(urlPhoto, imageView, mOptionsNormal, mAnimateDisplay);

        final String finalUrlPhoto = urlPhoto;
        final String finalPhotoId = mItems.get(position).getPhotoId();
        final String finalPhotoUrlNoSecret = mItems.get(position).getPhotoUrlNoSecret();
        // Set a URL of large photo, which is supposed to be downloaded in case if original
        // photo is too large to display
        final String finalPhotoUrlLarge = mItems.get(position)
                .getPhotoUrl(FeedItem.PhotoSize.LARGE_1024);

        // Set on view item click event, which opens new activity with original-size photo
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Passing URL and photo ID parameters to fragment
                Intent intent = PhotoActivity.newInstance(mContext, finalUrlPhoto, finalPhotoId,
                    finalPhotoUrlNoSecret, finalPhotoUrlLarge);
                mContext.startActivity(intent);
            }
        });
        textViewTitle.setText(mItems.get(position).getPhotoTitle());
        textViewTitle.setTypeface(mTfRobotoMedium);
        textViewAuthor.setText(mItems.get(position).getUserName());
        textViewAuthor.setTypeface(mTfRobotoRegular);
    }

    // Handles one images' loading complete event, by animating it
    private static class AnimateDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages
                = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    public ArrayList<FeedItem> getItems(){
        return mItems;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size() / 4;
    }

    @Override
    public FeedItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(FeedItem item) {
        return mItems.indexOf(item);
    }


}