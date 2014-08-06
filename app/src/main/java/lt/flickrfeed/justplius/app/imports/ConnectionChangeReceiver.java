package lt.flickrfeed.justplius.app.imports;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

/**
 * This class responds to internet state changes. When informed about state change it
 * asynchronously executes checkIfDeviceIsConnected(), to check devices' new state. *
 * CcheckIfDeviceIsConnected determines network state changes, such as "Network available",
 * "Network is active", "Network is inactive", "No network available".
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    public static ConnectionChangeReceiver ccr = new ConnectionChangeReceiver();

    public static ConnectionChangeReceiver getInstance() {
        return ccr;
    }

    // Handle the network state change event by asynchronously executing checkIfDeviceIsConnected()
    @Override
    public void onReceive (Context context, Intent intent){

        NetworkState.checkIfDeviceIsConnected(context);

    }

    // Register receiver in onResume() activity callback
    public void registerBroadcastReceiver(Context context) {

        context.registerReceiver(ccr, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

    }

    // Unregister receiver in onPause() activity callback
    public void unregisterBroadcastReceiver(Context context) {

        context.unregisterReceiver(ccr);

    }


}
