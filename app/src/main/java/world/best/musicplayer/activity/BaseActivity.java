package world.best.musicplayer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.squareup.picasso.Picasso;

import world.best.musicplayer.MediaPlaybackServiceManager;
import world.best.musicplayer.MediaPlaybackServiceManager.ServiceToken;
import world.best.musicplayer.MusicApplication;

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {

    protected ServiceToken mToken;
    protected Picasso mPicasso;
    protected boolean mServiceConnected;

    protected boolean mPermissionGranted = false;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPicasso = ((MusicApplication) getApplicationContext()).getPicassoInstance();
        // verifyStoragePermissions(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyStoragePermissions(this);
    }

    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            mPermissionGranted = false;
            showNoPermissionView();
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                // We don't have permission so prompt the user
                showPermissionDialog(activity);
            }
        } else {
            mPermissionGranted = true;
            if (mToken == null) {
                mToken = MediaPlaybackServiceManager.bindToService(this, this);
            }
            loadViews();
        }
    }

    protected void showPermissionDialog(Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadViews();
                } else {
                    showNoPermissionView();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        MediaPlaybackServiceManager.unbindFromService(mToken);
        super.onDestroy();
    }

    /**
     * Unregister a receiver, but eat the exception that is thrown if the
     * receiver was never registered to begin with. This is a little easier
     * than keeping track of whether the receivers have actually been
     * registered by the time onDestroy() is called.
     */
    protected void unregisterReceiverSafe(BroadcastReceiver receiver) {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    protected abstract void showNoPermissionView();

    protected abstract void loadViews();
}