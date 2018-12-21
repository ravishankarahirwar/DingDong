package world.best.musicplayer.utils.files.audio.asf.util;

import world.best.musicplayer.utils.files.audio.asf.data.Chunk;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This class is needed for ordering all types of
 * {@link world.best.musicplayer.utils.files.audio.asf.data.Chunk}s ascending by their Position. <br>
 */
public final class ChunkPositionComparator implements Comparator<Chunk>,
        Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6337108235272376289L;

    /**
     * {@inheritDoc}
     */
    public int compare(final Chunk first, final Chunk second) {
        return Long.valueOf(first.getPosition())
                .compareTo(second.getPosition());
    }
}