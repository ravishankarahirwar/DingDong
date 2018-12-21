package world.best.musicplayer.utils.files.audio.exceptions;

/**
 * Thrown if portion of file thought to be an AudioFrame is found to not be.
 */
public class InvalidAudioFrameException extends Exception
{
    public InvalidAudioFrameException(String message)
    {
        super(message);
    }
}
