package world.best.musicplayer.utils.files.tag.lyrics3;

import world.best.musicplayer.utils.files.tag.id3.AbstractTag;
import world.best.musicplayer.utils.files.tag.id3.ID3v1Tag;

import java.io.IOException;
import java.io.RandomAccessFile;


public abstract class AbstractLyrics3 extends AbstractTag
{
    public AbstractLyrics3()
    {
    }

    public AbstractLyrics3(AbstractLyrics3 copyObject)
    {
        super(copyObject);
    }

    /**
     * @param file
     * @throws IOException
     */
    public void delete(RandomAccessFile file) throws IOException
    {
        long filePointer;
        ID3v1Tag id3v1tag = new ID3v1Tag();


    }
}
