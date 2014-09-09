package lt.flickrfeed.jzplius.common;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import lt.jzplius.flickrfeed.R;

/**
 * This activity shows a view that reminds to turn on internet connection.
 * If internet is present user pushes button to return to previous activity.
 */
public class NetworkUnavailableActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_unavailable);

        // Get instances of view objects
        Button buttonCheckConnection = (Button) findViewById(R.id.button_check_connection);

        // Static variable used to avoid simultaneous invoking of
        // NetworkUnavailable Activity from different fragments.
        NetworkState.sIsConnectionBeingHandled = true;
        // Handle responses to events on view objects
        buttonCheckConnection.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Return to previous activity if internet connection is present
                if (NetworkState.sIsConnected) {
                    NetworkState.sIsConnectionBeingHandled = false;
                    finish();
                }
            }

        });
    }

    // Disable on back button pressed event by default
    // and exit from app on back button pressed twice
    @Override
    public void onBackPressed() {
        // If the button has been pressed twice go to the main screen of phone
        new BackStackDoubleTapExit(this);
    }
}
