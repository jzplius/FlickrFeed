package lt.flickrfeed.jzplius.common;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class provides functionality to download response from server and parses that response
 * using abstract XML parser T, which must extend BaseXmlParser. It returns parsed XML content in
 * required V type object.
 */
public abstract class DownloadAndParseXML<T extends BaseXmlParser, V>{

    /**
     * In subclasses override this method to provide specific
     * XMLPullParser, that extends BaseXmlParser
     */
    protected abstract T setParser();

    // Downloads XML from Flickr feed, parses it and returns list of FeedItems
    public V loadXmlFromNetwork(String urlString)
            throws XmlPullParserException, IOException {
        // Parser for XML content
        T xmlParser = setParser();
        V result = null;
        InputStream stream = null;

        try {
            // Download feed from given URL string
            stream = downloadUrl(urlString);
            // Parse given stream and return list of FeedItems
            result = (V) xmlParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        // XmlParser returns a List (called "FeedItems") of FeedItem objects.
        // Each FeedItem object represents a single post in the XML feed.
        return result;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    public InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(5000);
        conn.setConnectTimeout(7500);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();

        return conn.getInputStream();
    }
}
