package world.best.musicplayer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import world.best.musicplayer.R;

public class FooterViewHolder extends RecyclerView.ViewHolder {

    public TextView footerText;
    private AdView mAdView;

    public FooterViewHolder (View itemView) {
        super(itemView);

        mAdView = (AdView)itemView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        footerText = (TextView) itemView.findViewById(R.id.footer_text);
    }
}