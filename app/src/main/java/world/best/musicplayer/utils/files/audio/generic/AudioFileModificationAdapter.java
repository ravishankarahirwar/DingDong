package world.best.musicplayer.utils.files.audio.generic;

import world.best.musicplayer.utils.files.audio.AudioFile;
import world.best.musicplayer.utils.files.audio.exceptions.ModifyVetoException;

import java.io.File;

/**
 *
 *
 * @author Christian Laireiter
 */
public class AudioFileModificationAdapter implements AudioFileModificationListener
{

    /**
     * (overridden)
     *
     * @see world.best.musicplayer.utils.files.audio.generic.AudioFileModificationListener#fileModified(world.best.musicplayer.utils.files.audio.AudioFile,
     *File)
     */
    public void fileModified(AudioFile original, File temporary) throws ModifyVetoException
    {
        // Nothing to do
    }

    /**
     * (overridden)
     *
     * @see world.best.musicplayer.utils.files.audio.generic.AudioFileModificationListener#fileOperationFinished(File)
     */
    public void fileOperationFinished(File result)
    {
        // Nothing to do
    }

    /**
     * (overridden)
     *
     * @see world.best.musicplayer.utils.files.audio.generic.AudioFileModificationListener#fileWillBeModified(world.best.musicplayer.utils.files.audio.AudioFile,
     *boolean)
     */
    public void fileWillBeModified(AudioFile file, boolean delete) throws ModifyVetoException
    {
        // Nothing to do
    }

    /**
     * (overridden)
     *
     * @see world.best.musicplayer.utils.files.audio.generic.AudioFileModificationListener#vetoThrown(world.best.musicplayer.utils.files.audio.generic.AudioFileModificationListener,
     * world.best.musicplayer.utils.files.audio.AudioFile,
     * world.best.musicplayer.utils.files.audio.exceptions.ModifyVetoException)
     */
    public void vetoThrown(AudioFileModificationListener cause, AudioFile original, ModifyVetoException veto)
    {
        // Nothing to do
    }

}
