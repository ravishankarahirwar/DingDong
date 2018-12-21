package world.best.musicplayer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import world.best.musicplayer.R;

public class ExpandableHeaderView extends FrameLayout {

    public final static int FIRST_ITEM = 0;
    public final static int SECOND_ITEM = 1;
    public final static int THIRD_ITEM = 2;

    private View mExpandableContainer;
    private ImageButton mExpandButton;

    private HeaderItem mFirstHeaderItem;
    private HeaderItem mSecondHeaderItem;
    private HeaderItem mThirdHeaderItem;

    private boolean mExpanded;

    private HeaderViewCallback mCallback;

    public interface HeaderViewCallback {
        void onItemClicked(int id);
    }

    public ExpandableHeaderView(Context context) {
        super(context);
    }

    public ExpandableHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mExpandableContainer = findViewById(R.id.expandable_container);
        mExpandButton = (ImageButton) findViewById(R.id.expand_button);

        final OnClickListener expandClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpandedState(!mExpanded);
            }
        };
        mExpandButton.setOnClickListener(expandClickListener);

        View firstHeaderItemView = findViewById(R.id.header_item_1);
        firstHeaderItemView.setOnClickListener(expandClickListener);
        mFirstHeaderItem = new HeaderItem(firstHeaderItemView);

        View secondHeaderItemView = findViewById(R.id.header_item_2);
        secondHeaderItemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirstHeaderItem.copyFrom(mSecondHeaderItem);
                if (mCallback != null) {
                    mCallback.onItemClicked(SECOND_ITEM);
                }
                setExpandedState(false);
            }
        });
        mSecondHeaderItem = new HeaderItem(secondHeaderItemView);

        View thirdHeaderItemView = findViewById(R.id.header_item_3);
        thirdHeaderItemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirstHeaderItem.copyFrom(mThirdHeaderItem);
                if (mCallback != null) {
                    mCallback.onItemClicked(THIRD_ITEM);
                }
                setExpandedState(false);
            }
        });
        mThirdHeaderItem = new HeaderItem(thirdHeaderItemView);

        mExpandableContainer.getLayoutParams().height = 0;
    }

    public void setCallback(HeaderViewCallback callback) {
        mCallback = callback;
    }

    public void setHeaderItem(int position) {
        if (position == 0) {
            mFirstHeaderItem.copyFrom(mSecondHeaderItem);
            if (mCallback != null) {
                mCallback.onItemClicked(SECOND_ITEM);
            }
            setExpandedState(false);
        } else {
            mFirstHeaderItem.copyFrom(mThirdHeaderItem);
            if (mCallback != null) {
                mCallback.onItemClicked(THIRD_ITEM);
            }
            setExpandedState(false);
        }
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpandedState(boolean expand) {
        mExpanded = expand;
        updateSize();
    }

    public void createHeader(int index, int imageId, int titleId, int descriptionId) {
        switch (index) {
            case FIRST_ITEM:
                mFirstHeaderItem.create(imageId, titleId, descriptionId);
                break;
            case SECOND_ITEM:
                mSecondHeaderItem.create(imageId, titleId, descriptionId);
                break;
            case THIRD_ITEM:
                mThirdHeaderItem.create(imageId, titleId, descriptionId);
                break;
        }
    }

    private void updateSize() {
        mExpandButton.setImageResource(mExpanded ? R.drawable.ic_up : R.drawable.ic_down);

        mExpandableContainer.getLayoutParams().height
                = mExpanded ? LinearLayout.LayoutParams.WRAP_CONTENT : 0;
        mExpandableContainer.requestLayout();
        setElevation(mExpanded ? 4f : 0f);
    }

    private class HeaderItem {
        View item;
        ImageView image;
        TextView title;
        TextView description;

        public HeaderItem(View item) {
            this.item = item;
            image = (ImageView) item.findViewById(R.id.item_image);
            title = (TextView) item.findViewById(R.id.item_title);
            description = (TextView) item.findViewById(R.id.item_description);
        }

        public void create(int imageId, int titleId, int descriptionId) {
            image.setImageResource(imageId);
            title.setText(titleId);
            description.setText(descriptionId);
        }

        public void copyFrom(HeaderItem item) {
            image.setImageDrawable(item.image.getDrawable());
            description.setText(item.title.getText());
        }
    }
}
