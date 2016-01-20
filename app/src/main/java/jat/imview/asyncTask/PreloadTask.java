package jat.imview.asyncTask;

import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import jat.imview.ui.activity.SplashScreenActivity;

/**
 * Created by bulat on 06.12.15.
 */
public class PreloadTask extends AsyncTask<Long, Void, Void> {
    private WeakReference<SplashScreenActivity> activityWeakReference;


    public PreloadTask(SplashScreenActivity splashScreenActivity) {
        activityWeakReference = new WeakReference<>(splashScreenActivity);
    }

    @Override
    protected Void doInBackground(Long... params) {
        try {
            TimeUnit.MILLISECONDS.sleep(params[0]);
        } catch (InterruptedException ignored) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activityWeakReference.get().startMainActivity();
    }
}
