package world.best.musicplayer.utils.files.tag.datatype;

import world.best.musicplayer.utils.files.tag.id3.framebody.FrameBodySYTC;

/**
 * List of {@link world.best.musicplayer.utils.files.tag.datatype.SynchronisedTempoCode}s.
 */
public class SynchronisedTempoCodeList extends AbstractDataTypeList<SynchronisedTempoCode>
{

    /**
     * Mandatory, concretely-typed copy constructor, as required by
     * {@link world.best.musicplayer.utils.files.tag.datatype.AbstractDataTypeList#AbstractDataTypeList(world.best.musicplayer.utils.files.tag.datatype.AbstractDataTypeList)}.
     *
     * @param copy instance to copy
     */
    public SynchronisedTempoCodeList(final SynchronisedTempoCodeList copy)
    {
        super(copy);
    }

    public SynchronisedTempoCodeList(final FrameBodySYTC body)
    {
        super(DataTypes.OBJ_SYNCHRONISED_TEMPO_LIST, body);
    }

    @Override
    protected SynchronisedTempoCode createListElement()
    {
        return new SynchronisedTempoCode(DataTypes.OBJ_SYNCHRONISED_TEMPO, frameBody);
    }
}
