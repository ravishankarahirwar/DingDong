package world.best.musicplayer.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;


public class TagsHomePageAdapter extends SimpleCursorAdapter implements SectionIndexer {

    AlphabetIndexer alphaIndexer;

    public TagsHomePageAdapter(Context context, int layout, Cursor c,
            String[] from, int[] to,int sortedColumnIndex) {
        super(context, layout, c, from, to);
        alphaIndexer = new AlphabetIndexer(c, sortedColumnIndex,
                " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // you have just to instanciate the indexer class like this
        // cursor,index of the sorted colum,a string representing the
        // alphabeth (pay attention on the blank char at the beginning of
        // the sequence)
    }

    @Override
    public int getPositionForSection(int section) {
        return alphaIndexer.getPositionForSection(section); // use the
                                                            // indexer
    }

    @Override
    public int getSectionForPosition(int position) {
        return alphaIndexer.getSectionForPosition(position); // use the
                                                             // indexer
    }

    @Override
    public Object[] getSections() {
        return alphaIndexer.getSections(); // use the indexer
    }

}