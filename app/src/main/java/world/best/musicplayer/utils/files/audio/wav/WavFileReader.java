package world.best.musicplayer.utils.files.audio.wav;

import world.best.musicplayer.utils.files.audio.exceptions.CannotReadException;
import world.best.musicplayer.utils.files.audio.generic.AudioFileReader;
import world.best.musicplayer.utils.files.audio.generic.GenericAudioHeader;
import world.best.musicplayer.utils.files.audio.wav.util.WavInfoReader;
import world.best.musicplayer.utils.files.tag.Tag;

import java.io.IOException;
import java.io.RandomAccessFile;

public class WavFileReader extends AudioFileReader
{

    private WavInfoReader ir = new WavInfoReader();

    protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException
    {
        return ir.read(raf);
    }

    protected Tag getTag(RandomAccessFile raf) throws CannotReadException
    {           
        return new WavTag();
    }
}