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

package lt.flickrfeed.jzplius.feed_photo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import lt.flickrfeed.jzplius.common.BaseXmlParser;

/**
 * This class parses XML feed from flickr. Given an InputStream representation
 * of a feed, it returns a List of FeedItems, where each list element represents
 * a single post in the XML feed.
 */
public class FlickrPhotoInfoXmlParser extends BaseXmlParser<NameValuePair, String>{

    public FlickrPhotoInfoXmlParser(String initialTag) {
        super(initialTag);
    }

    /**
     * In subclasses override this method to provide conversion from ArrayList<T>
     * to needed V object
     *
     * @param listFrom - ArrayList<T> to be converted to other type objects
     */
    @Override
    protected String changeToObject(ArrayList<NameValuePair> listFrom) {

        if (listFrom.size() == 2) {
            String oSecret = "";
            String format = "";

            for (NameValuePair nvp : listFrom) {
                switch (nvp.getName()) {
                    case "originalsecret":
                        oSecret = nvp.getValue();
                        break;
                    case "originalformat":
                        format = nvp.getValue();
                        break;
                }
            }

            // Generate URL postfix, to be added to photo URL for original image download
            return "_" + oSecret + "_o." + format;
        } else {
            return null;
        }
    }

    /**
     * In subclasses override this method to provide specific
     * actions to handle each tag
     *
     * @param parser - XMLPullParser
     * @param tag    - tag to be handled
     */
    @Override
    protected ArrayList<NameValuePair> handleTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        ArrayList<NameValuePair> list = new ArrayList<>();
        String tagName = parser.getName();

        switch (tag) {
            // Handles "rsp" tag and his inner nodes.
            case "rsp":
                // Handles "photo" tag and it's attributes. Receives "originalsecret",
                // "originalsecret" string values, inserts them into in ArrayList<NameValuePair>.
                switch (tagName) {
                    case "photo":
                        list.add(new BasicNameValuePair(
                                "originalsecret", parser.getAttributeValue(null, "originalsecret")));
                        list.add(new BasicNameValuePair(
                                "originalformat", parser.getAttributeValue(null, "originalformat")));
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
