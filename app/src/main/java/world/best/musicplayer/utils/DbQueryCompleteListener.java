package world.best.musicplayer.utils;

public interface DbQueryCompleteListener {
    public final int TAG_RENAME = 1;
    public final int TAG_ADD = 2;
    public final int TAG_DELETE = 3;
    public final int QUERY_GENERIC = 4;
    public final int RESULT_SUCCESS = 1;
    public final int RESULT_FAILURE = -1;

    public void onDbQueryComplete(int queryResult,int queryOperationType);
}
