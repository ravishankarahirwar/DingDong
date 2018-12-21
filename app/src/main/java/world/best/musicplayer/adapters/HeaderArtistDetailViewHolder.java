package world.best.musicplayer.adapters;

import world.best.musicplayer.R;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;


public class HeaderArtistDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView headerTitle;

    public HeaderArtistDetailViewHolder(View itemView) {
        super(itemView);
        headerTitle = (TextView) itemView.findViewById(R.id.header_title);
    }

    public void onBind(final Context context, String title) {
        headerTitle.setText(title);
    }
}