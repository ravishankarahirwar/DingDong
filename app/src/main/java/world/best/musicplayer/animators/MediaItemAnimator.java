package world.best.musicplayer.animators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import world.best.musicplayer.adapters.BaseCursorAdapter;
import world.best.musicplayer.adapters.ContentViewHolder;

public class MediaItemAnimator extends DefaultItemAnimator {

    private int lastAddAnimatedItem = -2;

    public MediaItemAnimator() {
        super();
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.getItemViewType() == BaseCursorAdapter.TYPE_ITEM) {
            if (viewHolder.getLayoutPosition() > lastAddAnimatedItem) {
                lastAddAnimatedItem++;
                runEnterAnimation((ContentViewHolder) viewHolder);
                return false;
            }
        }

        dispatchAddFinished(viewHolder);
        return false;
    }

    private void runEnterAnimation(final ContentViewHolder holder) {
        WindowManager wm = (WindowManager) holder.itemView.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int screenHeight = size.y;
        holder.itemView.setTranslationY(screenHeight);
        holder.itemView.setAlpha(0.3f);
        holder.itemView.animate()
                .translationY(0)
                .alpha(1.0f)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dispatchAddFinished(holder);
                    }
                })
                .start();
    }

    @Override
    public void runPendingAnimations() {
        super.runPendingAnimations();
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return super.animateRemove(holder);
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return super.animateMove(holder, fromX, fromY, toX, toY);
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
    }

    @Override
    public boolean isRunning() {
        return super.isRunning();
    }

    @Override
    public void endAnimations() {
        super.endAnimations();
    }
}