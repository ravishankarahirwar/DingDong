package world.best.musicplayer.utils.files.audio.mp3;

import world.best.musicplayer.utils.files.audio.AudioFile;
import world.best.musicplayer.utils.files.audio.exceptions.CannotReadException;
import world.best.musicplayer.utils.files.audio.exceptions.InvalidAudioFrameException;
import world.best.musicplayer.utils.files.audio.exceptions.ReadOnlyFileException;
import world.best.musicplayer.utils.files.audio.generic.AudioFileReader;
import world.best.musicplayer.utils.files.audio.generic.GenericAudioHeader;
import world.best.musicplayer.utils.files.tag.Tag;
import world.best.musicplayer.utils.files.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Read Mp3 Info (retrofitted to entagged ,done differently to entagged which is why some methods throw RuntimeException)
 * because done elsewhere
 */
public class MP3FileReader extends AudioFileReader
{
    protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException
    {
        throw new RuntimeException("MP3FileReader.getEncodingInfo should be called");
    }

    protected Tag getTag(RandomAccessFile raf) throws CannotReadException, IOException
    {
        throw new RuntimeException("MP3FileReader.getEncodingInfo should be called");
    }

    /**
     * @param f
     * @return
     */
    //Override because we read mp3s differently to the entagged code
    public AudioFile read(File f) throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        MP3File mp3File = new MP3File(f, MP3File.LOAD_IDV1TAG | MP3File.LOAD_IDV2TAG, true);
        return mp3File;
    }

    /**
     * Read
     *
     * @param f
     * @return
     * @throws ReadOnlyFileException thrown if the file is not writable
     * @throws world.best.musicplayer.utils.files.tag.TagException
     * @throws IOException
     * @throws world.best.musicplayer.utils.files.audio.exceptions.InvalidAudioFrameException
     */
    public AudioFile readMustBeWritable(File f) throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        MP3File mp3File = new MP3File(f, MP3File.LOAD_IDV1TAG | MP3File.LOAD_IDV2TAG, false);
        return mp3File;
    }

}
