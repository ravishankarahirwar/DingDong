package world.best.musicplayer.utils.files.tag.id3;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * A tag is term given to a container that holds audio metadata
 */
public abstract class AbstractTag extends AbstractTagItem
{
    protected static final String TYPE_TAG = "tag";


    public AbstractTag()
    {
    }

    public AbstractTag(AbstractTag copyObject)
    {
        super(copyObject);
    }

    /**
     * Looks for this tag in the buffer
     *
     * @param byteBuffer
     * @return returns true if found, false otherwise.
     */
    abstract public boolean seek(ByteBuffer byteBuffer);

    /**
     * Writes the tag to the file
     *
     * @param file
     * @throws IOException
     */
    public abstract void write(RandomAccessFile file) throws IOException;


    /**
     * Removes the specific tag from the file
     *
     * @param file MP3 file to append to.
     * @throws IOException on any I/O error
     */
    abstract public void delete(RandomAccessFile file) throws IOException;


    /**
     * Determines whether another datatype is equal to this tag. It just compares
     * if they are the same class, then calls <code>super.equals(obj)</code>.
     *
     * @param obj The object to compare
     * @return if they are equal
     */
    public boolean equals(Object obj)
    {
        return (obj instanceof AbstractTag) && super.equals(obj);

    }

    /**
     * @return
     */
    abstract public Iterator iterator();
}



