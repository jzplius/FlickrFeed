package lt.flickrfeed.jzplius.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class responds to internet state changes. When informed about state change it
 * asynchronously executes checkIfDeviceIsConnected(), to check devices' new state. *
 * checkIfDeviceIsConnected determines network state changes, such as "Network available",
 * "Network is active", "Network is inactive", "No network available".
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
    // Receiver handling internet connection change events
    private static ConnectionChangeReceiver mReceiver;

    public static ConnectionChangeReceiver getInstance() {
        if (mReceiver == null) {
            mReceiver = new ConnectionChangeReceiver();
        }
        return mReceiver;
    }

    public ConnectionChangeReceiver() {
    }

    // Handle the network state change event by asynchronously executing checkIfDeviceIsConnected()
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkState.checkIfDeviceIsConnected(context);
    }
}
