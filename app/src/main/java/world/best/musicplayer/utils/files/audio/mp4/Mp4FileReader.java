package world.best.musicplayer.utils.files.audio.mp4;

import world.best.musicplayer.utils.files.audio.exceptions.CannotReadException;
import world.best.musicplayer.utils.files.audio.generic.AudioFileReader;
import world.best.musicplayer.utils.files.audio.generic.GenericAudioHeader;
import world.best.musicplayer.utils.files.tag.Tag;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Mp4 File Reader
 *
 * <p>This can read files containing either the .mp4 or .m4a suffixes
 */
public class Mp4FileReader extends AudioFileReader
{
    private Mp4InfoReader ir = new Mp4InfoReader();
    private Mp4TagReader tr = new Mp4TagReader();

    protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException
    {
        return ir.read(raf);
    }

    protected Tag getTag(RandomAccessFile raf) throws CannotReadException, IOException
    {
        return tr.read(raf);
    }
}
