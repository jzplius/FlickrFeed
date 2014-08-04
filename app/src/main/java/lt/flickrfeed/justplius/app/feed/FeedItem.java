package lt.flickrfeed.justplius.app.feed;

/**
 * This model class encapsulates feed items, red from flickr feed. Valuable contents:
 * - PhotoSize enum: pairs available sizes strings with letters, that will be used 
 * on photo access on flickr server. 
 * - contains getters and setters for FeedItem
 * - getPhotoUrl(int): method returns string URL of desired size Photo in flickr server
 */

public class FeedItem {
	
	/* pair available photo size's with server request letters
	s small square 75x75
	q large square 150x150
	t thumbnail, 100 on longest side
	m small, 240 on longest side
	n small, 320 on longest side-medium, 500 on longest side
	z medium 640, 640 on longest side
	c medium 800, 800 on longest side
	b large, 1024 on longest side
	o original image, either a jpg, gif or png, depending on source format
	*/
	public enum PhotoSize {
		
		SQUARE_75(75), SQUARE_150(150), THUMBNAIL_100(100), SMALL_240(240), 
		SMALL_500(500), MEDIUM_640(640), MEDIUM(800), LARGE(0);  
		  
		private char letter; //postfix letter added to the end of photo URL   
		
		//pair available sizes strings with letter 
		PhotoSize(int size) {  
			
			switch(size) {

			case 75:
				letter = 's';
				break;
			case 150:
				letter = 'q';
				break;
			case 100:
				letter = 't';
				break;	
			case 240:
				letter = 'm';
				break;
			case 500:
				letter = 'n';
				break;
			case 640:
				letter = 'z';
				break;
			case 800:
				letter = 'c';
				break;
			default:
				letter = 'o';
				break;
			}
			
		}  
		  
		// Return photo URL with size postfix letter
		public String getPhotoUrlWithLetter(String url) { 
			return url + "_" + letter; 
		}  
	}

	//user's information	
	private String mUserIconUrl;
	private String mUserName;
	
	//photo's information
	private String mPhotoUrl;
	private String mPhotoTitle;
	
	//constructor
	public FeedItem (String userIcon, String userName, String photoLarge, String photoTitle){
		
		setUserIconUrl(userIcon);
		setUserName(userName);		
		setPhotoUrl(photoLarge);
		setPhotoTitle(photoTitle);
		
	}
	
	/* Get URL of image with desired size 	  
	 * parameter is ordinal value of PhotoSize's enum
	 * i.e. getPhotoUrl(PhotoSize.SQUARE_75.ordinal()) 
	 * would return square 75x75 px photo's URL
	 */
		public String getPhotoUrl(int number) {
	
			String urlString = "";
			
			if (number < 0 || number > 7)
				return null;
			
			//get URL without postfix
			urlString = getPhotoUrl();
			//get URL with postfix
			urlString = PhotoSize.values()[number].getPhotoUrlWithLetter(urlString);
			//add jpg file extension
			urlString += ".jpg";
			
			return urlString;
			
		}
	
	//getters and setters	
		
	public String getUserIconUrl() {
			
		//trim the additional parameters of string URL
		if (mUserIconUrl.indexOf("?") > 0)
			mUserIconUrl = mUserIconUrl.substring(0, mUserIconUrl.lastIndexOf("?"));
		return mUserIconUrl;
	}
	
	public void setUserIconUrl(String userIconUrl) {
		mUserIconUrl = userIconUrl;
	}
	
	public String getUserName() {
		return "by " + mUserName;
	}
	
	public void setUserName(String userName) {
		mUserName = userName;
	}
	
	//return URL wothout file extention
	public String getPhotoUrl() {
		return mPhotoUrl;
	}
	
	//save URL without file extension and '_b' postfix
	public void setPhotoUrl(String photoUrl) {
		
		//trim file extension and '_b' size postfix
		if (photoUrl.indexOf("_b.") > 0)
			photoUrl = photoUrl.substring(0, photoUrl.lastIndexOf("_b."));
		
		mPhotoUrl = photoUrl;
	}

	public String getPhotoTitle() {
		return mPhotoTitle;
	}
	
	public void setPhotoTitle(String photoTitle) {
		mPhotoTitle = photoTitle;
	}
	
}
