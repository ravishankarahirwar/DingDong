package world.best.musicplayer.utils.files.tag;

/**
 * Thrown if a frame identifier isn't valid.
 */
public class InvalidFrameIdentifierException extends InvalidFrameException
{
    /**
     * Creates a new InvalidFrameIdentifierException datatype.
     */
    public InvalidFrameIdentifierException()
    {
    }

    /**
     * Creates a new InvalidFrameIdentifierException datatype.
     *
     * @param ex the cause.
     */
    public InvalidFrameIdentifierException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates a new InvalidFrameIdentifierException datatype.
     *
     * @param msg the detail message.
     */
    public InvalidFrameIdentifierException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new InvalidFrameIdentifierException datatype.
     *
     * @param msg the detail message.
     * @param ex  the cause.
     */
    public InvalidFrameIdentifierException(String msg, Throwable ex)
    {
        super(msg, ex);
    }

}
