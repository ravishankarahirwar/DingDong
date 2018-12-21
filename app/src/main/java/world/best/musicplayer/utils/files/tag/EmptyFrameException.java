package world.best.musicplayer.utils.files.tag;

/**
 * Thrown when find a Frame but it contains no data.
 *
 * @version $Revision$
 */
public class EmptyFrameException extends InvalidFrameException
{
    /**
     * Creates a new EmptyFrameException datatype.
     */
    public EmptyFrameException()
    {
    }

    /**
     * Creates a new EmptyFrameException datatype.
     *
     * @param ex the cause.
     */
    public EmptyFrameException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates a new EmptyFrameException datatype.
     *
     * @param msg the detail message.
     */
    public EmptyFrameException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new EmptyFrameException datatype.
     *
     * @param msg the detail message.
     * @param ex  the cause.
     */
    public EmptyFrameException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
