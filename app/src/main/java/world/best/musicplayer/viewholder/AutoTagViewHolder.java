package world.best.musicplayer.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import world.best.musicplayer.R;
import world.best.musicplayer.activity.TagActivity;
import world.best.musicplayer.utils.Constants;
import android.widget.TextView;

public class AutoTagViewHolder extends RecyclerView.ViewHolder {

    private TextView mostPlayedContainer;
    private TextView recentlyAddedContainer;

    public AutoTagViewHolder(View itemView) {
        super(itemView);
        mostPlayedContainer = (TextView) itemView.findViewById(R.id.most_played);
        recentlyAddedContainer = (TextView) itemView.findViewById(R.id.recently_added);
    }

    public void onBind(final Context context, AutoTagViewHolder autoTagViewHolder) {
        autoTagViewHolder.mostPlayedContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), TagActivity.class);
                intent.putExtra(Constants.EXTRA_TAG_NAME, context.getString(R.string.auto_tag_most_played));
                intent.putExtra(Constants.EXTRA_TAG_TYPE, Constants.TAG_MOST_PLAYED);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            }
        });

        autoTagViewHolder.recentlyAddedContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context
                            .getApplicationContext(), TagActivity.class);
                    intent.putExtra(Constants.EXTRA_TAG_NAME, context.getString(R.string.auto_tag_recently_added));
                    intent.putExtra(Constants.EXTRA_TAG_TYPE, Constants.TAG_RECENTLY_ADDED);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }
            });
    }
}