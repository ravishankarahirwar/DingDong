package world.best.musicplayer.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import world.best.musicplayer.R;

public class ViewSelectionListAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater mInflater;
    private String[] mViewSelectionOptions;
    private int mIconColorFilter;

    public ViewSelectionListAdapter(Context context, int resource, String[] selectionOptions) {
        super(context, resource, selectionOptions);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mViewSelectionOptions = selectionOptions;
        mIconColorFilter = mContext.getResources().getColor(R.color.colorAccent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null || convertView.getTag() == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.song_menu_list_item,
                    parent, false);
            viewHolder.label = (TextView) convertView
                    .findViewById(R.id.menu_title);
            viewHolder.icon = (ImageView) convertView
                    .findViewById(R.id.menu_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.label.setText(mViewSelectionOptions[position]);
        switch (position) {
            case 0:
                viewHolder.icon.setColorFilter(mIconColorFilter, PorterDuff.Mode.SRC_IN);
                viewHolder.icon.setImageResource(R.drawable.ic_tracks);
                break;
            case 1:
                viewHolder.icon.setColorFilter(mIconColorFilter, PorterDuff.Mode.SRC_IN);
                viewHolder.icon.setImageResource(R.drawable.ic_artists);
                break;
            case 2:
                viewHolder.icon.setColorFilter(mIconColorFilter, PorterDuff.Mode.SRC_IN);
                viewHolder.icon.setImageResource(R.drawable.ic_albums);
                break;
            case 3:
                viewHolder.icon.setColorFilter(mIconColorFilter, PorterDuff.Mode.SRC_IN);
                viewHolder.icon.setImageResource(R.drawable.ic_tags);
                break;
        }

        return convertView;
    }

    class ViewHolder {
        TextView label;
        ImageView icon;
    }
}