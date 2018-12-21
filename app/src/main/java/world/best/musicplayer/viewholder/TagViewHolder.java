package world.best.musicplayer.viewholder;

import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import world.best.musicplayer.R;
import world.best.musicplayer.ui.FlowLayout;

public class TagViewHolder extends RecyclerView.ViewHolder {

	public RelativeLayout container;
	public TextView tagIndex;
	public ImageView tagIcon;
	public FlowLayout tagHolder;
    public View fullDivider;
    public View marginalDivider;

    public TagViewHolder(View view) {
        super(view);
        container = (RelativeLayout) view.findViewById(R.id.container);
        tagIndex = (TextView) view.findViewById(R.id.text);
        tagIcon = (ImageView) view.findViewById(R.id.icon);
        tagHolder = (FlowLayout) view.findViewById(R.id.tag_layout);
        fullDivider = (View) view.findViewById(R.id.full_divider);
        marginalDivider = (View) view.findViewById(R.id.marginal_divider);
    }
}