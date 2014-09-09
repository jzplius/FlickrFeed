/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package lt.flickrfeed.jzplius.feed_items_list;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import lt.flickrfeed.jzplius.common.BaseXmlParser;
import lt.flickrfeed.jzplius.feed_item.FeedItem;

/**
 * This class parses XML feed from flickr. Given an InputStream representation
 * of a feed, it returns a List of FeedItems, where each list element represents
 * a single post in the XML feed.
 */
public class FlickrFeedXmlParser extends BaseXmlParser<NameValuePair, ArrayList<FeedItem>> {

    public FlickrFeedXmlParser(String initialTag) {
        super(initialTag);
    }

    /**
     * In subclasses override this method to provide conversion from ArrayList<T>
     * to needed V object
     *
     * @param listFrom - ArrayList<T> to be converted to other type objects
     */
    @Override
    protected ArrayList<FeedItem> changeToObject(ArrayList<NameValuePair> listFrom) {
        ArrayList<FeedItem> listTo = new ArrayList<>();
        String userIcon = "";
        String userName = "";
        String photoUrl = "";
        String photoTitle = "";
        // Counter for currently being handled FeedItem's part
        int counter = 0;

        // Step through all items of ArrayList<NameValuePair> and construct ArrayList<FeedItems>
        for (NameValuePair item : listFrom) {
            counter++;

            // Every four items contain values for FeedItem
            switch (counter) {
                case 1:
                    photoTitle = item.getValue();
                    break;
                case 2:
                    userName = item.getValue();
                    break;
                case 3:
                    userIcon = item.getValue();
                    break;
                case 4:
                    photoUrl = item.getValue();
                    break;
            }

            if (counter == 4) {
                listTo.add(new FeedItem(userIcon, userName, photoUrl, photoTitle));
                counter = 0;
            }
        }

        return listTo;
    }

    /**
     * In subclasses override this method to provide specific
     * actions based on each tag
     *
     * @param parser - XMLPullParser
     * @param tag - tag to be handled
     */
    @Override
    protected ArrayList<NameValuePair> handleTag(XmlPullParser parser, String tag)
            throws IOException, XmlPullParserException {
        ArrayList<NameValuePair> list = new ArrayList<>();
        String tagName = parser.getName();

        switch (tag) {
            // Handles "feed" tag and his inner nodes.
            case "feed":
                // Handle "entry" tag
                switch (tagName) {
                    case "entry":
                        list.addAll(parseTag(parser, "entry"));
                        break;
                    default:
                        skip(parser);
                        break;
                }
                break;

            // Handles "entry" tag and it's inner nodes. Receives "title", "link", "name",
            // "buddyicon" string representations packed in ArrayList<NameValuePair>
            case "entry":
                // Handle "title", "link", "name", "buddyicon" tags
                switch (tagName) {
                    case "author":
                        list.addAll(parseTag(parser, "author"));
                        break;
                    case "title":
                        list.add(new BasicNameValuePair("title", readTag(parser, "title")));
                        break;
                    case "link":
                        String link = readLink(parser, "link", "enclosure", "image/jpeg");
                        if (!link.equals("")) {
                            list.add(new BasicNameValuePair("photo", link));
                        }
                        break;
                    default:
                        skip(parser);
                        break;
                }
                break;

            // Handles "author" tag and it's inner nodes, thus receiving and returning
            // "name", "buddyicon" string representations packed in ArrayList<NameValuePair>
            case "author":
                // Handle "name", "buddyicon" tags and pack them into ArrayList<NameValuePair>
                switch (tagName) {
                    case "name":
                        list.add(new BasicNameValuePair("name", readTag(parser, "name")));
                        break;
                    case "buddyicon":
                        list.add(new BasicNameValuePair("buddyicon", readTag(parser, "buddyicon")));
                        break;
                    default:
                        skip(parser);
                        break;
                }
                break;
        }

        return list;
    }
}
