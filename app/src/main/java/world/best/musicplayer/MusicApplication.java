package world.best.musicplayer;

import android.app.Application;

import com.squareup.picasso.Picasso;

public class MusicApplication extends Application {

    private static Picasso mPicasso;

    @Override
    public void onCreate() {
        super.onCreate();
        mPicasso = Picasso.with(getApplicationContext());
    }

    public Picasso getPicassoInstance() {
        return mPicasso;
    }
}