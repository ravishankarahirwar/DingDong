package world.best.musicplayer.utils;

import java.util.List;

public interface OnUnTagListener {
    public void onUnTagSong(long songId);
    public void onUnTagSongs(List<Long> songIds);
}
