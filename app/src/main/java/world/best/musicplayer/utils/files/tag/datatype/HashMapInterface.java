package world.best.musicplayer.utils.files.tag.datatype;

import java.util.Iterator;
import java.util.Map;

/**
 * Represents an interface allowing maping from key to value and value to key
 */
public interface HashMapInterface<K, V>
{
    /**
     * @return a mapping between the key within the frame and the value
     */
    public Map<K, V> getKeyToValue();

    /**
     * @return a mapping between the value to the key within the frame
     */
    public Map<V, K> getValueToKey();

    /**
     * @return an interator of the values within the map
     */
    public Iterator<V> iterator();
}
