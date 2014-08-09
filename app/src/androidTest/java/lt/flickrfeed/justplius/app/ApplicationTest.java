package lt.flickrfeed.justplius.app;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.apps.common.testing.ui.espresso.Espresso;
import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;

import org.hamcrest.Matchers;

import java.util.regex.Matcher;

import lt.flickrfeed.justplius.app.feed.FeedItem;
import lt.flickrfeed.justplius.app.feed.FeedItemsListViewAdapter;
import lt.justplius.flickrfeed.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.pressKey;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeTextIntoFocusedView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.hasFocus;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.IsNot.not;

/**
 * These tests check for search bar and transparent background states
 * assurance to requirements. Checking visibility and focus states
 * while performing click() and keyPress() actions
 */
@LargeTest
public class ApplicationTest extends ActivityInstrumentationTestCase2<PhotoFeedActivity> {

    @SuppressWarnings("deprecation")
    public ApplicationTest() {

        // This constructor was deprecated - but we want to support lower API levels.
        super("com.google.android.apps.common.testing.ui.testapp", PhotoFeedActivity.class);

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Espresso will not launch our activity for us, we must launch it via getActivity().
        getActivity();
    }

    public void testViewTransparentBackground() {

        // On view load transparent background should not be visible
        onView(withId(R.id.ViewTransparentBackground))
                .check(matches(not(isDisplayed())));

    }

    public void testOnSearchBarFocusViewTransparentBackground() {

        // On search bar icon click, transparent background should be visible
       onView(withId(R.id.action_search))
                .perform(click());
        onView(withId(R.id.ViewTransparentBackground))
                .check(matches(isDisplayed()));

    }

    public void testOnSearchQuerySubmitted() {

        // On search query entered and ENTER presses
        // search bar should be without user focus,
        // so that search results would be shown and no keyboard
        // would take up screen space
        onView(withId(R.id.action_search))
                .perform(click())
                .perform(typeTextIntoFocusedView("coffee"))
                .perform(pressKey(KeyEvent.KEYCODE_ENTER))
                .check(matches(not(hasFocus())));

    }

}
