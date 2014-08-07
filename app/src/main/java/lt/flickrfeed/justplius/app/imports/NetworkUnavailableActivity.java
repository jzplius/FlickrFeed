package lt.flickrfeed.justplius.app.imports;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import lt.justplius.flickrfeed.R;

/**
 * This activity shows a view that reminds to turn on internet connection.
 * If internet is present user pushes button to return to previous activity.
 */

public class NetworkUnavailableActivity extends Activity {

    private Button buttonCheckConnection;

    protected ConnectionChangeReceiver ccr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Static variable used to avoid simultaneous invoking of
        // NetworkUnavailable Activity from different fragments.
        NetworkState.isConnectionBeingHandled = true;

        setContentView(R.layout.activity_network_unavailable);

        // Get instances of view objects
        buttonCheckConnection = (Button) findViewById(R.id.button_check_connection);

        // Handle responses to events on view objects
        buttonCheckConnection.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Return to previous activity if internet connection is present
                if (NetworkState.isConnected) {

                    NetworkState.isConnectionBeingHandled = false;
                    finish();
                }

            }

        });

        // Register connection change receiver
        ccr = ConnectionChangeReceiver.getInstance();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register receiver in onResume() activity callback
        ccr.registerBroadcastReceiver(getApplicationContext());

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister receiver in onPause() activity callback
        ccr.unregisterBroadcastReceiver(getApplicationContext());

    }

    // Disable on back button pressed event by default
    // and exit from app on back button pressed twice
    @Override
    public void onBackPressed() {

        // If the button has been pressed twice go to the main screen of phone
        new BackStackDoubleTapExit(getApplicationContext());

    }
}
