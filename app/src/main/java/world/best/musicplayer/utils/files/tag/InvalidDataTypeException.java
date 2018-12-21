package world.best.musicplayer.utils.files.tag;

/**
 * Indicates there was a problem parsing this datatype due to a problem with the data
 * such as the array being empty when trying to read from a file.
 *
 * @version $Revision$
 */
public class InvalidDataTypeException extends InvalidTagException
{
    /**
     * Creates a new InvalidDataTypeException datatype.
     */
    public InvalidDataTypeException()
    {
    }

    /**
     * Creates a new InvalidDataTypeException datatype.
     *
     * @param ex the cause.
     */
    public InvalidDataTypeException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates a new InvalidDataTypeException datatype.
     *
     * @param msg the detail message.
     */
    public InvalidDataTypeException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new InvalidDataTypeException datatype.
     *
     * @param msg the detail message.
     * @param ex  the cause.
     */
    public InvalidDataTypeException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
