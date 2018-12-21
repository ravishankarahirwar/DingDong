package world.best.musicplayer.utils.files.audio.exceptions;

/**
 * This exception is thrown if the writing process of an audio file failed.
 */
public class CannotWriteException extends Exception
{
    /**
     * (overridden)
     *
     * @see Exception#Exception()
     */
    public CannotWriteException()
    {
        super();
    }

    /**
     * (overridden)
     *
     * @param message
     * @see Exception#Exception(String)
     */
    public CannotWriteException(String message)
    {
        super(message);
    }

    /**
     * (overridden)
     *
     * @param message
     * @param cause
     * @see Exception#Exception(String,Throwable)
     */
    public CannotWriteException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * (overridden)
     *
     * @param cause
     * @see Exception#Exception(Throwable)
     */
    public CannotWriteException(Throwable cause)
    {
        super(cause);

    }

}
