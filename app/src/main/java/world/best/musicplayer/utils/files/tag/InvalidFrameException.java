package world.best.musicplayer.utils.files.tag;

/**
 * Thrown if frame cannot be read correctly.
 */
public class InvalidFrameException extends InvalidTagException
{
    /**
     * Creates a new InvalidFrameException datatype.
     */
    public InvalidFrameException()
    {
    }

    /**
     * Creates a new InvalidFrameException datatype.
     *
     * @param ex the cause.
     */
    public InvalidFrameException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates a new InvalidFrameException datatype.
     *
     * @param msg the detail message.
     */
    public InvalidFrameException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new InvalidFrameException datatype.
     *
     * @param msg the detail message.
     * @param ex  the cause.
     */
    public InvalidFrameException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
