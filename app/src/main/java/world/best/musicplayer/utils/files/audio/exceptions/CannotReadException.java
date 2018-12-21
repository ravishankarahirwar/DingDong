package world.best.musicplayer.utils.files.audio.exceptions;

/**
 * This exception is thrown if an audio file cannot be read.<br>
 * Causes may be invalid data or IO errors.
 */
public class CannotReadException extends Exception
{
    /**
     * Creates an instance.
     */
    public CannotReadException()
    {
        super();
    }

    public CannotReadException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates an instance.
     *
     * @param message The message.
     */
    public CannotReadException(String message)
    {
        super(message);
    }

    /**
     * Creates an instance.
     *
     * @param message The error message.
     * @param cause   The throwable causing this exception.
     */
    public CannotReadException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
