package lt.flickrfeed.jzplius.common;

import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;

/**
 * This class provides base activity for subclasses. It controls network
 * connectivity state.
 */
public class BaseActivity extends FragmentActivity {
    // Receiver for handling connectivity state changes
    private ConnectionChangeReceiver mReceiver = ConnectionChangeReceiver.getInstance();

    @Override
    public void onResume() {
        super.onResume();
        // Register connectivity state receiver
        registerReceiver(mReceiver,
                new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister connectivity state receiver
        unregisterReceiver(mReceiver);
    }
}
