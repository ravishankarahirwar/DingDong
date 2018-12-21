package world.best.musicplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import world.best.musicplayer.activity.TagActivity;
import world.best.musicplayer.R;
import world.best.musicplayer.utils.Constants;
import world.best.musicplayer.utils.TagUtils;
import world.best.musicplayer.viewholder.AutoTagViewHolder;
import world.best.musicplayer.viewholder.TagViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "TagsAdapter";

    public static final int TYPE_ITEM = 2;
    public static final int TYPE_FOOTER = 1;
    private static final int ROW_AUTO_TAG = 0;
    private static final int ROW_MOST_USED_TAGS = 1;

    private Context mContext;

    private List<List<String>> mOrderedTags = new ArrayList<>();
    private List<String> mMostUsedTags = new ArrayList<>();

    public TagsAdapter(Context context) {
        mContext = context;
        mMostUsedTags = TagUtils.getMostUsedTags();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ROW_AUTO_TAG;
        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case ROW_AUTO_TAG:
                View viewAutoTag = inflater.inflate(R.layout.auto_tag, null);
                return new AutoTagViewHolder(viewAutoTag);
            case TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.content_footer, viewGroup, false));
            default:
                View viewDefault = inflater.inflate(R.layout.tag_items, null);
                return new TagViewHolder(viewDefault);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder baseHolder, final int position) {
        if (baseHolder instanceof AutoTagViewHolder) {
            AutoTagViewHolder autoTagViewHolder = (AutoTagViewHolder) baseHolder;
            autoTagViewHolder.onBind(mContext, autoTagViewHolder);
        } else if (baseHolder instanceof FooterViewHolder) {
            final FooterViewHolder holder = (FooterViewHolder) baseHolder;
            holder.footerText.setText("");
        } else if (baseHolder instanceof TagViewHolder) {
            TagViewHolder holder = (TagViewHolder) baseHolder;
            if (position == ROW_MOST_USED_TAGS) {
                holder.tagIndex.setVisibility(View.GONE);
                holder.tagIcon.setVisibility(View.VISIBLE);
                holder.tagHolder.removeAllViews();
                for (int i = 0; i < mMostUsedTags.size(); i++) {
                    holder.tagHolder.addView(getTagView(mContext, mMostUsedTags.get(i)));
                }
                holder.fullDivider.setVisibility(View.VISIBLE);
                holder.marginalDivider.setVisibility(View.GONE);
            } else {
                holder.tagIndex.setVisibility(View.VISIBLE);
                holder.tagIcon.setVisibility(View.GONE);
                List<String> tags = mOrderedTags.get(position - 2);
                String tag = tags.get(0);
                char index;
                if (tag != null && tag.length() > 0) {
                    index = tag.charAt(0);
                } else {
                    index = '#';
                }

                if (Character.isLetter(index)) {
                    holder.tagIndex.setText(String.valueOf(index));
                } else {
                    holder.tagIndex.setText("#");
                }

                holder.tagHolder.removeAllViews();
                for (int i = 0; i < tags.size(); i++) {
                    holder.tagHolder.addView(getTagView(mContext, tags.get(i)));
                }

                holder.fullDivider.setVisibility(View.GONE);
                holder.marginalDivider.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mOrderedTags.isEmpty()) {
            return 2;
        } else {
            return 2 + mOrderedTags.size();
        }
    }

    public void setData(ArrayList<String> data) {
        if (!data.isEmpty()) {
            orderTags(data);
            mMostUsedTags = TagUtils.getMostUsedTags();
        }
        notifyDataSetChanged();
    }

    private void orderTags(ArrayList<String> data) {
        if (data != null && data.size() > 0) {
            mOrderedTags.clear();
            Collections.sort(data);
            String first = data.get(0);
            char index;
            if (first != null && !first.isEmpty()) {
                index = data.get(0).charAt(0);
            } else {
                index = '#';
            }
            List<String> indexedTags = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                String tag = data.get(i).trim();
                if (tag != null && !tag.isEmpty()) {
                    char currentIndex = tag.charAt(0);
                    if (currentIndex != index && Character.isLetter(currentIndex)) {
                        index = currentIndex;
                        mOrderedTags.add(new ArrayList<>(indexedTags));
                        indexedTags.clear();
                    }

                    indexedTags.add(tag);
                }
            }

            if (indexedTags.size() > 0) {
                mOrderedTags.add(indexedTags);
            }
        }
    }

    public View getTagView(Context context, final String name) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        final View tagView = View.inflate(context,R.layout.tag_item, null);
        TextView tagName = (TextView) tagView.findViewById(R.id.tag_name);
        RelativeLayout container = (RelativeLayout) tagView.findViewById(R.id.container);
        tagView.setLayoutParams(params);
        tagName.setText(name);
        tagName.setVisibility(View.VISIBLE);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Long> idObjs = TagUtils.getCachedSongsForTag(name);
                if (idObjs != null && idObjs.size() > 0) {
                    long[] songIds = new long[idObjs.size()];
                    for (int i = 0; i < songIds.length ;i++) {
                        songIds[i] = idObjs.get(i);
                    }
                    Intent intent = new Intent(mContext.getApplicationContext(), TagActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.EXTRA_TAG_NAME, name);
                    bundle.putInt(Constants.EXTRA_TAG_TYPE, Constants.TAG_NORMAL);
                    bundle.putLongArray(Constants.EXTRA_SONGS_IDS, songIds);
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.getApplicationContext().startActivity(intent);
                }
            }
        });

        return tagView;
    }

    private boolean isPositionFooter(int position) {
        if (mOrderedTags != null) {
            if (position == mOrderedTags.size() + 2) {
                return true;
            }
        }

        return false;
    }
}