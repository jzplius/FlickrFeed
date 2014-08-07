package lt.flickrfeed.justplius.app.imports;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is a static class for usage in these situations:
 * <p/>
 * - isNetworkAvailable(): performing a runtime network check on main
 * thread indicating whether phone is connected or not
 * <p/>
 * - hasActiveInternetConnection(): additionally checks if network
 * connection is active
 * <p/>
 * - CheckIfDeviceIsConnectedTask(): runs hasActiveInternetConnection()
 * in asynchronous task
 * <p/>
 * - handleIfNoNetworkAvailable(): invokes NetworkUnavailableActivity if
 * network is not present at runtime
 */

public class NetworkState {

    private static String TAG = "NetworkState,java: ";

    // Objects used to determine network state
    private static CheckIfDeviceIsConnectedTask cidict;

    // Static members for usage of qualify
    public static boolean isConnected = true;
    public static boolean isConnectionBeingHandled = false;

    // Check if device has any available network connection
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;

    }

    // Check if device is connected to active internet connection
    public static boolean hasActiveInternetConnection(Context context) {

        if (isNetworkAvailable(context)) {

            Log.d(TAG, "Network available");
            try {

                //
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                Log.d(TAG, "Network is active");

                return (urlc.getResponseCode() == 200);

            } catch (IOException e) {
                Log.e(TAG, "Network is inactive", e);
            }

        } else {

            Log.d(TAG, "No network available");

        }

        return false;
    }

    // Connection state change handler asynchronous task
    private static class CheckIfDeviceIsConnectedTask extends AsyncTask<Context, Void, Boolean> {

        private Context context;

        @Override
        protected Boolean doInBackground(Context... params) {

            context = params[0];

            return hasActiveInternetConnection(context);
        }

        //save connection status state on static member
        protected void onPostExecute(Boolean result) {
            isConnected = result;
        }

    }

    // Convenience method checking if device is connected to
    // internet. It creates and executes a single network test
    // asynchronous task, based on it's context
    public static void checkIfDeviceIsConnected(Context context) {

        //check is committed in asynchronous task
        cidict = new CheckIfDeviceIsConnectedTask();
        //execute asynchronous task
        cidict.execute(context);
    }

    // Method responsible for invoking NetworkUnavailableActivity if
    // network is not present at runtime
    public static void handleIfNoNetworkAvailable(Context context) {

        // Static variable for avoiding simultaneous invoking of
        // NetworkUnavailableActivity from different fragments
        // while inflating few of them at start
        if (!isConnectionBeingHandled) {

            // Determine if internet connection is available
            isConnected = isNetworkAvailable(context);
            if (!isConnected) {

                // Global variable for avoiding simultaneous invoking of
                // NetworkUnavailable Activity from different fragments
                // while inflating few of them at start
                isConnectionBeingHandled = true;

                // Start new activity when internet connection is not present
                Intent intent = new Intent(context.getApplicationContext(), NetworkUnavailableActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }

        }
    }

}
