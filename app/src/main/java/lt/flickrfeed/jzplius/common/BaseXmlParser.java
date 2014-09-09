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

package lt.flickrfeed.jzplius.common;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * This abstract class provides functionality for XML feed handling. Given an
 * InputStream representation of a feed, it returns ArrayList<T>.
 * Specific actions for each XML tag are handled in handleTag().
 * Returned ArrayList<T> needs to be converted to specific object V, which is the one
 * that you require in changeToObject().
 */
public abstract class BaseXmlParser<T, V>{
    /**
     * In subclasses override this method to provide specific
     * actions to handle each tag
     *
     * @param parser - XMLPullParser
     * @param tag - tag to be handled
     */
    protected abstract ArrayList<T> handleTag(XmlPullParser parser, String tag)
            throws IOException, XmlPullParserException;

    /**
     * In subclasses override this method to provide conversion from ArrayList<T>
     * to needed V object
     * @param listFrom - ArrayList<T> to be converted to other type objects
     */
    protected abstract V changeToObject(ArrayList<T> listFrom);

    private String mInitialTag;

    public BaseXmlParser (String initialTag) {
        mInitialTag = initialTag;
    }

    // Instantiate XmlPullParser and start handling initial tag
    public V parse(InputStream in)
            throws XmlPullParserException, IOException {
        try {
            // Construct XmlPullParser object
            XmlPullParser parser = Xml.newPullParser();
            // Configure parser to process namespaces
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            // Read first tag, to start usage of parser
            parser.nextTag();

            // Process initial tag and it's inner nodes
            ArrayList<T> listFrom = parseTag(parser, mInitialTag);
            return changeToObject(listFrom);
        } finally {
            in.close();
        }
    }

    // Parses tag and his inner nodes.
    public ArrayList<T> parseTag(XmlPullParser parser, String tag)
            throws XmlPullParserException, IOException {
        ArrayList<T> list = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, tag);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            /**
             * In subclasses override this method to provide specific
             * actions based on each tag
             */
            ArrayList<T> tagList = handleTag(parser, tag);
            if (tagList.size() > 0) {
                list.addAll(tagList);
            }
        }
        return list;
    }

    // Processes and reads given nodes text
    protected String readTag(XmlPullParser parser, String tag)
            throws IOException, XmlPullParserException {
        // Get text value of current tag
        parser.require(XmlPullParser.START_TAG, null, tag);
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, null, tag);

        return result;
    }

    // Processes link tags
    protected String readLink(XmlPullParser parser, String tag, String rel,
                            String type) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        // Retrieve current tag's name and attributes values
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
        parser.require(XmlPullParser.END_TAG, null, tag);

        return link;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    protected void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
