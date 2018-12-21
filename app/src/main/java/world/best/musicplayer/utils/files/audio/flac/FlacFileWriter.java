package world.best.musicplayer.utils.files.audio.flac;

import world.best.musicplayer.utils.files.audio.exceptions.CannotWriteException;
import world.best.musicplayer.utils.files.audio.generic.AudioFileWriter;
import world.best.musicplayer.utils.files.tag.Tag;

import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Write/delete tag info for Flac file (opensource lossless encoding)
 */
public class FlacFileWriter extends AudioFileWriter
{

    private FlacTagWriter tw = new FlacTagWriter();

    protected void writeTag(Tag tag, RandomAccessFile raf, RandomAccessFile rafTemp) throws CannotWriteException, IOException
    {
        tw.write(tag, raf, rafTemp);
    }

    protected void deleteTag(RandomAccessFile raf, RandomAccessFile tempRaf) throws CannotWriteException, IOException
    {
        tw.delete(raf, tempRaf);
    }
}

