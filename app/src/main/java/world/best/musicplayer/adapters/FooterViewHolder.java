package world.best.musicplayer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import world.best.musicplayer.R;

public class FooterViewHolder extends RecyclerView.ViewHolder {

    public TextView footerText;

    public FooterViewHolder (View itemView) {
        super(itemView);

        footerText = itemView.findViewById(R.id.footer_text);
    }
}