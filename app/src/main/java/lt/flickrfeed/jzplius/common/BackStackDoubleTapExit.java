package lt.flickrfeed.jzplius.common;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import lt.jzplius.flickrfeed.R;

/**
 * This class encapsulates method, which exits app on double times back pressed
 */
public class BackStackDoubleTapExit {
    // Member indicating whether "back pressed" button was pressed once
    private static boolean backToExitPressedOnce;

    public BackStackDoubleTapExit(Context context) {
        // If the button has been pressed twice go to the main screen of phone
        if (backToExitPressedOnce) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);

            return;
        }

        backToExitPressedOnce = true;
        Toast.makeText(context, context.getResources().getString(R.string.press_twice_to_exit_app),
                Toast.LENGTH_SHORT).show();

        // 2 seconds delay to reset back pressed counter
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                backToExitPressedOnce = false;
            }

        }, 2000);
    }
}
