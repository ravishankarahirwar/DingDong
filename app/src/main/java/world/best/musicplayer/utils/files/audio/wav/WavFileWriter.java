package world.best.musicplayer.utils.files.audio.wav;

import world.best.musicplayer.utils.files.audio.exceptions.CannotWriteException;
import world.best.musicplayer.utils.files.audio.generic.AudioFileWriter;
import world.best.musicplayer.utils.files.tag.Tag;

import java.io.IOException;
import java.io.RandomAccessFile;

public class WavFileWriter extends AudioFileWriter
{
    protected void writeTag(Tag tag, RandomAccessFile raf, RandomAccessFile rafTemp) throws CannotWriteException, IOException
    {
        //Nothing to do for wav file, no tag are supported
    }

    protected void deleteTag(RandomAccessFile raf, RandomAccessFile tempRaf) throws CannotWriteException, IOException
    {
        //Nothing to do for wav file, no tag are supported
    }
}