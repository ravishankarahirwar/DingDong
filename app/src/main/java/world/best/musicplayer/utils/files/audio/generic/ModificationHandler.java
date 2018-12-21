package world.best.musicplayer.utils.files.audio.generic;

import world.best.musicplayer.utils.files.audio.AudioFile;
import world.best.musicplayer.utils.files.audio.exceptions.ModifyVetoException;

import java.io.File;
import java.util.Vector;

/**
 * This class multicasts the events to multiple listener instances.<br>
 * Additionally the Vetos are handled. (other listeners are notified).
 */
public class ModificationHandler implements AudioFileModificationListener
{

    /**
     * The listeners to wich events are broadcasted are stored here.
     */
    private Vector<AudioFileModificationListener> listeners = new Vector<AudioFileModificationListener>();

    /**
     * This method adds an {@link AudioFileModificationListener}
     *
     * @param l Listener to add.
     */
    public void addAudioFileModificationListener(AudioFileModificationListener l)
    {
        if (!this.listeners.contains(l))
        {
            this.listeners.add(l);
        }
    }

    /**
     * (overridden)
     *
     * @see world.best.musicplayer.utils.files.audio.generic.AudioFileModificationListener#fileModified(world.best.musicplayer.utils.files.audio.AudioFile,
     *File)
     */
    public void fileModified(AudioFile original, File temporary) throws ModifyVetoException
    {
        for (AudioFileModificationListener listener : this.listeners)
        {
            AudioFileModificationListener current = listener;
            try
            {
                current.fileModified(original, temporary);
            }
            catch (ModifyVetoException e)
            {
                vetoThrown(current, original, e);
                throw e;
            }
        }
    }

    /**
     * (overridden)
     *
     * @see world.best.musicplayer.utils.files.audio.generic.AudioFileModificationListener#fileOperationFinished(File)
     */
    public void fileOperationFinished(File result)
    {
        for (AudioFileModificationListener listener : this.listeners)
        {
            AudioFileModificationListener current = listener;
            current.fileOperationFinished(result);
        }
    }

    /**
     * (overridden)
     *
     * @see world.best.musicplayer.utils.files.audio.generic.AudioFileModificationListener#fileWillBeModified(world.best.musicplayer.utils.files.audio.AudioFile,
     *boolean)
     */
    public void fileWillBeModified(AudioFile file, boolean delete) throws ModifyVetoException
    {
        for (AudioFileModificationListener listener : this.listeners)
        {
            AudioFileModificationListener current = listener;
            try
            {
                current.fileWillBeModified(file, delete);
            }
            catch (ModifyVetoException e)
            {
                vetoThrown(current, file, e);
                throw e;
            }
        }
    }

    /**
     * This method removes an {@link AudioFileModificationListener}
     *
     * @param l Listener to remove.
     */
    public void removeAudioFileModificationListener(AudioFileModificationListener l)
    {
        if (this.listeners.contains(l))
        {
            this.listeners.remove(l);
        }
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
        for (AudioFileModificationListener listener : this.listeners)
        {
            AudioFileModificationListener current = listener;
            current.vetoThrown(cause, original, veto);
        }
    }
}
