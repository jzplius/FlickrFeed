package lt.flickrfeed.jzplius.feed_item;

/**
 * This model class encapsulates feed items, red from flickr feed. Valuable contents:
 * - PhotoSize enum: pairs available sizes strings with letters, that will be used
 * on photo access on flickr server.
 * - contains getters and setters for FeedItem
 * - getPhotoUrl(int): method returns string URL of desired size Photo in flickr server
 */
public class FeedItem {
    /** Pair available photo size's with server request letters
    s small square 75x75
    q large square 150x150
    t thumbnail, 100 on longest side
    m small, 240 on longest side
    n small, 320 on longest side-medium, 500 on longest side
    z medium 640, 640 on longest side
    c medium 800, 800 on longest side
    b large, 1024 on longest side
    // o original image, either a jpg, gif or png, depending on source format
    // Original image is downloaded on FeedItems list click, after retrieving additional data
    // about selected photo, as accessing it does not completes with simple postfix letter addition.
    */
    public enum PhotoSize {
        SQUARE_75(75), SQUARE_150(150), THUMBNAIL_100(100), SMALL_240(240),
        SMALL_500(500), MEDIUM_640(640), MEDIUM_800(800), LARGE_1024(1024);

        private char letter; // Postfix letter added to the end of photo URL

        // Pair available sizes strings with letter
        PhotoSize(int size) {
            switch (size) {
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
                case 1024:
                    letter = 'b';
                    break;
                default:
                    letter = 'n';
                    break;
            }
        }

        // Return photo URL with size postfix letter
        public String getPhotoUrlWithLetter(String url) {
            return url + "_" + letter;
        }
    }
    // User's information
    private String mUserIconUrl;
    private String mUserName;
    // Photo's information
    private String mPhotoUrl;
    private String mPhotoUrlNoSecret;
    private String mPhotoTitle;
    private String mPhotoId;

    public FeedItem(String userIcon, String userName, String photoUrl, String photoTitle) {
        setUserIconUrl(userIcon);
        setUserName(userName);
        setPhotoUrl(photoUrl);
        setPhotoTitle(photoTitle);
        setPhotoId();
    }

    /** Get URL of image with desired size
     * parameter of PhotoSize's enum
     * i.e. getPhotoUrl(PhotoSize.SQUARE_75)
     * would return square 75x75 px photo's URL
     */
    public String getPhotoUrl(PhotoSize size) {
        int number = size.ordinal();
        if (number < 0 || number > 7)
            return null;

        // Get URL without postfix
        String urlString = getPhotoUrl();
        // Get URL with postfix
        urlString = PhotoSize.values()[number].getPhotoUrlWithLetter(urlString);
        // Add jpg file extension
        urlString += ".jpg";

        return urlString;
    }

    // Getters and setters

    public String getUserIconUrl() {
        // Trim the additional parameters of string URL
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

    // Return URL without file extension
    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    // Save URL without file extension and '_b' postfix
    public void setPhotoUrl(String photoUrl) {
        // Trim file extension and '_b' size postfix
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

    public String getPhotoId() {
        return mPhotoId;
    }

    public String getPhotoUrlNoSecret() {
        return mPhotoUrlNoSecret;
    }

    // Get photo's id by trimming photo URL
    public void setPhotoId() {
        // Trim mPhotoUrl secret key followed by '_' postfix
        if (mPhotoUrl.indexOf("_") > 0)
            mPhotoUrlNoSecret = mPhotoUrl.substring(0, mPhotoUrl.lastIndexOf("_"));

        // Get id of photo by returning value after last '/' occurrence
        if (mPhotoUrlNoSecret.lastIndexOf("/") > 0)
            mPhotoId = mPhotoUrlNoSecret.substring(mPhotoUrlNoSecret.lastIndexOf("/") + 1);
    }
}
