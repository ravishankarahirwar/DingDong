package world.best.musicplayer.model;

public class SortItem {

	private String title;

	private String description;

	private int icon;

	public SortItem(String title, String description, int icon) {
		this.title = title;
		this.description = description;
		this.icon = icon;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getIcon() {
		return icon;
	}
}
