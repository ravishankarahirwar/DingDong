package world.best.musicplayer.utils.files.tag;

public class PaddingException extends InvalidFrameIdentifierException
{
    /**
     * Creates a new PaddingException datatype.
     */
    public PaddingException()
    {
    }

    /**
     * Creates a new PaddingException datatype.
     *
     * @param ex the cause.
     */
    public PaddingException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates a new PaddingException datatype.
     *
     * @param msg the detail message.
     */
    public PaddingException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new PaddingException  datatype.
     *
     * @param msg the detail message.
     * @param ex  the cause.
     */
    public PaddingException(String msg, Throwable ex)
    {
        super(msg, ex);
    }

}