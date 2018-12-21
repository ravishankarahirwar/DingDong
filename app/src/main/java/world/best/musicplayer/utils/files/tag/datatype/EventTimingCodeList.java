package world.best.musicplayer.utils.files.tag.datatype;

import world.best.musicplayer.utils.files.tag.id3.framebody.FrameBodyETCO;

/**
 * List of {@link EventTimingCode}s.
 */
public class EventTimingCodeList extends AbstractDataTypeList<EventTimingCode>
{

    /**
     * Mandatory, concretely-typed copy constructor, as required by
     * {@link AbstractDataTypeList#AbstractDataTypeList(AbstractDataTypeList)}.
     *
     * @param copy instance to copy
     */
    public EventTimingCodeList(final EventTimingCodeList copy)
    {
        super(copy);
    }

    public EventTimingCodeList(final FrameBodyETCO body)
    {
        super(DataTypes.OBJ_TIMED_EVENT_LIST, body);
    }

    @Override
    protected EventTimingCode createListElement()
    {
        return new EventTimingCode(DataTypes.OBJ_TIMED_EVENT, frameBody);
    }
}
