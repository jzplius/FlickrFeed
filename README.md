Functionalities based on screens:
=========

* Feed items list. Shows list of Flickr feeds grouped in blocks of 4 feeds.  When one feed item is picked it displays original-sized photo of feed.
     * A stylized search bar lets submit tags, on which to filter feed by.
* Feed photo. On item in list clicked original-sized photo is displayed. Photo can be zoomed by crossing and double tapping. If original-sized photo is too large to display (larger than 4000x3200px), up to 1024px size photo is downloaded and shown instead.

Technical features:
=========

* Focused on high reusability of code:
     * implemented abstract XML parser with templates;
     * implemented abstract URL response download and parse with XML parser template;
     * created base activity to be extended.
* Created 4 different dimensions sets for: default qualifiers, large phones, 1200 dp width-sized ones and xhdpi density-sized screens.
* Data is taken from Flickr API and default feed.
* Checks network connection at start and at runtime.
    * If no network is available then new activity starts, which waits till network connection is active.
     * Network state changes are received by BroadcastReceiver and app knows if connection is active at run-time.
     * Connection state is being checked while attaching all fragments.
* Allows screen rotation on all screens.
* The size of desired photo resolution is selected by using ENUM constants.
* Images are being downloaded and displayed using "Android Universar Image Loader" library.
* The feed is "never ending", when you scroll to end it downloads a new bunch of latest data based on same URL response.
* There font is set to a specific Roboto typeface.

Tested on:
=========
* The app is tested on physical devices: LG L7 II phone, Galaxy Nexus 7 (2013) tablet and in a bunch of other AVD devices. 
* Added automated Espresso UI tests, which simulate simple app usage scenarios from user perspective (to use them on Android Studio edit configurations in project and add Android Test, which runs in "All in module" and is set to run with specific instrumentation runner: GoogleInstrumentationTestRunner).

