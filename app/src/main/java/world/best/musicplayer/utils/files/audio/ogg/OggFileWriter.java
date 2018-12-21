package world.best.musicplayer.utils.files.audio.ogg;

import world.best.musicplayer.utils.files.audio.exceptions.CannotReadException;
import world.best.musicplayer.utils.files.audio.exceptions.CannotWriteException;
import world.best.musicplayer.utils.files.audio.generic.AudioFileWriter;
import world.best.musicplayer.utils.files.tag.Tag;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

/**
 * Write tag data to Ogg File
 *
 * Only works for Ogg files containing a vorbis stream
 */
public class OggFileWriter extends AudioFileWriter
{
    // Logger Object
    public static Logger logger = Logger.getLogger("world.best.musicplayer.utils.files.audio.ogg");

    private OggVorbisTagWriter vtw = new OggVorbisTagWriter();

    protected void writeTag(Tag tag, RandomAccessFile raf, RandomAccessFile rafTemp) throws CannotReadException, CannotWriteException, IOException
    {
        vtw.write(tag, raf, rafTemp);
    }

    protected void deleteTag(RandomAccessFile raf, RandomAccessFile tempRaf) throws CannotReadException, CannotWriteException, IOException
    {
        vtw.delete(raf, tempRaf);
    }
}
