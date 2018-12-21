package world.best.musicplayer.activity;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

public class ManualTagDetailActivity extends AppCompatActivity {

    /** Called when the activity is first created. */

    ListView myListView;
    Cursor mTagUniqueCursor;
    String[] proj;
    protected Activity mActivity = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.tag_home_page);
        //This cursor should be used to polpulate the UI implementation.
    }
}