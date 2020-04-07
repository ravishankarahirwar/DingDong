package world.best.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.AudioEffect;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.service.media.MediaBrowserService;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import musicplayer.IMediaPlaybackService;
import world.best.musicplayer.activity.MainActivity;
import world.best.musicplayer.activity.NowPlayingActivity;
import world.best.musicplayer.utils.MusicUtils;
import world.best.musicplayer.utils.TagUtils;

/**
 * Provides "background" audio playback capabilities, allowing the
 * user to switch between activities without stopping playback.
 */
public class MediaPlaybackService extends MediaBrowserService {
    /** used to specify whether enqueue() should start playing
     * the new list of files right away, next or once all the currently
     * queued files have been played
     */
    public static final int NOW = 1;
    public static final int NEXT = 2;
    public static final int LAST = 3;
    public static final int PLAYBACKSERVICE_STATUS = 1;

    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_AUTO = 2;

    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;
    private HashMap<Byte, Boolean> mAttributePairs = new HashMap<Byte, Boolean>();

    public static final String ACTION = "action";
    public static final String CATEGORY = "world.best.musicplayer.service.CATEGORY";

    public static final String PLAYSTATE_CHANGED = "world.best.musicplayer.playstatechanged";
    public static final String META_CHANGED = "world.best.musicplayer.metachanged";
    public static final String SHUFFLE_CHANGED = "org.codeaurora.musicplayer.shuffle";
    public static final String REPEAT_CHANGED = "org.codeaurora.musicplayer.repeat";
    public static final String QUEUE_CHANGED = "world.best.musicplayer.queuechanged";
    public static final String TAGS_UPDATED = "world.best.musicplayer.tagsupdated";

    public static final String SERVICECMD = "world.best.musicplayer.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPLAY = "play";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String CMDGET = "get";
    public static final String CMDSET = "set";

    public static final String TOGGLEPAUSE_ACTION = "world.best.musicplayer.musicservicecommand.togglepause";
    public static final String PAUSE_ACTION = "world.best.musicplayer.musicservicecommand.pause";
    public static final String PREVIOUS_ACTION = "world.best.musicplayer.musicservicecommand.previous";
    public static final String NEXT_ACTION = "world.best.musicplayer.musicservicecommand.next";
    public static final String STOP_ACTION = "world.best.musicplayer.musicservicecommand.stop";
    private static final String PLAYSTATUS_REQUEST = "org.codeaurora.android.musicplayer.playstatusrequest";
    private static final String PLAYSTATUS_RESPONSE = "org.codeaurora.musicplayer.playstatusresponse";
    private static final String PLAYERSETTINGS_REQUEST = "org.codeaurora.musicplayer.playersettingsrequest";
    private static final String PLAYERSETTINGS_RESPONSE = "org.codeaurora.musicplayer.playersettingsresponse";
    private static final String SET_ADDRESSED_PLAYER = "org.codeaurora.musicplayer.setaddressedplayer";
    private static final String EXTRA_SHUFFLE_VAL = "shuffle";
    private static final String EXTRA_REPEAT_VAL = "repeat";

    private static final int TRACK_ENDED = 1;
    private static final int RELEASE_WAKELOCK = 2;
    private static final int SERVER_DIED = 3;
    private static final int FOCUSCHANGE = 4;
    private static final int FADEDOWN = 5;
    private static final int FADEUP = 6;
    private static final int TRACK_WENT_TO_NEXT = 7;
    private static final int ERROR = 8 ;
    private static final int MAX_HISTORY_SIZE = 100;
    private static final int DEFAULT_REPEAT_VAL = 0;
    private static final int DEFAULT_SHUFFLE_VAL = 0;
    private static final int SET_BROWSED_PLAYER = 1001;
    private static final int SET_PLAY_ITEM = 1002;
    private static final int GET_NOW_PLAYING_ENTRIES = 1003;

    private static final int SCOPE_FILE_SYSTEM = 0x01;
    private static final int SCOPE_NOW_PLAYING = 0x03;
    private static final int INVALID_SONG_UID = 0xffffffff;

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    private IntentFilter mNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private NoisyAudioStreamReceiver mNoisyAudioStreamReceiver;

    private MultiPlayer mPlayer;
    private String mFileToPlay;
    private int mShuffleMode = SHUFFLE_NONE;
    private int mRepeatMode = REPEAT_NONE;
    private int mMediaMountedCount = 0;
    private long [] mAutoShuffleList = null;
    private long [] mPlayList = null;
    private int mPlayListLen = 0;
    private Vector<Integer> mHistory = new Vector<Integer>(MAX_HISTORY_SIZE);
    private Cursor mCursor;
    private int mPlayPos = -1;
    private int mNextPlayPos = -1;
    private static final String LOGTAG = "MediaPlaybackService";
    private final Shuffler mRand = new Shuffler();
    private int mOpenFailedCounter = 0;
    String[] mCursorCols = new String[] {
            MediaStore.Audio.Media._ID,             // index must match IDCOLIDX below
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.IS_PODCAST, // index must match PODCASTCOLIDX below
            MediaStore.Audio.Media.BOOKMARK    // index must match BOOKMARKCOLIDX below
    };
    private final static int IDCOLIDX = 0;
    private final static int PODCASTCOLIDX = 8;
    private final static int BOOKMARKCOLIDX = 9;
    private BroadcastReceiver mUnmountReceiver = null;
    private BroadcastReceiver mA2dpReceiver = null;
    private WakeLock mWakeLock;
    private int mServiceStartId = -1;
    private boolean mServiceInUse = false;
    private boolean mIsSupposedToBePlaying = false;
    private boolean mQuietMode = false;
    private AudioManager mAudioManager;
    private boolean mQueueIsSaveable = true;
    // used to track what type of audio focus loss caused the playback to pause
    private boolean mPausedByTransientLossOfFocus = false;
    // private SetBrowsedPlayerMonitor mSetBrowsedPlayerMonitor;
    // private SetPlayItemMonitor mSetPlayItemMonitor;
    // private GetNowPlayingEntriesMonitor mGetNowPlayingEntriesMonitor;
    public static final byte ATTRIBUTE_ALL = -1;
    public static final byte ERROR_NOTSUPPORTED = -1;
    public static final byte ATTRIBUTE_EQUALIZER = 1;
    public static final byte ATTRIBUTE_REPEATMODE = 2;
    public static final byte ATTRIBUTE_SHUFFLEMODE = 3;
    public static final byte ATTRIBUTE_SCANMODE = 4;

    private byte [] supportedAttributes = new byte[] {
                          ATTRIBUTE_REPEATMODE,
                          ATTRIBUTE_SHUFFLEMODE
                          };

    public static final byte VALUE_REPEATMODE_OFF = 1;
    public static final byte VALUE_REPEATMODE_SINGLE = 2;
    public static final byte VALUE_REPEATMODE_ALL = 3;
    public static final byte VALUE_REPEATMODE_GROUP = 4;

    private byte [] supportedRepeatValues = new byte [] {
                            VALUE_REPEATMODE_OFF,
                            VALUE_REPEATMODE_SINGLE,
                            VALUE_REPEATMODE_ALL
                            };

    public static final byte VALUE_SHUFFLEMODE_OFF = 1;
    public static final byte VALUE_SHUFFLEMODE_ALL = 2;
    public static final byte VALUE_SHUFFLEMODE_GROUP = 3;

    private byte [] supportedShuffleValues = new byte [] {
                            VALUE_SHUFFLEMODE_OFF,
                            VALUE_SHUFFLEMODE_ALL
                            };

    String [] AttrStr = new String[] {
                                "",
                                "Equalizer",
                                "Repeat Mode",
                                "Shuffle Mode",
                                "Scan Mode"
                                };

    private byte [] unsupportedList = new byte [] {
                                    0
                                    };
    private static final String EXTRA_GET_COMMAND = "commandExtra";
    private static final String EXTRA_GET_RESPONSE = "Response";
    private static final int GET_ATTRIBUTE_IDS = 0;
    private static final int GET_VALUE_IDS = 1;
    private static final int GET_ATTRIBUTE_TEXT = 2;
    private static final int GET_VALUE_TEXT        = 3;
    private static final int GET_ATTRIBUTE_VALUES = 4;
    private static final int NOTIFY_ATTRIBUTE_VALUES = 5;
    private static final int SET_ATTRIBUTE_VALUES  = 6;
    private static final int GET_INVALID = 0xff;
    private static final byte GET_ATTR_INVALID = 0x7f;

    private static final String EXTRA_ATTRIBUTE_ID = "Attribute";
    private static final String EXTRA_VALUE_STRING_ARRAY = "ValueStrings";
    private static final String EXTRA_ATTRIB_VALUE_PAIRS = "AttribValuePairs";
    private static final String EXTRA_ATTRIBUTE_STRING_ARRAY = "AttributeStrings";
    private static final String EXTRA_VALUE_ID_ARRAY = "Values";
    private static final String EXTRA_ATTIBUTE_ID_ARRAY = "Attributes";

    private boolean mFetchTagsRunning;

    private SharedPreferences mPreferences;
    // We use this to distinguish between different cards when saving/restoring playlists.
    // This will have to change if we want to support multiple simultaneous cards.
    private int mCardId;

    private MediaSession mSession;

    // interval after which we stop the service when idle
    private static final int IDLE_DELAY = 60000;

    // private NotificationManager mNotificationManager;

    private Handler mMediaplayerHandler = new Handler() {
        float mCurrentVolume = 1.0f;
        @Override
        public void handleMessage(Message msg) {
            MusicUtils.debugLog("mMediaplayerHandler.handleMessage " + msg.what);
            switch (msg.what) {
                case FADEDOWN:
                    mCurrentVolume -= .05f;
                    if (mCurrentVolume > .2f) {
                        mMediaplayerHandler.sendEmptyMessageDelayed(FADEDOWN, 10);
                    } else {
                        mCurrentVolume = .2f;
                    }
                    mPlayer.setVolume(mCurrentVolume);
                    break;
                case FADEUP:
                    mCurrentVolume += .01f;
                    if (mCurrentVolume < 1.0f) {
                        mMediaplayerHandler.sendEmptyMessageDelayed(FADEUP, 10);
                    } else {
                        mCurrentVolume = 1.0f;
                    }
                    mPlayer.setVolume(mCurrentVolume);
                    break;
                case SERVER_DIED:
                    if (mIsSupposedToBePlaying) {
                        gotoNext(true);
                    } else {
                        // the server died when we were idle, so just
                        // reopen the same song (it will start again
                        // from the beginning though when the user
                        // restarts)
                        openCurrentAndNext();
                    }
                    break;
                case TRACK_WENT_TO_NEXT:
                    mPlayPos = mNextPlayPos;
                    if (mCursor != null) {
                        mCursor.close();
                        mCursor = null;
                    }
                    if (mPlayPos >= 0 && mPlayPos < mPlayList.length) {
                        mCursor = getCursorForId(mPlayList[mPlayPos]);
                    }
                    notifyChange(META_CHANGED);
                    updateNotification(true);
                    setNextTrack();
                    break;
                case TRACK_ENDED:
                    if (mRepeatMode == REPEAT_CURRENT) {
                        seek(0);
                        play();
                    } else {
                        gotoNext(false);
                    }
                    break;
                case RELEASE_WAKELOCK:
                    mWakeLock.release();
                    break;

                case FOCUSCHANGE:
                    // This code is here so we can better synchronize it with the code that
                    // handles fade-in
                    switch (msg.arg1) {
                        case AudioManager.AUDIOFOCUS_LOSS:
                            Log.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_LOSS");
                            if(isPlaying()) {
                                mPausedByTransientLossOfFocus = false;
                            }
                            pause();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            mMediaplayerHandler.removeMessages(FADEUP);
                            mMediaplayerHandler.sendEmptyMessage(FADEDOWN);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            Log.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT");
                            if(isPlaying()) {
                                mPausedByTransientLossOfFocus = true;
                            }
                            pause();
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                            Log.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_GAIN");
                            if(!isPlaying() && mPausedByTransientLossOfFocus) {
                                mPausedByTransientLossOfFocus = false;
                                // mCurrentVolume = 0f;
                                // mPlayer.setVolume(mCurrentVolume);
                                play(); // also queues a fade-in
                            } else {
                                mMediaplayerHandler.removeMessages(FADEDOWN);
                                mMediaplayerHandler.sendEmptyMessage(FADEUP);
                            }
                            break;
                        default:
                            Log.e(LOGTAG, "Unknown audio focus change code");
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            MusicUtils.debugLog("mIntentReceiver.onReceive " + action + " / " + cmd);
            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                gotoNext(true);
            } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
                prev();
            } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
                if (isPlaying()) {
                    pause();
                    mPausedByTransientLossOfFocus = false;
                } else {
                    play();
                }
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
                pause();
                mPausedByTransientLossOfFocus = false;
            } else if (CMDPLAY.equals(cmd)) {
                play();
            } else if (CMDSTOP.equals(cmd)) {
                pause();
                mPausedByTransientLossOfFocus = false;
                seek(0);
            } else if (STOP_ACTION.equals(action)) {
                pause();
                stopForeground(true);
            }
        }
    };

    private class NoisyAudioStreamReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                pause();
            }
        }
    }

    private OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            mMediaplayerHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
        }
    };

    private static final long PLAY_TIME_THRESHOLD = 5000; // 5 seconds
    private static PlayTimeTimer mPlayTimeTimer;
    private static long mElapsedTime = 0;

    private class PlayTimeTimer extends CountDownTimer {

        public PlayTimeTimer(long millisInFuture) {
            super(millisInFuture, 1000);
        }

        @Override
        public void onFinish() {
            mElapsedTime = 0;
            TagUtils.updateMostPlayedData(getApplicationContext(), MusicUtils.getAbsolutePathFromURI(getApplicationContext(), Uri.parse(getPath())));
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mElapsedTime = PLAY_TIME_THRESHOLD - millisUntilFinished;
        }
    }

    private void startPlayTimer() {
        mPlayTimeTimer = new PlayTimeTimer(PLAY_TIME_THRESHOLD - mElapsedTime);
        mPlayTimeTimer.start();
    }

    public MediaPlaybackService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        // mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mSession = new MediaSession(this, "OPMediaService");
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                     MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent intent = new Intent(getApplicationContext(), NowPlayingActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 99 /*request code*/,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSession.setSessionActivity(pi);
        mSession.setActive(true);
        PlaybackState state = new PlaybackState.Builder()
                .setActions(
                        PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
                                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
                                PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                .build();
        mSession.setPlaybackState(state);

        mPreferences = getSharedPreferences("Music", Context.MODE_PRIVATE);
        mCardId = MusicUtils.getCardId(this);

        registerExternalStorageListener();
        registerA2dpServiceListener();

        // Needs to be done in this thread, since otherwise ApplicationContext.getPowerManager() crashes.
        mPlayer = new MultiPlayer();
        mPlayer.setHandler(mMediaplayerHandler);

        fetchTags();
        TagUtils.loadDataFromPrefs(getApplicationContext());

        reloadQueue();
        notifyChange(QUEUE_CHANGED);
        notifyChange(META_CHANGED);

        mAttributePairs.put(ATTRIBUTE_EQUALIZER, false);
        mAttributePairs.put(ATTRIBUTE_REPEATMODE, true);
        mAttributePairs.put(ATTRIBUTE_SHUFFLEMODE, true);

        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(SERVICECMD);
        commandFilter.addAction(TOGGLEPAUSE_ACTION);
        commandFilter.addAction(PAUSE_ACTION);
        commandFilter.addAction(NEXT_ACTION);
        commandFilter.addAction(PREVIOUS_ACTION);
        commandFilter.addAction(STOP_ACTION);
        registerReceiver(mIntentReceiver, commandFilter);

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
        mWakeLock.setReferenceCounted(false);

        // If the service was idle, but got killed before it stopped itself, the
        // system will relaunch it. Make sure it gets stopped again in that case.
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);

        mNoisyAudioStreamReceiver = new NoisyAudioStreamReceiver();

        createNotificationChannel();
    }

    @Override
    public BrowserRoot onGetRoot(String s, int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadChildren(String s, Result<List<MediaBrowser.MediaItem>> result) {

    }


    @Override
    public void onDestroy() {
        // Check that we're not being destroyed while something is still playing.
        if (isPlaying()) {
            Log.e(LOGTAG, "Service being destroyed while still playing.");
        }
        // release all MediaPlayer resources, including the native player and wakelocks
        Intent i = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(i);
        mPlayer.release();
        mPlayer = null;

        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        // mAudioManager.unregisterRemoteControlClient(mRemoteControlClient);

        // make sure there aren't any other messages coming
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mMediaplayerHandler.removeCallbacksAndMessages(null);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        unregisterReceiverSafe(mIntentReceiver);
        if (mUnmountReceiver != null) {
            unregisterReceiverSafe(mUnmountReceiver);
            mUnmountReceiver = null;
        }

        if (mA2dpReceiver != null) {
            unregisterReceiverSafe(mA2dpReceiver);
            mA2dpReceiver = null;
        }

        if (mAttributePairs != null) {
            mAttributePairs.clear();
        }

        mWakeLock.release();
        mSession.release();
        super.onDestroy();
    }

    private final char hexdigits [] = new char [] {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };

    private void saveQueue(boolean full) {
        if (!mQueueIsSaveable) {
            return;
        }

        Editor ed = mPreferences.edit();
        //long start = System.currentTimeMillis();
        if (full) {
            StringBuilder q = new StringBuilder();

            // The current playlist is saved as a list of "reverse hexadecimal"
            // numbers, which we can generate faster than normal decimal or
            // hexadecimal numbers, which in turn allows us to save the playlist
            // more often without worrying too much about performance.
            // (saving the full state takes about 40 ms under no-load conditions
            // on the phone)
            int len = mPlayListLen;
            for (int i = 0; i < len; i++) {
                long n = mPlayList[i];
                if (n < 0) {
                    continue;
                } else if (n == 0) {
                    q.append("0;");
                } else {
                    while (n != 0) {
                        int digit = (int)(n & 0xf);
                        n >>>= 4;
                        q.append(hexdigits[digit]);
                    }
                    q.append(";");
                }
            }
            //Log.i("@@@@ service", "created queue string in " + (System.currentTimeMillis() - start) + " ms");
            ed.putString("queue", q.toString());
            ed.putInt("cardid", mCardId);
            if (mShuffleMode != SHUFFLE_NONE) {
                // In shuffle mode we need to save the history too
                len = mHistory.size();
                q.setLength(0);
                for (int i = 0; i < len; i++) {
                    int n = mHistory.get(i);
                    if (n == 0) {
                        q.append("0;");
                    } else {
                        while (n != 0) {
                            int digit = (n & 0xf);
                            n >>>= 4;
                            q.append(hexdigits[digit]);
                        }
                        q.append(";");
                    }
                }
                ed.putString("history", q.toString());
            }
        }
        ed.putInt("curpos", mPlayPos);
        if (mPlayer.isInitialized()) {
            ed.putLong("seekpos", mPlayer.position());
        }
        ed.putInt("repeatmode", mRepeatMode);
        ed.putInt("shufflemode", mShuffleMode);
        SharedPreferencesCompat.apply(ed);

        //Log.i("@@@@ service", "saved state in " + (System.currentTimeMillis() - start) + " ms");
    }

    private void reloadQueue() {
        // Per product spec, we do not give a shit about M permission model.
        try {
            String q = null;

            boolean newstyle = false;
            int id = mCardId;
            if (mPreferences.contains("cardid")) {
                newstyle = true;
                id = mPreferences.getInt("cardid", ~mCardId);
            }
            if (id == mCardId) {
                // Only restore the saved playlist if the card is still
                // the same one as when the playlist was saved
                q = mPreferences.getString("queue", "");
            }
            int qlen = q != null ? q.length() : 0;
            if (qlen > 1) {
                //Log.i("@@@@ service", "loaded queue: " + q);
                int plen = 0;
                int n = 0;
                int shift = 0;
                for (int i = 0; i < qlen; i++) {
                    char c = q.charAt(i);
                    if (c == ';') {
                        ensurePlayListCapacity(plen + 1);
                        mPlayList[plen] = n;
                        plen++;
                        n = 0;
                        shift = 0;
                    } else {
                        if (c >= '0' && c <= '9') {
                            n += ((c - '0') << shift);
                        } else if (c >= 'a' && c <= 'f') {
                            n += ((10 + c - 'a') << shift);
                        } else {
                            // bogus playlist data
                            plen = 0;
                            break;
                        }
                        shift += 4;
                    }
                }
                mPlayListLen = plen;

                int pos = mPreferences.getInt("curpos", 0);
                if (pos < 0 || pos >= mPlayListLen) {
                    // The saved playlist is bogus, discard it
                    mPlayListLen = 0;
                    return;
                }
                mPlayPos = pos;

                // When reloadQueue is called in response to a card-insertion,
                // we might not be able to query the media provider right away.
                // To deal with this, try querying for the current file, and if
                // that fails, wait a while and try again. If that too fails,
                // assume there is a problem and don't restore the state.
                Cursor crsr = MusicUtils.query(this,
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            new String [] {"_id"}, "_id=" + mPlayList[mPlayPos] , null, null);
                if (crsr == null || crsr.getCount() == 0) {
                    // wait a bit and try again
                    SystemClock.sleep(3000);
                    crsr = getContentResolver().query(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            mCursorCols, "_id=" + mPlayList[mPlayPos] , null, null);
                }
                if (crsr != null) {
                    crsr.close();
                }

                // Make sure we don't auto-skip to the next song, since that
                // also starts playback. What could happen in that case is:
                // - musicplayer is paused
                // - go to UMS and delete some files, including the currently playing one
                // - come back from UMS
                // (time passes)
                // - musicplayer app is killed for some reason (out of memory)
                // - musicplayer service is restarted, service restores state, doesn't find
                //   the "current" file, goes to the next and: playback starts on its
                //   own, potentially at some random inconvenient time.
                mOpenFailedCounter = 20;
                mQuietMode = true;
                openCurrentAndNext();
                mQuietMode = false;
                if (!mPlayer.isInitialized()) {
                    // couldn't restore the saved state
                    mPlayListLen = 0;
                    return;
                }

                long seekpos = mPreferences.getLong("seekpos", 0);
                seek(seekpos >= 0 && seekpos < duration() ? seekpos : 0);
                Log.d(LOGTAG, "restored queue, currently at position "
                        + position() + "/" + duration()
                        + " (requested " + seekpos + ")");

                int repmode = mPreferences.getInt("repeatmode", REPEAT_NONE);
                if (repmode != REPEAT_ALL && repmode != REPEAT_CURRENT) {
                    repmode = REPEAT_NONE;
                }
                mRepeatMode = repmode;

                int shufmode = mPreferences.getInt("shufflemode", SHUFFLE_NONE);
                if (shufmode != SHUFFLE_AUTO && shufmode != SHUFFLE_NORMAL) {
                    shufmode = SHUFFLE_NONE;
                }
                if (shufmode != SHUFFLE_NONE) {
                    // in shuffle mode we need to restore the history too
                    q = mPreferences.getString("history", "");
                    qlen = q != null ? q.length() : 0;
                    if (qlen > 1) {
                        plen = 0;
                        n = 0;
                        shift = 0;
                        mHistory.clear();
                        for (int i = 0; i < qlen; i++) {
                            char c = q.charAt(i);
                            if (c == ';') {
                                if (n >= mPlayListLen) {
                                    // bogus history data
                                    mHistory.clear();
                                    break;
                                }
                                mHistory.add(n);
                                n = 0;
                                shift = 0;
                            } else {
                                if (c >= '0' && c <= '9') {
                                    n += ((c - '0') << shift);
                                } else if (c >= 'a' && c <= 'f') {
                                    n += ((10 + c - 'a') << shift);
                                } else {
                                    // bogus history data
                                    mHistory.clear();
                                    break;
                                }
                                shift += 4;
                            }
                        }
                    }
                }
                if (shufmode == SHUFFLE_AUTO) {
                    if (! makeAutoShuffleList()) {
                        shufmode = SHUFFLE_NONE;
                    }
                }
                mShuffleMode = shufmode;
            }
        } catch (Exception e) {
            // noop
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mSession.setActive(true);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mServiceInUse = true;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mSession.setActive(true);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mServiceInUse = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;
        mDelayedStopHandler.removeCallbacksAndMessages(null);

        if (intent != null) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            MusicUtils.debugLog("onStartCommand " + action + " / " + cmd);

            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                gotoNext(true);
            } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
                prev();
            } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
                if (isPlaying()) {
                    pause();
                    mPausedByTransientLossOfFocus = false;
                } else {
                    play();
                }
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
                pause();
                mPausedByTransientLossOfFocus = false;
            } else if (CMDPLAY.equals(cmd)) {
                play();
            } else if (CMDSTOP.equals(cmd)) {
                pause();
                mPausedByTransientLossOfFocus = false;
                seek(0);
            } else if (STOP_ACTION.equals(action)) {
                pause();
                stopForeground(true);
            }
        }

        // make sure the service will shut down on its own if it was
        // just started but not bound to and nothing is playing
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);

        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mServiceInUse = false;

        // Take a snapshot of the current playlist
        saveQueue(true);

        // stopForeground(true);
        if (isPlaying() || mPausedByTransientLossOfFocus) {
            // something is currently playing, or will be playing once
            // an in-progress action requesting audio focus ends, so don't stop the service now.
            // pause();
            return true;
        }

        // If there is a playlist but playback is paused, then wait a while
        // before stopping the service, so that pause/resume isn't slow.
        // Also delay stopping the service if we're transitioning between tracks.
        // if (mPlayListLen > 0  || mMediaplayerHandler.hasMessages(TRACK_ENDED)) {
        //     Message msg = mDelayedStopHandler.obtainMessage();
        //     mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
        //     return true;
        // }

        // No active playlist, OK to stop the service right now
        // stopSelf(mServiceStartId);
        return true;
    }

    private void fetchTags() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            if (!mFetchTagsRunning) {
                mFetchTagsRunning = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TagUtils.loadTagsFromFiles(getApplicationContext(),
                                    MusicUtils.getAllSongPaths(getApplicationContext()));
                            notifyChange(TAGS_UPDATED);
                        } catch (Exception e) {
                            //noop
                        }
                        mFetchTagsRunning = false;
                    }
                }).start();
            }
        }
    }

    private void getNowPlayingEntries() {
        Log.i(LOGTAG,  "getNowPlayingEntries: num of items: " + mPlayListLen);
        synchronized (mPlayList) {
            long [] nowPlayingList = new long[mPlayListLen];
            for (int count = 0; count < mPlayListLen; count++) {
                nowPlayingList[count] = mPlayList[count];
            }
            // mRemoteControlClient.updateNowPlayingEntries(nowPlayingList);
        }
    }

    private void setBrowsedPlayer() {
        Log.i(LOGTAG,  "setBrowsedPlayer");
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.i(LOGTAG, "URI: " + uri);
        // mRemoteControlClient.updateFolderInfoBrowsedPlayer(uri.toString());
    }

    private void playItem(int scope, long playItemUid) {
        boolean success = false;
        Log.i(LOGTAG,  "playItem uid: " + playItemUid + " scope: " + scope);
        if (playItemUid < 0) {
            // mRemoteControlClient.playItemResponse(success);
            return;
        } else if (scope == SCOPE_FILE_SYSTEM) {
            success = openItem(playItemUid);
        } else if (scope == SCOPE_NOW_PLAYING) {
            for (int index = 0; index < mPlayListLen; index++) {
                if (mPlayList[index] == playItemUid) {
                    Log.i(LOGTAG, "Now Playing list contains UID at " + index);
                    success = true;
                    break;
                }
            }
            if (success) {
                success = openItem(playItemUid);
            }
        }
        // mRemoteControlClient.playItemResponse(success);
    }

    private boolean openItem (long playItemUid) {
        boolean isSuccess = false;
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        if (mPlayListLen == 0) {
            Log.e(LOGTAG, "Playlist Length = 0");
            return isSuccess;
        }
        stop(false);
        mCursor = getCursorForId(playItemUid);
        if (mCursor != null) {
            long [] list = new long[] { playItemUid };
            enqueue(list, NOW);
            Log.i(LOGTAG, "Opened UID: " + playItemUid);
            isSuccess = true;
        } else {
            Log.e(LOGTAG, "Cursor could not be fetched");
        }
        return isSuccess;
    }

    private Handler mDelayedStopHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // Check again to make sure nothing is playing right now
            if (isPlaying() || mPausedByTransientLossOfFocus || mServiceInUse
                    || mMediaplayerHandler.hasMessages(TRACK_ENDED)) {
                return;
            }
            // save the queue again, because it might have changed
            // since the user exited the musicplayer app (because of
            // party-shuffle or because the play-position changed)
            saveQueue(true);
            // stopSelf(mServiceStartId);
        }
    };

    private Handler mAvrcpHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_BROWSED_PLAYER:
                    setBrowsedPlayer();
                    break;
                case SET_PLAY_ITEM:
                    playItem(msg.arg1, ((Long)msg.obj).longValue());
                    break;
                case GET_NOW_PLAYING_ENTRIES:
                    getNowPlayingEntries();
                    break;
                default:
            }
        }
    };

    /**
     * Called when we receive a ACTION_MEDIA_EJECT notification.
     *
     * @param storagePath path to mount point for the removed media
     */
    public void closeExternalStorageFiles(String storagePath) {
        // stop playback and clean up if the SD card is going to be unmounted.
        stop(true);
        notifyChange(QUEUE_CHANGED);
        notifyChange(META_CHANGED);
    }

    /**
     * Registers an intent to listen for ACTION_MEDIA_EJECT notifications.
     * The intent will call closeExternalStorageFiles() if the external media
     * is going to be ejected, so applications can clean up any files they have open.
     */
    public void registerExternalStorageListener() {
        if (mUnmountReceiver == null) {
            mUnmountReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                        saveQueue(true);
                        mQueueIsSaveable = false;
                        closeExternalStorageFiles(intent.getData().getPath());
                        fetchTags();
                    } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                        mMediaMountedCount++;
                        mCardId = MusicUtils.getCardId(MediaPlaybackService.this);
                        reloadQueue();
                        mQueueIsSaveable = true;
                        notifyChange(QUEUE_CHANGED);
                        notifyChange(META_CHANGED);
                    } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                        fetchTags();
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
            iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            iFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
            iFilter.addDataScheme("file");
            registerReceiver(mUnmountReceiver, iFilter);
        }
    }

    public void registerA2dpServiceListener() {
        mA2dpReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String cmd = intent.getStringExtra("command");
                if (action.equals(SET_ADDRESSED_PLAYER)) {
                    play(); // this ensures audio focus change is called and play the media
                } else if (action.equals(PLAYSTATUS_REQUEST)) {
                    notifyChange(PLAYSTATUS_RESPONSE);
                } else if (PLAYERSETTINGS_REQUEST.equals(action)) {
                    if (CMDGET.equals(cmd)) {
                        int getCommand = intent.getIntExtra(EXTRA_GET_COMMAND,
                                                            GET_INVALID);
                        byte attribute;
                        byte [] attrIds; byte [] valIds;
                        switch (getCommand) {
                            case GET_ATTRIBUTE_IDS:
                                notifyAttributeIDs(PLAYERSETTINGS_RESPONSE);
                            break;
                            case GET_VALUE_IDS:
                                attribute =
                                    intent.getByteExtra(EXTRA_ATTRIBUTE_ID,
                                                        GET_ATTR_INVALID);
                                notifyValueIDs(PLAYERSETTINGS_RESPONSE, attribute);
                            break;
                            case GET_ATTRIBUTE_TEXT:
                                attrIds = intent.getByteArrayExtra(
                                                     EXTRA_ATTIBUTE_ID_ARRAY);
                                notifyAttributesText(PLAYERSETTINGS_RESPONSE, attrIds);
                            break;
                            case GET_VALUE_TEXT:
                                 attribute =
                                 intent.getByteExtra(EXTRA_ATTRIBUTE_ID,
                                                    GET_ATTR_INVALID);
                                 valIds = intent.getByteArrayExtra(
                                                     EXTRA_VALUE_ID_ARRAY);
                                 notifyAttributeValuesText(
                                     PLAYERSETTINGS_RESPONSE, attribute, valIds);
                            break;
                            case GET_ATTRIBUTE_VALUES:
                                 notifyAttributeValues(PLAYERSETTINGS_RESPONSE,
                                             mAttributePairs, GET_ATTRIBUTE_VALUES);
                            break;
                            default:
                               Log.e(LOGTAG, "invalid getCommand"+getCommand);
                            break;
                        }
                    } else if (CMDSET.equals(cmd)){
                        byte[] attribValuePairs = intent.getByteArrayExtra(
                                                    EXTRA_ATTRIB_VALUE_PAIRS);
                        setValidAttributes(attribValuePairs);
                    }
                }
            }
        };
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SET_ADDRESSED_PLAYER);
        iFilter.addAction(PLAYSTATUS_REQUEST);
        iFilter.addAction(PLAYERSETTINGS_REQUEST);
        registerReceiver(mA2dpReceiver, iFilter);
    }

    /**
     * Notify the change-receivers that something has changed.
     * The intent that is sent contains the following data
     * for the currently playing track:
     * "id" - Integer: the database row ID
     * "artist" - String: the name of the artist
     * "album" - String: the name of the album
     * "track" - String: the name of the track
     * The intent has an action that is one of
     * "world.best.musicplayer.metachanged"
     * "world.best.musicplayer.queuechanged",
     * "world.best.musicplayer.playbackcomplete"
     * "world.best.musicplayer.playstatechanged"
     * respectively indicating that a new track has
     * started playing, that the playback queue has
     * changed, that playback has stopped because
     * the last file in the list has been played,
     * or that the play-state changed (paused/resumed).
     */
    private void notifyChange(String what) {

        Intent i = new Intent();
        i.setAction(what);
        i.addCategory(CATEGORY);
        i.putExtra("id", Long.valueOf(getAudioId()));
        i.putExtra("artist", getArtistName());
        i.putExtra("album",getAlbumName());
        i.putExtra("album_id", getAlbumId());
        i.putExtra("track", getTrackName());
        i.putExtra("playing", isPlaying());
        i.putExtra("shuffle", getShuffleMode());
        i.putExtra("repeat", getRepeatMode());
        this.sendBroadcast(i);

        if (what.equals(QUEUE_CHANGED)) {
            saveQueue(true);
        } else {
            saveQueue(false);
        }
    }

    private void ensurePlayListCapacity(int size) {
        if (mPlayList == null || size > mPlayList.length) {
            // reallocate at 2x requested size so we don't
            // need to grow and copy the array for every
            // insert
            long [] newlist = new long[size * 2];
            int len = mPlayList != null ? mPlayList.length : mPlayListLen;
            for (int i = 0; i < len; i++) {
                newlist[i] = mPlayList[i];
            }
            mPlayList = newlist;
        }
        // FIXME: shrink the array when the needed size is much smaller
        // than the allocated size
    }

    // insert the list of songs at the specified position in the playlist
    private void addToPlayList(long [] list, int position) {
        int addlen = list.length;
        if (position < 0) { // overwrite
            mPlayListLen = 0;
            position = 0;
        }
        ensurePlayListCapacity(mPlayListLen + addlen);
        if (position > mPlayListLen) {
            position = mPlayListLen;
        }

        // move part of list after insertion point
        int tailsize = mPlayListLen - position;
        for (int i = tailsize ; i > 0 ; i--) {
            mPlayList[position + i] = mPlayList[position + i - addlen];
        }

        // copy list into playlist
        for (int i = 0; i < addlen; i++) {
            mPlayList[position + i] = list[i];
        }
        mPlayListLen += addlen;
        if (mPlayListLen == 0) {
            mCursor.close();
            mCursor = null;
            notifyChange(META_CHANGED);
        }
    }

    /**
     * Appends a list of tracks to the current playlist.
     * If nothing is playing currently, playback will be started at
     * the first track.
     * If the action is NOW, playback will switch to the first of
     * the new tracks immediately.
     * @param list The list of tracks to append.
     * @param action NOW, NEXT or LAST
     */
    public void enqueue(long [] list, int action) {
        synchronized(this) {
            if (action == NEXT && mPlayPos + 1 < mPlayListLen) {
                addToPlayList(list, mPlayPos + 1);
                notifyChange(QUEUE_CHANGED);
            } else {
                // action == LAST || action == NOW || mPlayPos + 1 == mPlayListLen
                addToPlayList(list, Integer.MAX_VALUE);
                notifyChange(QUEUE_CHANGED);
                if (action == NOW) {
                    mPlayPos = mPlayListLen - list.length;
                    openCurrentAndNext();
                    play();
                    notifyChange(META_CHANGED);
                    return;
                }
            }
            if (mPlayPos < 0) {
                mPlayPos = 0;
                openCurrentAndNext();
                play();
                notifyChange(META_CHANGED);
            }
        }
    }

    /**
     * Replaces the current playlist with a new list,
     * and prepares for starting playback at the specified
     * position in the list, or a random position if the
     * specified position is 0.
     * @param list The new list of tracks.
     */
    public void open(long [] list, int position) {
        synchronized (this) {
            if (mShuffleMode == SHUFFLE_AUTO) {
                mShuffleMode = SHUFFLE_NORMAL;
            }
            long oldId = getAudioId();
            int listlength = list.length;
            boolean newlist = true;
            if (mPlayListLen == listlength) {
                // possible fast path: list might be the same
                newlist = false;
                for (int i = 0; i < listlength; i++) {
                    if (list[i] != mPlayList[i]) {
                        newlist = true;
                        break;
                    }
                }
            }
            if (newlist) {
                addToPlayList(list, -1);
                notifyChange(QUEUE_CHANGED);
            }
            int oldpos = mPlayPos;
            if (position >= 0) {
                mPlayPos = position;
            } else {
                mPlayPos = mRand.nextInt(mPlayListLen);
            }
            mHistory.clear();

            saveBookmarkIfNeeded();
            openCurrentAndNext();
            if (oldId != getAudioId()) {
                notifyChange(META_CHANGED);
            }
        }
    }

    /**
     * Moves the item at index1 to index2.
     * @param index1
     * @param index2
     */
    public void moveQueueItem(int index1, int index2) {
        synchronized (this) {
            if (index1 >= mPlayListLen) {
                index1 = mPlayListLen - 1;
            }
            if (index2 >= mPlayListLen) {
                index2 = mPlayListLen - 1;
            }
            if (index1 < index2) {
                long tmp = mPlayList[index1];
                for (int i = index1; i < index2; i++) {
                    mPlayList[i] = mPlayList[i+1];
                }
                mPlayList[index2] = tmp;
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index1 && mPlayPos <= index2) {
                        mPlayPos--;
                }
            } else if (index2 < index1) {
                long tmp = mPlayList[index1];
                for (int i = index1; i > index2; i--) {
                    mPlayList[i] = mPlayList[i-1];
                }
                mPlayList[index2] = tmp;
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index2 && mPlayPos <= index1) {
                        mPlayPos++;
                }
            }
            notifyChange(QUEUE_CHANGED);
        }
    }

    /**
     * Returns the current play list
     * @return An array of integers containing the IDs of the tracks in the play list
     */
    public long [] getQueue() {
        synchronized (this) {
            int len = mPlayListLen;
            long [] list = new long[len];
            for (int i = 0; i < len; i++) {
                list[i] = mPlayList[i];
            }
            return list;
        }
    }

    private Cursor getCursorForId(long lid) {
        String id = String.valueOf(lid);

        Cursor c = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                mCursorCols, "_id=" + id , null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    private void openCurrentAndNext() {
        synchronized (this) {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }

            if (mPlayListLen == 0) {
                return;
            }
            stop(false);

            mCursor = getCursorForId(mPlayList[mPlayPos]);
            while(true) {
                if (mCursor != null && mCursor.getCount() != 0 &&
                        open(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" +
                                mCursor.getLong(IDCOLIDX))) {
                    break;
                }
                // if we get here then opening the file failed. We can close the cursor now, because
                // we're either going to create a new one next, or stop trying
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }
                if (mOpenFailedCounter++ < 10 &&  mPlayListLen > 1) {
                    int pos = getNextPosition(false);
                    if (pos < 0) {
                        gotoIdleState();
                        if (mIsSupposedToBePlaying) {
                            mIsSupposedToBePlaying = false;
                            notifyChange(PLAYSTATE_CHANGED);
                        }
                        return;
                    }
                    mPlayPos = pos;
                    stop(false);
                    mPlayPos = pos;
                    mCursor = getCursorForId(mPlayList[mPlayPos]);
                } else {
                    mOpenFailedCounter = 0;
                    if (!mQuietMode) {
                        Toast.makeText(this, R.string.playback_failed, Toast.LENGTH_SHORT).show();
                    }
                    Log.d(LOGTAG, "Failed to open file for playback");
                    gotoIdleState();
                    if (mIsSupposedToBePlaying) {
                        mIsSupposedToBePlaying = false;
                        notifyChange(PLAYSTATE_CHANGED);
                    }
                    return;
                }
            }

            // go to bookmark if needed
            if (isPodcast()) {
                long bookmark = getBookmark();
                // Start playing a little bit before the bookmark,
                // so it's easier to get back in to the narrative.
                seek(bookmark - 5000);
            }
            setNextTrack();
        }
    }

    private void setNextTrack() {
        mNextPlayPos = getNextPosition(false);
        if (mNextPlayPos >= 0) {
            long id = mPlayList[mNextPlayPos];
            mPlayer.setNextDataSource(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id);
        } else {
            mPlayer.setNextDataSource(null);
        }
    }

    /**
     * Opens the specified file and readies it for playback.
     *
     * @param path The full path of the file to be opened.
     */
    public boolean open(String path) {
        synchronized (this) {
            if (path == null) {
                return false;
            }

            // if mCursor is null, try to associate path with a database cursor
            if (mCursor == null) {

                ContentResolver resolver = getContentResolver();
                Uri uri;
                String where;
                String selectionArgs[];
                if (path.startsWith("content://media/")) {
                    uri = Uri.parse(path);
                    where = null;
                    selectionArgs = null;
                } else {
                   uri = MediaStore.Audio.Media.getContentUriForPath(path);
                   where = MediaStore.Audio.Media.DATA + "=?";
                   selectionArgs = new String[] { path };
                }

                try {
                    mCursor = resolver.query(uri, mCursorCols, where, selectionArgs, null);
                    if  (mCursor != null) {
                        if (mCursor.getCount() == 0) {
                            mCursor.close();
                            mCursor = null;
                        } else {
                            mCursor.moveToNext();
                            ensurePlayListCapacity(1);
                            mPlayListLen = 1;
                            mPlayList[0] = mCursor.getLong(IDCOLIDX);
                            mPlayPos = 0;
                        }
                    }
                } catch (UnsupportedOperationException ex) {
                }
            }
            mFileToPlay = path;
            mPlayer.setDataSource(mFileToPlay);
            if (mPlayer.isInitialized()) {
                mOpenFailedCounter = 0;
                return true;
            }
            stop(true);
            return false;
        }
    }

    /**
     * Starts playback of a previously opened file.
     */
    public void play() {
        mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mSession.setActive(true);

        if (mPlayer.isInitialized()) {
            // if we are at the end of the song, go to the next song first
            long duration = mPlayer.duration();
            if (mRepeatMode != REPEAT_CURRENT && duration > 2000 &&
                mPlayer.position() >= duration - 2000) {
                gotoNext(true);
            }

            mPlayer.start();
            // make sure we fade in, in case a previous fadein was stopped because
            // of another focus loss
            mMediaplayerHandler.removeMessages(FADEDOWN);
            mMediaplayerHandler.sendEmptyMessage(FADEUP);

            if (!mIsSupposedToBePlaying) {
                mIsSupposedToBePlaying = true;
                notifyChange(PLAYSTATE_CHANGED);
            }
            updateNotification(true);
        } else if (mPlayListLen <= 0) {
            // This is mostly so that if you press 'play' on a bluetooth headset
            // without every having played anything before, it will still play
            // something.
            setShuffleMode(SHUFFLE_AUTO);
        }
        registerReceiver(mNoisyAudioStreamReceiver, mNoisyIntentFilter);
    }

    private void updateNotification(boolean playing) {
        Notification.MediaStyle style = new Notification.MediaStyle();
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
//
//        Notification notification = new Notification(R.drawable.ic_notification,
//                getString(R.string.app_name), System.currentTimeMillis());

        Bitmap artwork = MusicUtils.getArtwork(getApplicationContext(), getAudioId(), getAlbumId());

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        // Small notification
        final RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setImageViewBitmap(R.id.artwork, artwork);
        contentView.setImageViewResource(
                R.id.play, playing ? R.drawable.ic_pause : R.drawable.ic_play);
        contentView.setImageViewResource(R.id.previous, R.drawable.ic_previous);
        contentView.setImageViewResource(R.id.next, R.drawable.ic_next);
        contentView.setTextViewText(R.id.title, getTrackName());
        contentView.setTextViewText(R.id.artist, getArtistName());
        contentView.setOnClickPendingIntent(
                R.id.previous, generateNotificationAction(PREVIOUS_ACTION));
        contentView.setOnClickPendingIntent(
                R.id.play, generateNotificationAction(TOGGLEPAUSE_ACTION));
        contentView.setOnClickPendingIntent(
                R.id.next, generateNotificationAction(NEXT_ACTION));
        contentView.setOnClickPendingIntent(R.id.notification_container, contentIntent);

        final RemoteViews bigContentView =
                new RemoteViews(getPackageName(), R.layout.notification_expanded);
        bigContentView.setImageViewBitmap(R.id.artwork, artwork);
        bigContentView.setImageViewResource(R.id.play, playing ? R.drawable.ic_pause : R.drawable.ic_play);
        bigContentView.setImageViewResource(R.id.previous, R.drawable.ic_previous);
        bigContentView.setImageViewResource(R.id.next, R.drawable.ic_next);
        bigContentView.setTextViewText(R.id.title, getTrackName());
        bigContentView.setTextViewText(R.id.artist, getArtistName());
        bigContentView.setOnClickPendingIntent(
                R.id.previous, generateNotificationAction(PREVIOUS_ACTION));
        bigContentView.setOnClickPendingIntent(
                R.id.play, generateNotificationAction(TOGGLEPAUSE_ACTION));
        bigContentView.setOnClickPendingIntent(
                R.id.next, generateNotificationAction(NEXT_ACTION));
        bigContentView.setOnClickPendingIntent(R.id.dismiss, generateNotificationAction(STOP_ACTION));
        bigContentView.setOnClickPendingIntent(R.id.notification_container, contentIntent);

        notification.contentView = contentView;
        notification.bigContentView = bigContentView;

        // notification.deleteIntent = generateNotificationAction(STOP_ACTION);
        // mNotificationManager.notify(FMRADIOSERVICE_STATUS, notification);

        startForeground(PLAYBACKSERVICE_STATUS, notification);

        // TODO: Update later for keyguard image
        // MediaMetadata track = new MediaMetadata.Builder()
        //                     .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, artwork)
        //                     .build();
        // mSession.setMetadata(track);

        // Intent uiIntent = new Intent(getApplicationContext(), NowPlayingActivity.class);
        // uiIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 1, uiIntent, 0);

        // Notification notification = new Notification.Builder(getApplicationContext())
        //                                 .setVisibility(Notification.VISIBILITY_PUBLIC)
        //                                 .setSmallIcon(R.drawable.ic_default_art)
        //                                 .addAction(R.drawable.ic_previous, "Previous", generateNotificationAction(PREVIOUS_ACTION))
        //                                 .addAction(playing ? R.drawable.ic_pause : R.drawable.ic_play, "PlayPause", generateNotificationAction(TOGGLEPAUSE_ACTION))
        //                                 .addAction(R.drawable.ic_next, "Next", generateNotificationAction(NEXT_ACTION))
        //                                 .setContentTitle(getTrackName())
        //                                 .setContentText(getArtistName())
        //                                 .setContentIntent(contentIntent)
        //                                 .setLargeIcon(artwork)
        //                                 .setStyle(new Notification.MediaStyle() // TODO: Extend for close action
        //                                     .setMediaSession(getSessionToken())
        //                                     .setShowActionsInCompactView(0, 1, 2))
        //                                 .build();

        // startForeground(PLAYBACKSERVICE_STATUS, notification);
    }


    private final static String CHANNEL_ID = "offline_music";
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private PendingIntent generateNotificationAction(String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MediaPlaybackService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1,
                intent, 0);

        return pendingIntent;
    }

    private void stop(boolean remove_status_icon) {
        if (mPlayer != null && mPlayer.isInitialized()) {
            mPlayer.stop();
        }
        mFileToPlay = null;
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        if (remove_status_icon) {
            gotoIdleState();
        } else {
            stopForeground(false);
        }
        if (remove_status_icon) {
            mIsSupposedToBePlaying = false;
        }
    }

    /**
     * Stops playback.
     */
    public void stop() {
        stop(true);
    }

    /**
     * Pauses playback (call play() to resume)
     */
    public void pause() {
        synchronized(this) {
            mMediaplayerHandler.removeMessages(FADEUP);
            if (isPlaying()) {
                mPlayer.pause();
                updateNotification(false);
                gotoIdleState();
                mIsSupposedToBePlaying = false;
                notifyChange(PLAYSTATE_CHANGED);
                saveBookmarkIfNeeded();
            }
        }
        unregisterReceiverSafe(mNoisyAudioStreamReceiver);
    }

    /** Returns whether something is currently playing
     *
     * @return true if something is playing (or will be playing shortly, in case
     * we're currently transitioning between tracks), false if not.
     */
    public boolean isPlaying() {
        return mIsSupposedToBePlaying;
    }

    public void prev() {
        if (position() < 5000) {
            prevInternal();
        } else {
            seek(0);
            play();
        }
    }

    /*
      Desired behavior for prev/next/shuffle:

      - NEXT will move to the next track in the list when not shuffling, and to
        a track randomly picked from the not-yet-played tracks when shuffling.
        If all tracks have already been played, pick from the full set, but
        avoid picking the previously played track if possible.
      - when shuffling, PREV will go to the previously played track. Hitting PREV
        again will go to the track played before that, etc. When the start of the
        history has been reached, PREV is a no-op.
        When not shuffling, PREV will go to the sequentially previous track (the
        difference with the shuffle-case is mainly that when not shuffling, the
        user can back up to tracks that are not in the history).

        Example:
        When playing an album with 10 tracks from the start, and enabling shuffle
        while playing track 5, the remaining tracks (6-10) will be shuffled, e.g.
        the final play order might be 1-2-3-4-5-8-10-6-9-7.
        When hitting 'prev' 8 times while playing track 7 in this example, the
        user will go to tracks 9-6-10-8-5-4-3-2. If the user then hits 'next',
        a random track will be picked again. If at any time user disables shuffling
        the next/previous track will be picked in sequential order again.
     */
    private void prevInternal() {
        synchronized (this) {
            if (mShuffleMode == SHUFFLE_NORMAL) {
                // go to previously-played track and remove it from the history
                int histsize = mHistory.size();
                if (histsize == 0) {
                    // prev is a no-op
                    return;
                }
                Integer pos;
                if (histsize > 1) {
                    pos = mHistory.remove(histsize - 1);
                    pos = mHistory.remove(histsize - 2);
                } else {
                    pos = mHistory.remove(histsize - 1);
                }
                mPlayPos = pos.intValue();
            } else {
                if (mPlayPos > 0) {
                    mPlayPos--;
                } else {
                    mPlayPos = mPlayListLen - 1;
                }
            }
            // saveBookmarkIfNeeded();
            stop(false);
            openCurrentAndNext();
            play();
            notifyChange(META_CHANGED);
        }
    }

    /**
     * Get the next position to play. Note that this may actually modify mPlayPos
     * if playback is in SHUFFLE_AUTO mode and the shuffle list window needed to
     * be adjusted. Either way, the return value is the next value that should be
     * assigned to mPlayPos;
     */
    private int getNextPosition(boolean force) {
        if (mRepeatMode == REPEAT_CURRENT) {
            if (mPlayPos < 0) return 0;
            return mPlayPos;
        } else if (mShuffleMode == SHUFFLE_NORMAL) {
            // Pick random next track from the not-yet-played ones
            // TODO: make it work right after adding/removing items in the queue.
            // Store the current file in the history, but keep the history at a
            // reasonable size
            if (mPlayPos >= 0) {
                int historySize = mHistory.size();
                if (historySize > 0) {
                    Integer pos = mHistory.get(historySize - 1);
                    if (pos.intValue() != mPlayPos) {
                        mHistory.add(mPlayPos);
                    }
                } else {
                    mHistory.add(mPlayPos);
                }
            }

            if (mHistory.size() > MAX_HISTORY_SIZE) {
                mHistory.removeElementAt(0);
            }

            int numTracks = mPlayListLen;
            int[] tracks = new int[numTracks];
            for (int i = 0; i < numTracks; i++) {
                tracks[i] = i;
            }

            int numHistory = mHistory.size();
            int numUnplayed = numTracks;
            for (int i = 0; i < numHistory; i++) {
                int idx = mHistory.get(i).intValue();
                if (idx < numTracks && tracks[idx] >= 0) {
                    numUnplayed--;
                    tracks[idx] = -1;
                }
            }

            // 'numUnplayed' now indicates how many tracks have not yet
            // been played, and 'tracks' contains the indices of those
            // tracks.
            if (numUnplayed <=0) {
                // everything's already been played
                if (mRepeatMode == REPEAT_ALL || force) {
                    //pick from full set
                    numUnplayed = numTracks;
                    for (int i=0;i < numTracks; i++) {
                        tracks[i] = i;
                    }
                } else {
                    // all done
                    return -1;
                }
            }
            int skip = mRand.nextInt(numUnplayed);
            int cnt = -1;
            while (true) {
                while (tracks[++cnt] < 0)
                    ;
                skip--;
                if (skip < 0) {
                    break;
                }
            }
            return cnt;
        } else if (mShuffleMode == SHUFFLE_AUTO) {
            doAutoShuffleUpdate();
            return mPlayPos + 1;
        } else {
            if (mPlayPos >= mPlayListLen - 1) {
                // we're at the end of the list
                if (mRepeatMode == REPEAT_NONE && !force) {
                    // all done
                    return -1;
                } else if (mRepeatMode == REPEAT_ALL || force) {
                    return 0;
                }
                return -1;
            } else {
                return mPlayPos + 1;
            }
        }
    }

    public void gotoNext(boolean force) {
        synchronized (this) {
            if (mPlayListLen <= 0) {
                Log.d(LOGTAG, "No play queue");
                return;
            }

            int pos = getNextPosition(force);
            if (pos < 0) {
                gotoIdleState();
                if (mIsSupposedToBePlaying) {
                    mIsSupposedToBePlaying = false;
                    notifyChange(PLAYSTATE_CHANGED);
                }
                return;
            }

            mPlayPos = pos;
            saveBookmarkIfNeeded();
            stop(false);
            mPlayPos = pos;
            openCurrentAndNext();
            play();
            notifyChange(META_CHANGED);
        }
    }

    private void gotoIdleState() {
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
        stopForeground(false);
        updateNotification(false);
    }

    private void saveBookmarkIfNeeded() {
        try {
            if (isPodcast()) {
                long pos = position();
                long bookmark = getBookmark();
                long duration = duration();
                if ((pos < bookmark && (pos + 10000) > bookmark) ||
                        (pos > bookmark && (pos - 10000) < bookmark)) {
                    // The existing bookmark is close to the current
                    // position, so don't update it.
                    return;
                }
                if (pos < 15000 || (pos + 10000) > duration) {
                    // if we're near the start or end, clear the bookmark
                    pos = 0;
                }

                // write 'pos' to the bookmark field
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.BOOKMARK, pos);
                Uri uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mCursor.getLong(IDCOLIDX));
                getContentResolver().update(uri, values, null, null);
            }
        } catch (SQLiteException ex) {
        }
    }

    // Make sure there are at least 5 items after the currently playing item
    // and no more than 10 items before.
    private void doAutoShuffleUpdate() {
        boolean notify = false;

        // remove old entries
        if (mPlayPos > 10) {
            removeTracks(0, mPlayPos - 9);
            notify = true;
        }
        // add new entries if needed
        int to_add = 7 - (mPlayListLen - (mPlayPos < 0 ? -1 : mPlayPos));
        for (int i = 0; i < to_add; i++) {
            // pick something at random from the list

            int lookback = mHistory.size();
            int idx = -1;
            while(true) {
                idx = mRand.nextInt(mAutoShuffleList.length);
                if (!wasRecentlyUsed(idx, lookback)) {
                    break;
                }
                lookback /= 2;
            }
            mHistory.add(idx);
            if (mHistory.size() > MAX_HISTORY_SIZE) {
                mHistory.remove(0);
            }
            ensurePlayListCapacity(mPlayListLen + 1);
            mPlayList[mPlayListLen++] = mAutoShuffleList[idx];
            notify = true;
        }
        if (notify) {
            notifyChange(QUEUE_CHANGED);
        }
    }

    // check that the specified idx is not in the history (but only look at at
    // most lookbacksize entries in the history)
    private boolean wasRecentlyUsed(int idx, int lookbacksize) {

        // early exit to prevent infinite loops in case idx == mPlayPos
        if (lookbacksize == 0) {
            return false;
        }

        int histsize = mHistory.size();
        if (histsize < lookbacksize) {
            Log.d(LOGTAG, "lookback too big");
            lookbacksize = histsize;
        }
        int maxidx = histsize - 1;
        for (int i = 0; i < lookbacksize; i++) {
            long entry = mHistory.get(maxidx - i);
            if (entry == idx) {
                return true;
            }
        }
        return false;
    }

    // A simple variation of Random that makes sure that the
    // value it returns is not equal to the value it returned
    // previously, unless the interval is 1.
    private static class Shuffler {
        private int mPrevious;
        private Random mRandom = new Random();
        public int nextInt(int interval) {
            int ret;
            do {
                ret = mRandom.nextInt(interval);
            } while (ret == mPrevious && interval > 1);
            mPrevious = ret;
            return ret;
        }
    };

    private boolean makeAutoShuffleList() {
        ContentResolver res = getContentResolver();
        Cursor c = null;
        try {
            c = res.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[] {MediaStore.Audio.Media._ID}, MediaStore.Audio.Media.IS_MUSIC + "=1",
                    null, null);
            if (c == null || c.getCount() == 0) {
                return false;
            }
            int len = c.getCount();
            long [] list = new long[len];
            for (int i = 0; i < len; i++) {
                c.moveToNext();
                list[i] = c.getLong(0);
            }
            mAutoShuffleList = list;
            return true;
        } catch (RuntimeException ex) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }

    /**
     * Removes the range of tracks specified from the play list. If a file within the range is
     * the file currently being played, playback will move to the next file after the
     * range.
     * @param first The first file to be removed
     * @param last The last file to be removed
     * @return the number of tracks deleted
     */
    public int removeTracks(int first, int last) {
        int numremoved = removeTracksInternal(first, last);
        if (numremoved > 0) {
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }

    private int removeTracksInternal(int first, int last) {
        synchronized (this) {
            if (last < first) return 0;
            if (first < 0) first = 0;
            if (last >= mPlayListLen) last = mPlayListLen - 1;

            boolean gotonext = false;
            if (first <= mPlayPos && mPlayPos <= last) {
                mPlayPos = first;
                gotonext = true;
            } else if (mPlayPos > last) {
                mPlayPos -= (last - first + 1);
            }
            int num = mPlayListLen - last - 1;
            for (int i = 0; i < num; i++) {
                mPlayList[first + i] = mPlayList[last + 1 + i];
            }
            mPlayListLen -= last - first + 1;

            if (gotonext) {
                if (mPlayListLen == 0) {
                    stop(true);
                    mPlayPos = -1;
                    if (mCursor != null) {
                        mCursor.close();
                        mCursor = null;
                    }
                } else {
                    if (mPlayPos >= mPlayListLen) {
                        mPlayPos = 0;
                    }
                    boolean wasPlaying = isPlaying();
                    stop(false);
                    openCurrentAndNext();
                    if (wasPlaying) {
                        play();
                    }
                }
                notifyChange(META_CHANGED);
            }
            return last - first + 1;
        }
    }

    /**
     * Removes all instances of the track with the given id
     * from the playlist.
     * @param id The id to be removed
     * @return how many instances of the track were removed
     */
    public int removeTrack(long id) {
        int numremoved = 0;
        synchronized (this) {
            for (int i = 0; i < mPlayListLen; i++) {
                if (mPlayList[i] == id) {
                    numremoved += removeTracksInternal(i, i);
                    i--;
                }
            }
        }
        if (numremoved > 0) {
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }

    public void setShuffleMode(int shufflemode) {
        synchronized(this) {
            mShuffleMode = shufflemode;
            notifyAttributeValues(PLAYERSETTINGS_RESPONSE,
                            mAttributePairs, SET_ATTRIBUTE_VALUES);
            if (mShuffleMode == shufflemode && mPlayListLen > 0) {
                return;
            }
            if (mShuffleMode == SHUFFLE_AUTO) {
                if (makeAutoShuffleList()) {
                    mPlayListLen = 0;
                    doAutoShuffleUpdate();
                    mPlayPos = 0;
                    openCurrentAndNext();
                    play();
                    notifyChange(META_CHANGED);
                    return;
                } else {
                    // failed to build a list of files to shuffle
                    mShuffleMode = SHUFFLE_NONE;
                }
            }
            notifyChange(SHUFFLE_CHANGED);
            saveQueue(false);
        }
    }

    public int getShuffleMode() {
        return mShuffleMode;
    }

    public void setRepeatMode(int repeatmode) {
        synchronized(this) {
            mRepeatMode = repeatmode;
            setNextTrack();
            notifyAttributeValues(PLAYERSETTINGS_RESPONSE,
                            mAttributePairs, SET_ATTRIBUTE_VALUES);
            notifyChange(REPEAT_CHANGED);
            saveQueue(false);
        }
    }
    public int getRepeatMode() {
        return mRepeatMode;
    }

    public int getMediaMountedCount() {
        return mMediaMountedCount;
    }

    /**
     * Returns the path of the currently playing file, or null if
     * no file is currently playing.
     */
    public String getPath() {
        return mFileToPlay;
    }

    /**
     * Returns the rowid of the currently playing file, or -1 if
     * no file is currently playing.
     */
    public long getAudioId() {
        synchronized (this) {
            if (mPlayPos >= 0 && mPlayer.isInitialized()) {
                return mPlayList[mPlayPos];
            }
        }
        return -1;
    }

    /**
     * Returns the position in the queue
     * @return the position in the queue
     */
    public int getQueuePosition() {
        synchronized(this) {
            return mPlayPos;
        }
    }

    /**
     * Starts playing the track at the given position in the queue.
     * @param pos The position in the queue of the track that will be played.
     */
    public void setQueuePosition(int pos) {
        synchronized(this) {
            stop(false);
            mPlayPos = pos;
            openCurrentAndNext();
            play();
            notifyChange(META_CHANGED);
            if (mShuffleMode == SHUFFLE_AUTO) {
                doAutoShuffleUpdate();
            }
        }
    }

    public String getArtistName() {
        synchronized(this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
        }
    }

    public long getArtistId() {
        synchronized (this) {
            if (mCursor == null) {
                return -1;
            }
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));
        }
    }

    public String getAlbumName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
        }
    }

    public long getAlbumId() {
        synchronized (this) {
            if (mCursor == null) {
                return -1;
            }
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        }
    }

    public String getTrackName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
        }
    }

    private boolean isPodcast() {
        synchronized (this) {
            if (mCursor == null) {
                return false;
            }
            return (mCursor.getInt(PODCASTCOLIDX) > 0);
        }
    }

    private long getBookmark() {
        synchronized (this) {
            if (mCursor == null) {
                return 0;
            }
            return mCursor.getLong(BOOKMARKCOLIDX);
        }
    }

    /**
     * Returns the duration of the file in milliseconds.
     * Currently this method returns -1 for the duration of MIDI files.
     */
    public long duration() {
        if (mPlayer.isInitialized()) {
            return mPlayer.duration();
        }
        return -1;
    }

    /**
     * Returns the current playback position in milliseconds
     */
    public long position() {
        if (mPlayer.isInitialized()) {
            return mPlayer.position();
        }
        return -1;
    }

    /**
     * Seeks to the position specified.
     *
     * @param pos The position to seek to, in milliseconds
     */
    public long seek(long pos) {
        if (mPlayer.isInitialized()) {
            if (pos < 0) pos = 0;
            if (pos > mPlayer.duration()) pos = mPlayer.duration();
            return mPlayer.seek(pos);
        }
        return -1;
    }

    /**
     * Sets the audio session ID.
     *
     * @param sessionId: the audio session ID.
     */
    public void setAudioSessionId(int sessionId) {
        synchronized (this) {
            mPlayer.setAudioSessionId(sessionId);
        }
    }

    /**
     * Returns the audio session ID.
     */
    public int getAudioSessionId() {
        synchronized (this) {
            return mPlayer.getAudioSessionId();
        }
    }

    /**
     * Returns the player supported attribute IDs.
     */
    private void notifyAttributeIDs(String what) {
        Intent i = new Intent(what);
        i.putExtra(EXTRA_GET_RESPONSE, GET_ATTRIBUTE_IDS);
        i.putExtra(EXTRA_ATTIBUTE_ID_ARRAY, supportedAttributes);
        Log.e(LOGTAG, "notifying attributes");
        sendBroadcast(i);
    }

    /**
     * Returns the player supported value IDs for given attrib.
     */
    private void notifyValueIDs(String what, byte attribute) {
        Intent intent = new Intent(what);
        intent.putExtra(EXTRA_GET_RESPONSE, GET_VALUE_IDS);
        intent.putExtra(EXTRA_ATTRIBUTE_ID, attribute);
        switch (attribute) {
            case ATTRIBUTE_REPEATMODE:
                intent.putExtra(EXTRA_VALUE_ID_ARRAY, supportedRepeatValues);
            break;
            case ATTRIBUTE_SHUFFLEMODE:
                intent.putExtra(EXTRA_VALUE_ID_ARRAY, supportedShuffleValues);
            break;
            default:
                Log.e(LOGTAG,"unsupported attribute"+attribute);
                intent.putExtra(EXTRA_VALUE_ID_ARRAY, unsupportedList);
            break;
        }
        sendBroadcast(intent);
    }

    /**
     * Returns the player supported attrib text for given IDs.
     */
    private void notifyAttributesText(String what, byte [] attrIds) {
        String [] AttribStrings = new String [attrIds.length];
        Intent intent = new Intent(what);
        intent.putExtra(EXTRA_GET_RESPONSE, GET_ATTRIBUTE_TEXT);
        for (int i = 0; i < attrIds.length; i++) {
            if (attrIds[i] >= AttrStr.length) {
                Log.e(LOGTAG, "attrib id is"+attrIds[i]+"which is not supported");
                AttribStrings[i] = "";
            } else {
                AttribStrings[i] = AttrStr[attrIds[i]];
            }
        }
        intent.putExtra(EXTRA_ATTRIBUTE_STRING_ARRAY, AttribStrings);
        sendBroadcast(intent);
    }

    /**
     * Returns the player supported value text for given IDs.
     */
    private void notifyAttributeValuesText(String what, int attribute,
                                           byte [] valIds) {
        Intent intent = new Intent(what);
        String [] ValueStrings = new String [valIds.length];
        intent.putExtra(EXTRA_GET_RESPONSE,GET_VALUE_TEXT);
        intent.putExtra(EXTRA_ATTRIBUTE_ID, attribute);
        Log.e(LOGTAG, "attrib is "+ attribute);
        String [] valueStrs = null;
        switch (attribute) {
            case ATTRIBUTE_REPEATMODE:
                valueStrs = new String[] {
                                             "",
                                             getString(R.string.repeat_off_notif),
                                             getString(R.string.repeat_current_notif),
                                             getString(R.string.repeat_all_notif),
                                          };
            break;
            case ATTRIBUTE_SHUFFLEMODE:
                valueStrs = new String[] {
                                           "",
                                           getString(R.string.shuffle_off_notif),
                                           getString(R.string.shuffle_on_notif),
                                          };
            break;
        }
        for (int i = 0; i < valIds.length; i++) {
            if ((valueStrs == null) ||
                (valIds[i] >= valueStrs.length)) {
                Log.e(LOGTAG, "value id is" + valIds[i] + "which is not supported");
                ValueStrings[i] = "";
            } else {
                ValueStrings[i] = valueStrs[valIds[i]];
            }
        }
        intent.putExtra(EXTRA_VALUE_STRING_ARRAY, ValueStrings);
        sendBroadcast(intent);
    }

    /**
     * Returns the player current values for given attrib IDs.
     */
    private void notifyAttributeValues(String what, HashMap<Byte, Boolean> attrIds, int extra) {
        Intent intent = new Intent(what);
        intent.putExtra(EXTRA_GET_RESPONSE, extra);
        int j = 0;
        byte [] retValarray = new byte [attrIds.size()*2];
        for (int i = 0; i < attrIds.size()*2; i++) {
            retValarray[i] = 0x0;
        }

        for (Byte attribute : attrIds.keySet()) {
            if(attrIds.get(attribute)) {
                retValarray[j] = attribute;
                if (attribute == ATTRIBUTE_REPEATMODE) {
                    retValarray[j+1] = getMappingRepeatVal(mRepeatMode);
                } else if (attribute == ATTRIBUTE_SHUFFLEMODE) {
                    retValarray[j+1] = getMappingShuffleVal(mShuffleMode);
                }
                j += 2;
            } else {
                retValarray[j] = attribute;
                retValarray[j+1] = ERROR_NOTSUPPORTED;
                j += 2;
            }
        }
        intent.putExtra(EXTRA_ATTRIB_VALUE_PAIRS, retValarray);
        sendBroadcast(intent);
    }

    /**
     * Sets the values to current player for given attrib IDs.
     */
    private void setValidAttributes(byte [] attribValuePairs) {
        byte attrib, value;

        for (int i = 0; i < (attribValuePairs.length-1); i += 2) {
           attrib = attribValuePairs[i];
           value = attribValuePairs[i+1];
           switch(attrib) {
                case ATTRIBUTE_REPEATMODE:
                    if (isValidRepeatMode(value)) {
                        setRepeatMode(getMappingRepeatMode(value));
                    }
                break;
                case ATTRIBUTE_SHUFFLEMODE:
                    if (isValidShuffleMode(value)) {
                        setShuffleMode(getMappingShuffleMode(value));
                    }
                break;
                default:
                   Log.e(LOGTAG,"Unknown attribute"+attrib);
                   notifyAttributeValues(PLAYERSETTINGS_RESPONSE,
                            mAttributePairs, SET_ATTRIBUTE_VALUES);
                break;
           }
        }
    }

    byte getMappingRepeatVal (int repeatMode) {
        switch (repeatMode) {
            case REPEAT_NONE:
                return VALUE_REPEATMODE_OFF;
            case REPEAT_CURRENT:
                return VALUE_REPEATMODE_SINGLE;
            case REPEAT_ALL:
                return VALUE_REPEATMODE_ALL;
            default:
                return VALUE_REPEATMODE_OFF;
        }
    }

    byte getMappingShuffleVal (int shuffleMode) {
        switch (shuffleMode) {
            case SHUFFLE_NONE:
                return VALUE_SHUFFLEMODE_OFF;
            case SHUFFLE_NORMAL:
                return VALUE_SHUFFLEMODE_ALL;
            case SHUFFLE_AUTO:
                return VALUE_SHUFFLEMODE_ALL;
            default:
                return VALUE_SHUFFLEMODE_OFF;
        }
    }

    int getMappingRepeatMode (byte repeatVal) {
        switch (repeatVal) {
            case VALUE_REPEATMODE_OFF:
                return REPEAT_NONE;
            case VALUE_REPEATMODE_SINGLE:
                return REPEAT_CURRENT;
            case VALUE_REPEATMODE_ALL:
            case VALUE_REPEATMODE_GROUP:
                return REPEAT_ALL;
            default:
                return REPEAT_NONE;
        }
    }

    int getMappingShuffleMode (byte shuffleVal) {
        switch (shuffleVal) {
            case VALUE_SHUFFLEMODE_OFF:
                return SHUFFLE_NONE;
            case VALUE_SHUFFLEMODE_ALL:
            case VALUE_SHUFFLEMODE_GROUP:
                return SHUFFLE_NORMAL;
            default:
                return SHUFFLE_NONE;
        }
    }

    /**
     * Validates the value with CMDSET for Repeat mode.
     */
    private boolean isValidRepeatMode(byte value) {
        if (value == 0) {
            return false;
        }
        value--;
        if ((value >= REPEAT_NONE) && ( value <= REPEAT_ALL)) {
            return true;
        }
        return false;
    }

    /**
     * Validates the value with CMDSET for Shuffle mode.
     */
    private boolean isValidShuffleMode(byte value) {
        if (value == 0) {
            return false;
        }
        value--;
        // check the mapping for local suffle and argument
        if ((value >= SHUFFLE_NONE) && ( value <= SHUFFLE_AUTO)) {
            return true;
        }
        return false;
    }

    private final class MediaSessionCallback extends MediaSession.Callback {
        @Override
        public void onPlay() {
            if (isPlaying()) {
                pause();
                mPausedByTransientLossOfFocus = false;
            } else {
                play();
            }
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
        }

        @Override
        public void onSeekTo(long position) {
            seek(position);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
        }

        @Override
        public void onPause() {
            pause();
        }

        @Override
        public void onStop() {
            stop();
        }

        @Override
        public void onSkipToNext() {
            gotoNext(true);
        }

        @Override
        public void onSkipToPrevious() {
            prev();
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
        }
    }

    /**
     * Provides a unified interface for dealing with midi files and
     * other media files.
     */
    private class MultiPlayer {
        private CompatMediaPlayer mCurrentMediaPlayer = new CompatMediaPlayer();
        private CompatMediaPlayer mNextMediaPlayer;
        private Handler mHandler;
        private boolean mIsInitialized = false;

        public MultiPlayer() {
            mCurrentMediaPlayer.setWakeMode(MediaPlaybackService.this, PowerManager.PARTIAL_WAKE_LOCK);
        }

        public void setDataSource(String path) {
            mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
            if (mIsInitialized) {
                setNextDataSource(null);
            }
        }

        private boolean setDataSourceImpl(MediaPlayer player, String path) {
            try {
                player.reset();
                player.setOnPreparedListener(null);
                if (path.startsWith("content://")) {
                    player.setDataSource(MediaPlaybackService.this, Uri.parse(path));
                } else {
                    player.setDataSource(path);
                }
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.prepare();
            } catch (IOException ex) {
                // TODO: notify the user why the file couldn't be opened
                return false;
            } catch (IllegalArgumentException ex) {
                // TODO: notify the user why the file couldn't be opened
                return false;
            }
            player.setOnCompletionListener(listener);
            player.setOnErrorListener(errorListener);
            Intent i = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
            i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
            i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
            sendBroadcast(i);
            return true;
        }

        public void setNextDataSource(String path) {
            mCurrentMediaPlayer.setNextMediaPlayer(null);
            if (mNextMediaPlayer != null) {
                mNextMediaPlayer.release();
                mNextMediaPlayer = null;
            }
            if (path == null) {
                return;
            }
            mNextMediaPlayer = new CompatMediaPlayer();
            mNextMediaPlayer.setWakeMode(MediaPlaybackService.this, PowerManager.PARTIAL_WAKE_LOCK);
            mNextMediaPlayer.setAudioSessionId(getAudioSessionId());
            if (setDataSourceImpl(mNextMediaPlayer, path)) {
                mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
            } else {
                // failed to open next, we'll transition the old fashioned way,
                // which will skip over the faulty file
                mNextMediaPlayer.release();
                mNextMediaPlayer = null;
            }
        }

        public boolean isInitialized() {
            return mIsInitialized;
        }

        public void start() {
            MusicUtils.debugLog(new Exception("MultiPlayer.start called"));
            mCurrentMediaPlayer.start();
            if (mShuffleMode != SHUFFLE_NORMAL) {
                mHistory.add(mPlayPos);

                if (mHistory.size() > MAX_HISTORY_SIZE) {
                    mHistory.removeElementAt(0);
                }
            }
            startPlayTimer();
        }

        public void stop() {
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
            if (mPlayTimeTimer != null) {
                mPlayTimeTimer.cancel();
            }
            mElapsedTime = 0;
        }

        /**
         * You CANNOT use this player anymore after calling release()
         */
        public void release() {
            stop();
            mCurrentMediaPlayer.release();
            if (mPlayTimeTimer != null) {
                mPlayTimeTimer.cancel();
            }
            mElapsedTime = 0;
        }

        public void pause() {
            mCurrentMediaPlayer.pause();
            if (mPlayTimeTimer != null) {
                mPlayTimeTimer.cancel();
            }
        }

        public void setHandler(Handler handler) {
            mHandler = handler;
        }

        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
                    mCurrentMediaPlayer.release();
                    mCurrentMediaPlayer = mNextMediaPlayer;
                    mNextMediaPlayer = null;
                    mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
                    if (mPlayTimeTimer != null) {
                        mPlayTimeTimer.cancel();
                    }
                    mElapsedTime = 0;
                    startPlayTimer();
                } else {
                    // Acquire a temporary wakelock, since when we return from
                    // this callback the MediaPlayer will release its wakelock
                    // and allow the device to go to sleep.
                    // This temporary wakelock is released when the RELEASE_WAKELOCK
                    // message is processed, but just in case, put a timeout on it.
                    mWakeLock.acquire(30000);
                    mHandler.sendEmptyMessage(TRACK_ENDED);
                    mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
                    mElapsedTime = 0;
                    if (mPlayTimeTimer != null) {
                        mPlayTimeTimer.cancel();
                    }
                }
            }
        };

        MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    mIsInitialized = false;
                    mCurrentMediaPlayer.release();
                    // Creating a new MediaPlayer and settings its wakemode does not
                    // require the media service, so it's OK to do this now, while the
                    // service is still being restarted
                    mCurrentMediaPlayer = new CompatMediaPlayer();
                    mCurrentMediaPlayer.setWakeMode(MediaPlaybackService.this, PowerManager.PARTIAL_WAKE_LOCK);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(SERVER_DIED), 2000);
                    return true;
                default:
                    Log.d("MultiPlayer", "Error: " + what + "," + extra);
                    break;
                }
                return false;
           }
        };

        public long duration() {
            return mCurrentMediaPlayer.getDuration();
        }

        public long position() {
            return mCurrentMediaPlayer.getCurrentPosition();
        }

        public long seek(long whereto) {
            mCurrentMediaPlayer.seekTo((int) whereto);
            return whereto;
        }

        public void setVolume(float vol) {
            mCurrentMediaPlayer.setVolume(vol, vol);
        }

        public void setAudioSessionId(int sessionId) {
            mCurrentMediaPlayer.setAudioSessionId(sessionId);
        }

        public int getAudioSessionId() {
            return mCurrentMediaPlayer.getAudioSessionId();
        }
    }

    static class CompatMediaPlayer extends MediaPlayer implements OnCompletionListener {

        private boolean mCompatMode = true;
        private MediaPlayer mNextPlayer;
        private OnCompletionListener mCompletion;

        public CompatMediaPlayer() {
            try {
                MediaPlayer.class.getMethod("setNextMediaPlayer", MediaPlayer.class);
                mCompatMode = false;
            } catch (NoSuchMethodException e) {
                mCompatMode = true;
                super.setOnCompletionListener(this);
            }
        }

        public void setNextMediaPlayer(MediaPlayer next) {
            if (mCompatMode) {
                mNextPlayer = next;
            } else {
                super.setNextMediaPlayer(next);
            }
        }

        @Override
        public void setOnCompletionListener(OnCompletionListener listener) {
            if (mCompatMode) {
                mCompletion = listener;
            } else {
                super.setOnCompletionListener(listener);
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mNextPlayer != null) {
                // as it turns out, starting a new MediaPlayer on the completion
                // of a previous player ends up slightly overlapping the two
                // playbacks, so slightly delaying the start of the next player
                // gives a better user experience
                SystemClock.sleep(50);
                mNextPlayer.start();
            }
            mCompletion.onCompletion(this);
        }
    }

    private void unregisterReceiverSafe(BroadcastReceiver receiver) {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            // nooooop
        }
    }

    /*
     * By making this a static class with a WeakReference to the Service, we
     * ensure that the Service can be GCd even when the system process still
     * has a remote reference to the stub.
     */
    static class ServiceStub extends IMediaPlaybackService.Stub {
        WeakReference<MediaPlaybackService> mService;

        ServiceStub(MediaPlaybackService service) {
            mService = new WeakReference<MediaPlaybackService>(service);
        }

        public void openFile(String path)
        {
            mService.get().open(path);
        }
        public void open(long [] list, int position) {
            mService.get().open(list, position);
        }
        public int getQueuePosition() {
            return mService.get().getQueuePosition();
        }
        public void setQueuePosition(int index) {
            mService.get().setQueuePosition(index);
        }
        public boolean isPlaying() {
            return mService.get().isPlaying();
        }
        public void stop() {
            mService.get().stop();
        }
        public void pause() {
            mService.get().pause();
        }
        public void play() {
            mService.get().play();
        }
        public void prev() {
            mService.get().prev();
        }
        public void next() {
            mService.get().gotoNext(true);
        }
        public String getTrackName() {
            return mService.get().getTrackName();
        }
        public String getAlbumName() {
            return mService.get().getAlbumName();
        }
        public long getAlbumId() {
            return mService.get().getAlbumId();
        }
        public String getArtistName() {
            return mService.get().getArtistName();
        }
        public long getArtistId() {
            return mService.get().getArtistId();
        }
        public void enqueue(long [] list , int action) {
            mService.get().enqueue(list, action);
        }
        public long [] getQueue() {
            return mService.get().getQueue();
        }
        public void moveQueueItem(int from, int to) {
            mService.get().moveQueueItem(from, to);
        }
        public String getPath() {
            return mService.get().getPath();
        }
        public long getAudioId() {
            return mService.get().getAudioId();
        }
        public long position() {
            return mService.get().position();
        }
        public long duration() {
            return mService.get().duration();
        }
        public long seek(long pos) {
            return mService.get().seek(pos);
        }
        public void setShuffleMode(int shufflemode) {
            mService.get().setShuffleMode(shufflemode);
        }
        public int getShuffleMode() {
            return mService.get().getShuffleMode();
        }
        public int removeTracks(int first, int last) {
            return mService.get().removeTracks(first, last);
        }
        public int removeTrack(long id) {
            return mService.get().removeTrack(id);
        }
        public void setRepeatMode(int repeatmode) {
            mService.get().setRepeatMode(repeatmode);
        }
        public int getRepeatMode() {
            return mService.get().getRepeatMode();
        }
        public int getMediaMountedCount() {
            return mService.get().getMediaMountedCount();
        }
        public int getAudioSessionId() {
            return mService.get().getAudioSessionId();
        }
    }

    private final IBinder mBinder = new ServiceStub(MediaPlaybackService.this);
}
