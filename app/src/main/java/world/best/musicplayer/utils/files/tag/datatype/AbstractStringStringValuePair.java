package world.best.musicplayer.utils.files.tag.datatype;

import java.util.Collections;

public class AbstractStringStringValuePair extends AbstractValuePair<String, String>
{
    protected String lkey = null;

    /**
     * Get Id for Value
     * @param value
     * @return
     */
    public String getIdForValue(String value)
    {
        return valueToId.get(value);
    }

    /**
     * Get value for Id
     * @param id
     * @return
     */
    public String getValueForId(String id)
    {
        return idToValue.get(id);
    }

    protected void createMaps()
    {
        iterator = idToValue.keySet().iterator();
        while (iterator.hasNext())
        {
            lkey = iterator.next();
            value = idToValue.get(lkey);
            valueToId.put(value, lkey);
        }

        //Value List
        iterator = idToValue.keySet().iterator();
        while (iterator.hasNext())
        {
            valueList.add(idToValue.get(iterator.next()));
        }
        //Sort alphabetically
        Collections.sort(valueList);
    }
}
