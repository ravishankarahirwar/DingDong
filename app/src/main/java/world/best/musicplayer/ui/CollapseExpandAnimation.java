package world.best.musicplayer.ui;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CollapseExpandAnimation extends Animation {

    private View mView;

    private final int mFromDimension;
    private final int mToDimension;

    private final boolean mExpand;

    public CollapseExpandAnimation(View view, int fromDimension, int toDimension, boolean expand) {
        mView = view;
        mFromDimension = fromDimension;
        mToDimension = toDimension;
        mExpand = expand;
    }

    @Override
    public void applyTransformation(float interpolatedTime, Transformation t) {
        int curPos;
        if (mExpand) {
            curPos = mFromDimension + (int) ((mToDimension - mFromDimension) * interpolatedTime);

        } else {
            curPos = mFromDimension + (int) ((mToDimension - mFromDimension) * (1f - interpolatedTime));
        }

        mView.getLayoutParams().height = curPos;
        mView.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}