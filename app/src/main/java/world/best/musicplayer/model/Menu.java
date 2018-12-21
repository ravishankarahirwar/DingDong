package world.best.musicplayer.model;

/**
 * @author [O2]Ravishankar
 * @since 04/Feb/2016
 * This class is use as a model class of Menu List.
 */
public class Menu {
    private String mMenuTitle;
    private int mMenuIcon;

    /**
     * @param mMenuTitle
     * @param mMenuIcon
     */
    public Menu(String menuTitle, int menuIcon) {
        super();
        this.mMenuTitle = menuTitle;
        this.mMenuIcon = menuIcon;
    }

    /**
     * @return the mMenuTitle
     */
    public String getMenuTitle() {
        return mMenuTitle;
    }

    /**
     * @param menuTitle
     *            the mMenuTitle to set
     */
    public void setMenuTitle(String menuTitle) {
        this.mMenuTitle = menuTitle;
    }

    /**
     * @return the mMenuIcon
     */
    public int getMenuIcon() {
        return mMenuIcon;
    }

    /**
     * @param menuIcon
     *            the mMenuIcon to set
     */
    public void setMenuIcon(int menuIcon) {
        this.mMenuIcon = menuIcon;
    }

}
