package world.best.musicplayer.utils.files.tag.id3.valuepair;

import world.best.musicplayer.utils.files.tag.datatype.AbstractIntStringValuePair;

public class InterpolationTypes extends AbstractIntStringValuePair
{
    private static InterpolationTypes interpolationTypes;

    public static InterpolationTypes getInstanceOf()
    {
        if (interpolationTypes == null)
        {
            interpolationTypes = new InterpolationTypes();
        }
        return interpolationTypes;
    }

    private InterpolationTypes()
    {
        idToValue.put(0, "Band");
        idToValue.put(1, "Linear");
        createMaps();
    }
}
