package world.best.musicplayer.utils.files.tag.datatype;

import java.util.Collections;
import java.util.Map;

/**
 * A two way mapping between an Integral Id and a String value
 */
public class AbstractIntStringValuePair extends AbstractValuePair<Integer, String>
{
    protected Integer key = null;

    /**
     * Get Id for Value
     * @param value
     * @return
     */
    public Integer getIdForValue(String value)
    {
        return valueToId.get(value);
    }

    /**
     * Get value for Id
     * @param id
     * @return
     */
    public String getValueForId(int id)
    {
        return idToValue.get(id);
    }

    protected void createMaps()
    {
        //Create the reverse the map
        for (Map.Entry<Integer, String> entry : idToValue.entrySet())
        {
            valueToId.put(entry.getValue(), entry.getKey());
        }

        //Value List sort alphabetically
        valueList.addAll(idToValue.values());
        Collections.sort(valueList);
    }
}
