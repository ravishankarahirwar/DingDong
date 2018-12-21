package world.best.musicplayer.factories;

import java.util.ArrayList;
import java.util.List;

import world.best.musicplayer.model.Menu;
import android.content.Context;
import android.content.res.Resources;
import world.best.musicplayer.R;

public class MenuFactory {
    public static final int MENU_ADD_TAG = 0;
    public static final int MENU_SONG_DETAIL = 1;
    public static final int MENU_SONG_DELETE = 2;
    public static final int MENU_SONG_SHARE = 3;
    public static final int MENU_SONG_RINGTONE = 4;

    private Context mContext;
    private int mMenuFor;

    public interface MenuFor {
        int SONG_LIST = 0;
        int ALBUM_LIST = 1;
        int ARTIST_LIST = 2;
        int SONG_DETAIL = 3;
        int ALBUM_DETAIL = 4;
        int ARTIST_DETAIL = 5;
        int TAG_DETAIL = 6;
        int AUTO_TAG_DETAIL = 7;
    }

    /**
     * @param context
     */
    public MenuFactory(Context context) {
        this.mContext = context;
    }

    /**
     * @param context
     * @param menuFor
     */
    public MenuFactory(Context context, int menuFor) {
        this(context);
        this.mMenuFor = menuFor;
    }
    public List<Menu> getMenus() {
        return getMenus(mContext, mMenuFor);
    }

    public List<Menu> getMenus(Context context, int menuFor) {
        Resources resources;
        String[] menus;
        List<Menu> menuList;
        switch (menuFor) {
        case MenuFor.SONG_LIST:
            resources = mContext.getResources();
            menus = resources.getStringArray(R.array.song_menus);
            menuList = new ArrayList<Menu>();
            menuList.add(new Menu(menus[0], R.drawable.ic_tag));
            menuList.add(new Menu(menus[1], R.drawable.ic_song_details));
            menuList.add(new Menu(menus[2], R.drawable.ic_delete));
            menuList.add(new Menu(menus[3], android.R.drawable.ic_menu_share));
            menuList.add(new Menu(menus[4], R.drawable.ic_menu_set_as_ringtone));
            return menuList;

        case MenuFor.TAG_DETAIL:
            resources = mContext.getResources();
            menus = resources.getStringArray(R.array.tag_menus);
            menuList = new ArrayList<Menu>();
            menuList.add(new Menu(menus[0], R.drawable.ic_tag));
            menuList.add(new Menu(menus[1], R.drawable.ic_song_details));
            menuList.add(new Menu(menus[2], R.drawable.ic_menu_untag));

            return menuList;
        case MenuFor.AUTO_TAG_DETAIL:
            resources = mContext.getResources();
            menus = resources.getStringArray(R.array.song_menus);
            menuList = new ArrayList<Menu>();
            menuList.add(new Menu(menus[0], R.drawable.ic_tag));
            menuList.add(new Menu(menus[1], R.drawable.ic_song_details));

            return menuList;
        case MenuFor.ARTIST_LIST:
            break;

        default:
            return null;
        }
        return null;
    }
}
