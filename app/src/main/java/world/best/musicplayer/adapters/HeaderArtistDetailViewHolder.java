package world.best.musicplayer.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import world.best.musicplayer.R;


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