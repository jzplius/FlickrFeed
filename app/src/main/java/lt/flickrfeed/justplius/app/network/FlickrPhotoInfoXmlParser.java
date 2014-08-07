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

package lt.flickrfeed.justplius.app.network;

import android.util.Log;
import android.util.Xml;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import lt.flickrfeed.justplius.app.feed.FeedItem;

/**
 * This class parses XML feed from flickr. Given an InputStream representation
 * of a feed, it returns a List of FeedItems, where each list element represents
 * a single post in the XML feed.
 */

public class FlickrPhotoInfoXmlParser {

    private static String TAG = "PhotoActivity.java";

    private static final String ns = null; //nameservice

    // Instantiate XmlPullParser and start handling feed by it's "rsp" node
    public ArrayList<NameValuePair> parse(InputStream in) throws XmlPullParserException, IOException {
        try {

            // Construct XmlPullParser object
            XmlPullParser parser = Xml.newPullParser();
            // Configure parser to process namespaces
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            // Read first tag, to start usage of parser
            parser.nextTag();

            //process "feed" tag and it's inner nodes
            return readRsp(parser);

        } finally {
            in.close();
        }
    }

    // Handles "rsp" tag and his inner nodes.
    private ArrayList<NameValuePair> readRsp(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        ArrayList<NameValuePair> nvpList = new ArrayList<NameValuePair>();
        String valueContainer = "";

        parser.require(XmlPullParser.START_TAG, ns, "rsp");
        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Handle "photo" tag
            if (name.equals("photo")) {

                valueContainer = parser.getAttributeValue(null, "originalsecret");
                nvpList.add(new BasicNameValuePair("originalsecret", valueContainer));
                valueContainer = parser.getAttributeValue(null, "originalformat");
                nvpList.add(new BasicNameValuePair("originalformat", valueContainer));

            } else {
                skip(parser);
            }

        }

        return nvpList;
    }

    // Handles "photo" tag and it's attributes. Receives "originalsecret", "originalsecret"
    // string values, inserts them into in ArrayList<NameValuePair>. Returns ArrayList of strings.
    private ArrayList<NameValuePair> readPhoto(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "photo");

        String valueContainer = "";
        String name = "";
        ArrayList<NameValuePair> nvpList = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            name = parser.getName();

            // Handle "originalsecret", "originalformat" tags
            if (name.equals("photo")) {

                valueContainer = parser.getAttributeValue(null, "originalsecret");
                nvpList.add(new BasicNameValuePair("originalsecret", valueContainer));
                valueContainer = parser.getAttributeValue(null, "originalformat");
                nvpList.add(new BasicNameValuePair("originalformat", valueContainer));

            } else {
                skip(parser);
            }

        }

        // Returns  ArrayList<NameValuePair>
        return nvpList;
    }

    // Processes tag in the feed.
    private String readTag(XmlPullParser parser, String tag)
            throws IOException, XmlPullParserException {

        // Get text value of current tag
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);

        return title;

    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser, String tag, String rel,
                            String type) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, tag);

        // Retrieve current tag's name and attribute values
        // so that we could determine if it is the required tag
        String link = "";
        String innerTag = parser.getName();
        String innerRel = parser.getAttributeValue(null, "rel");
        String innerType = parser.getAttributeValue(null, "type");
        if (innerTag.equals(tag)) {
            if (innerRel.equals(rel) && innerType.equals(type)) {
                link = parser.getAttributeValue(null, "href");
            }
        }

        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, tag);

        return link;

    }

    // Extracts current tag's text value.
    private String readText(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {

            result = parser.getText();
            parser.nextTag();

        }
        return result;

    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
