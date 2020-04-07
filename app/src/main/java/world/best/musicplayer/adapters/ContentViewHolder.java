package world.best.musicplayer.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import world.best.musicplayer.R;

public class ContentViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout layout;
    public TextView title;
    public TextView artistAlbum;
    public ImageView artwork;
    public ImageView checked;
    public ImageView equalizer;
    public ImageView overFlowMenu;
    public ImageView taggedIndicator;

    public ContentViewHolder (View itemView) {
        super(itemView);

        layout = (RelativeLayout) itemView.findViewById(R.id.layout);
        title = (TextView) itemView.findViewById(R.id.title);
        artistAlbum = (TextView) itemView.findViewById(R.id.artist_album);
        artwork = (ImageView) itemView.findViewById(R.id.artwork);
        checked = (ImageView) itemView.findViewById(R.id.checked);
        equalizer = (ImageView) itemView.findViewById(R.id.equalizer);
        overFlowMenu = (ImageView) itemView.findViewById(R.id.song_overflow_menu);
        taggedIndicator = (ImageView) itemView.findViewById(R.id.tagged_indicator);
    }
}