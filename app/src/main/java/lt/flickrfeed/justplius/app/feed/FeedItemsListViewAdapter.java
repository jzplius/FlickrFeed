package lt.flickrfeed.justplius.app.feed;

import android.content.Context;
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

import lt.flickrfeed.justplius.app.PhotoFeedActivity;
import lt.justplius.flickrfeed.R;

/**
 * This adapter links single ListView item with 4 FeedItems. Adapter returns single 
 * ListView item (grid_layout_block_of_feeds.xml) populated with 4 feeds informations.
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
        
        private String urlPhoto;
        private String urlIcon;    
        
        // Special fonts for TextViews
        private Typeface tfRobotoMedium;
        private Typeface tfRobotoRegular;
        
        
        public FeedItemsListViewAdapter(Context _context, ArrayList<FeedItem> _allItems, Typeface _tfRobotoMedium, Typeface _tfRobotoRegular) {
            
        	super(_context, R.layout.grid_layout_block_of_feeds);
    
        	// Instantiate objects
            this.allItems = _allItems;
            this.inflater = LayoutInflater.from(_context);
            
            imageLoader = ((PhotoFeedActivity) _context).getImageLoader();
            
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
        // with 4 feeds informations. One ListView item  consists of block of 4 feeds
		@Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            
        	final ViewHolder holder;
            int type = getItemViewType(position);     
            // Feed items' real position 
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
            
            // Pair ListView item with FeedItem
            urlPhoto = allItems.get(readPosition).getPhotoUrl(FeedItem.PhotoSize.MEDIUM_640.ordinal());
            Log.d("urlPhoto", urlPhoto);
            imageLoader.displayImage(urlPhoto, holder.imageView1, optionsNormal, animateFirstListener);            
            holder.textViewTitle1.setText(allItems.get(readPosition).getPhotoTitle()); 
            holder.textViewTitle1.setTypeface(tfRobotoMedium);
            holder.textViewAuthor1.setText(allItems.get(readPosition).getUserName());
            holder.textViewAuthor1.setTypeface(tfRobotoRegular);
            holder.imageViewIcon.setVisibility(View.VISIBLE);
            urlIcon = allItems.get(readPosition).getUserIconUrl();
	        imageLoader.displayImage(urlIcon, holder.imageViewIcon, optionsRounded, animateFirstListener);  
	        holder.position1 = readPosition;
            readPosition++;
            
            // Pair ListView item with FeedItem
	        urlPhoto = allItems.get(readPosition).getPhotoUrl(FeedItem.PhotoSize.MEDIUM_640.ordinal());
            imageLoader.displayImage(urlPhoto, holder.imageView2, optionsNormal, animateFirstListener);            
            holder.textViewTitle2.setText(allItems.get(readPosition).getPhotoTitle());    
            holder.textViewTitle2.setTypeface(tfRobotoMedium);
            holder.textViewAuthor2.setText(allItems.get(readPosition).getUserName());
            holder.textViewAuthor2.setTypeface(tfRobotoRegular);
            holder.position2 = readPosition;
            readPosition++;	
            
            // Pair ListView item with FeedItem
            urlPhoto = allItems.get(readPosition).getPhotoUrl(FeedItem.PhotoSize.MEDIUM_640.ordinal());
            imageLoader.displayImage(urlPhoto, holder.imageView3, optionsNormal, animateFirstListener);            
            holder.textViewTitle3.setText(allItems.get(readPosition).getPhotoTitle());   
            holder.textViewTitle3.setTypeface(tfRobotoMedium);
            holder.textViewAuthor3.setText(allItems.get(readPosition).getUserName());
            holder.textViewAuthor3.setTypeface(tfRobotoRegular);
            holder.position3 = readPosition;
            readPosition++;	
            
            // Pair ListView item with FeedItem
            urlPhoto = allItems.get(readPosition).getPhotoUrl(FeedItem.PhotoSize.MEDIUM_640.ordinal());
            imageLoader.displayImage(urlPhoto, holder.imageView4, optionsNormal, animateFirstListener);            
            holder.textViewTitle4.setText(allItems.get(readPosition).getPhotoTitle());      
            holder.textViewTitle4.setTypeface(tfRobotoMedium);
            holder.textViewAuthor4.setText(allItems.get(readPosition).getUserName());
            holder.textViewAuthor4.setTypeface(tfRobotoRegular);
            holder.position4 = readPosition;
            readPosition++;	
                        
            return convertView;
        }
		
        // Static views for each row
        static class ViewHolder {   
        	
        	ImageView imageView1;
            TextView textViewTitle1;
            TextView textViewAuthor1;
            int position1;
            ImageView imageViewIcon;
            ImageView imageView2;
            TextView textViewTitle2;
            TextView textViewAuthor2;
            int position2;
            ImageView imageView3;
            TextView textViewTitle3;
            TextView textViewAuthor3;
            int position3;
            ImageView imageView4;
            TextView textViewTitle4;
            TextView textViewAuthor4;
            int position4;
            
        }   
        
        // Handles one images' loading complete event
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