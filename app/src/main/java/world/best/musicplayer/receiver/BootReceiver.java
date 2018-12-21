package world.best.musicplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import world.best.musicplayer.MediaPlaybackService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, MediaPlaybackService.class));
        }
    }
}