package world.best.musicplayer.utils.files.tag.datatype;

import world.best.musicplayer.utils.files.tag.InvalidDataTypeException;
import world.best.musicplayer.utils.files.tag.id3.AbstractTagFrameBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of {@link Cloneable}(!!) {@link AbstractDataType}s, continuing until the end of the buffer.
 */
public abstract class AbstractDataTypeList<T extends AbstractDataType> extends AbstractDataType
{

    public AbstractDataTypeList(final String identifier, final AbstractTagFrameBody frameBody)
    {
        super(identifier, frameBody);
        setValue(new ArrayList<T>());
    }

    /**
     * Copy constructor.
     * By convention, subclasses <em>must</em> implement a constructor, accepting an argument of their own class type
     * and call this constructor for {@link world.best.musicplayer.utils.files.tag.id3.ID3Tags#copyObject(Object)} to work.
     * A parametrized {@code AbstractDataTypeList} is not sufficient.
     *
     * @param copy instance
     */
    protected AbstractDataTypeList(final AbstractDataTypeList<T> copy)
    {
        super(copy);
    }

    public List<T> getValue()
    {
        return (List<T>)super.getValue();
    }

    public void setValue(final List<T> list)
    {
        super.setValue(list == null ? new ArrayList<T>() : new ArrayList<T>(list));
    }

    /**
     * Return the size in byte of this datatype list.
     *
     * @return the size in bytes
     */
    public int getSize()
    {
        int size = 0;
        for (final T t : getValue()) {
            size+=t.getSize();
        }
        return size;
    }

    /**
     * Reads list of {@link EventTimingCode}s from buffer starting at the given offset.
     *
     * @param buffer buffer
     * @param offset initial offset into the buffer
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     */
    public void readByteArray(final byte[] buffer, final int offset) throws InvalidDataTypeException
    {
        if (buffer == null)
        {
            throw new NullPointerException("Byte array is null");
        }

        if (offset < 0)
        {
            throw new IndexOutOfBoundsException("Offset to byte array is out of bounds: offset = " + offset + ", array.length = " + buffer.length);
        }

        // no events
        if (offset >= buffer.length)
        {
            getValue().clear();
            return;
        }
        for (int currentOffset = offset; currentOffset<buffer.length;) {
            final T data = createListElement();
            data.readByteArray(buffer, currentOffset);
            data.setBody(frameBody);
            getValue().add(data);
            currentOffset+=data.getSize();
        }
    }

    /**
     * Factory method that creates new elements for this list.
     * Called from {@link #readByteArray(byte[], int)}.
     *
     * @return new list element
     */
    protected abstract T createListElement();

    /**
     * Write contents to a byte array.
     *
     * @return a byte array that that contains the data that should be persisted to file
     */
    public byte[] writeByteArray()
    {
        logger.config("Writing DataTypeList " + this.getIdentifier());
        final byte[] buffer = new byte[getSize()];
        int offset = 0;
        for (final AbstractDataType data : getValue()) {
            final byte[] bytes = data.writeByteArray();
            System.arraycopy(bytes, 0, buffer, offset, bytes.length);
            offset+=bytes.length;
        }

        return buffer;
    }

    @Override
    public int hashCode() {
        return getValue() != null ? getValue().hashCode() : 0;
    }

    @Override
    public String toString() {
        return getValue() != null ? getValue().toString() : "{}";

    }
}
