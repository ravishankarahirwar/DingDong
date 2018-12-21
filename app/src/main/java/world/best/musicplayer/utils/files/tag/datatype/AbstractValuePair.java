package world.best.musicplayer.utils.files.tag.datatype;

import java.util.*;

/**
 * A two way mapping between an id and a value
 */
public abstract class AbstractValuePair<I, V>
{
    protected final Map<I, V> idToValue = new LinkedHashMap<I, V>();
    protected final Map<V, I> valueToId = new LinkedHashMap<V, I>();
    protected final List<V> valueList = new ArrayList<V>();

    protected Iterator<I> iterator = idToValue.keySet().iterator();

    protected String value;

    /**
     * Get list in alphabetical order
     * @return
     */
    public List<V> getAlphabeticalValueList()
    {
        return valueList;
    }

    public Map<I, V> getIdToValueMap()
    {
        return idToValue;
    }

    public Map<V, I> getValueToIdMap()
    {
        return valueToId;
    }

    /**
     * @return the number of elements in the mapping
     */
    public int getSize()
    {
        return valueList.size();
    }
}
