package world.best.musicplayer.utils.files.audio.mp4;

import world.best.musicplayer.utils.files.audio.exceptions.CannotWriteException;
import world.best.musicplayer.utils.files.audio.generic.AudioFileWriter;
import world.best.musicplayer.utils.files.tag.Tag;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Mp4 File Writer
 *
 * <p>This can write files containing either the .mp4 or .m4a suffixes
 */
public class Mp4FileWriter extends AudioFileWriter
{

    private Mp4TagWriter tw = new Mp4TagWriter();


    protected void writeTag(Tag tag, RandomAccessFile raf, RandomAccessFile rafTemp) throws CannotWriteException, IOException
    {
        tw.write(tag, raf, rafTemp);
    }

    protected void deleteTag(RandomAccessFile raf, RandomAccessFile rafTemp) throws IOException
    {
        tw.delete(raf, rafTemp);
    }
}
