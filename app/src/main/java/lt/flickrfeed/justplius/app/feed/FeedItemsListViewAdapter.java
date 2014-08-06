package lt.flickrfeed.justplius.app.feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
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

import lt.flickrfeed.justplius.app.PhotoActivity;
import lt.flickrfeed.justplius.app.PhotoFeedActivity;
import lt.flickrfeed.justplius.app.imports.NetworkState;
import lt.justplius.flickrfeed.R;

/**
 * This adapter links single ListView item with 4 FeedItems. Adapter returns single 
 * ListView item (grid_layout_block_of_feeds.xml) populated with 4 feeds information.
 * One ListView item (grid_layout_block_of_feeds.xml) consists of block of 4 feeds.
 * ListView item is returned on getView(int, View, ViewGroup) method
 */

public class FeedItemsListViewAdapter extends ArrayAdapter<FeedItem> {

		private static String TAG = "FeedItemsListViewAdapter.java";
 
		// Handles one images' loading complete event
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		private ImageLoader imageLoader;
		// Contains specified image rendering options
		private DisplayImageOptions optionsNormal;    
        private DisplayImageOptions optionsRounded;  
		
		// Contains all FeedItems
        private ArrayList<FeedItem> allItems;
        private LayoutInflater inflater;

        private Context context;
        

        // Special fonts for TextViews
        private Typeface tfRobotoMedium;
        private Typeface tfRobotoRegular;
        
        
        public FeedItemsListViewAdapter(Context _context, ArrayList<FeedItem> _allItems, Typeface _tfRobotoMedium, Typeface _tfRobotoRegular) {
            
        	super(_context, R.layout.block_of_feeds);
    
        	// Instantiate objects
            this.allItems = _allItems;
            this.inflater = LayoutInflater.from(_context);
            context = _context;

            imageLoader = ((PhotoFeedActivity) _context).getImageLoader();

            // Set font style
            tfRobotoMedium = _tfRobotoMedium;
            tfRobotoRegular = _tfRobotoRegular;
            
            // Set image rendering option to required ones
            optionsNormal = new DisplayImageOptions.Builder()
        	.delayBeforeLoading(100)
    		.showImageOnLoading(R.drawable.background_image)
    		.showImageOnFail(R.drawable.buddyicon)
    		.cacheInMemory(true)
    		.cacheOnDisk(true)
    		.considerExifParams(true)
    		.build();
            
            // Set image rendering option to required ones
            optionsRounded = new DisplayImageOptions.Builder()
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

            String urlIcon;

            final ViewHolder holder;
            int type = getItemViewType(position);     
            // Feed items' real position in ListView
            int readPosition;
            
            if (convertView == null) {
            	
                holder = new ViewHolder();
                
                switch (type) {
                case 1:
                    
                	// Inflate a "grid_layout_block_of_feeds.xml" layout into ListView's item
                    convertView = inflater.inflate(R.layout.grid_layout_block_of_feeds,parent, false);
               
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
                    
                }

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
            urlIcon = allItems.get(readPosition).getUserIconUrl();
	        imageLoader.displayImage(urlIcon, holder.imageViewIcon, optionsRounded, animateFirstListener);
            handleFeed(readPosition++, holder.imageView1, holder.textViewTitle1, holder.textViewAuthor1);

            // Pair ListView item with FeedItem
            handleFeed(readPosition++, holder.imageView2, holder.textViewTitle2, holder.textViewAuthor2);
            
            // Pair ListView item with FeedItem
            handleFeed(readPosition++, holder.imageView3, holder.textViewTitle3, holder.textViewAuthor3);
            
            // Pair ListView item with FeedItem
            handleFeed(readPosition, holder.imageView4, holder.textViewTitle4, holder.textViewAuthor4);

            return convertView;
        }

        // Static views for each row
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
        public void handleFeed (final int position, ImageView imageView, TextView textViewTitle, TextView textViewAuthor) {

            String urlPhoto;

            // Get string URL of photo, which indicates to desired size photo
            // (medium up to 640 px on longest side) in Flick server
            urlPhoto = allItems.get(position).getPhotoUrl(FeedItem.PhotoSize.SMALL_500.ordinal());
            // Load image with specified urlPhoto URL, with specified image options
            imageLoader.displayImage(urlPhoto, imageView, optionsNormal, animateFirstListener);

            final String finalUrlPhoto = urlPhoto;
            final String finalPhotoId = allItems.get(position).getPhotoId();
            final String finalPhotoUrlNoSecret = allItems.get(position).getPhotoUrlNoSecret();
            // Set a URL of large photo, which is supposed to be downloaded in case if original
            // photo is too large to display
            final String finalPhotoUrlLarge = allItems.get(position).getPhotoUrl(FeedItem.PhotoSize.LARGE_1024.ordinal());

            // Set on view item click event, which opens new activity with original-size photo
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (NetworkState.isNetworkAvailable(context)) {

                        // Passing URL and photo ID parameters to fragment
                        Intent intent = PhotoActivity.newInstance(context, finalUrlPhoto, finalPhotoId,
                            finalPhotoUrlNoSecret, finalPhotoUrlLarge);

                        context.startActivity(intent);

                    } else {

                        //handle internet connection, if no network available
                        NetworkState.handleIfNoNetworkAvailable(context);

                    }

                }
            });

            textViewTitle.setText(allItems.get(position).getPhotoTitle());
            textViewTitle.setTypeface(tfRobotoMedium);
            textViewAuthor.setText(allItems.get(position).getUserName());
            textViewAuthor.setTypeface(tfRobotoRegular);

        }
        
        // Handles one images' loading complete event, by animating it
        private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

    		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

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
        
        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
        
        @Override
        public int getCount() {
            return allItems.size() / 4;
        }
        
        @Override
        public FeedItem getItem(int position) {
            return allItems.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public int getPosition(FeedItem item) {
            return allItems.indexOf(item);
        }
        
        @Override
        public int getViewTypeCount() {
            return 1; //Number of types + 1 !!!!!!!!
        }
        
        @Override
        public int getItemViewType(int position) {
            return 1;
        }
        
}