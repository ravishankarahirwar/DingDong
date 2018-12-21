package world.best.musicplayer.adapters;

import java.util.List;

import world.best.musicplayer.model.Menu;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import world.best.musicplayer.R;

/**
 * This class is use for showing a menu list for all views.
 * After clicking on three dots in row area.
 */
public class SongMenuListAdapter extends ArrayAdapter<Menu> {
    private Context mContext;
    private List<Menu> mMenus;
    private LayoutInflater mInflater;

    public SongMenuListAdapter(Context context, int resource, List<Menu> menus) {
        super(context, resource, menus);
        mContext = context;
        mMenus = menus;
        mInflater = LayoutInflater.from(mContext);
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

        viewHolder.label.setText(mMenus.get(position).getMenuTitle());
        viewHolder.icon.setImageResource(mMenus.get(position).getMenuIcon());
        return convertView;
    }

    class ViewHolder {
        TextView label;
        ImageView icon;
    }
}