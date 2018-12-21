package world.best.musicplayer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import world.best.musicplayer.R;
import world.best.musicplayer.ui.FlowLayout;
import world.best.musicplayer.utils.TagUtils;

import java.util.ArrayList;
import java.util.List;

public class AddTagDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private EditText mTagName;
    private ImageView mAddTag;
    private ImageView mCloseDialog;

    private LinearLayout mRecentsContainer;
    private FlowLayout mRecentsLayout;
    private LinearLayout mTaggedContainer;
    private FlowLayout mTaggedLayout;
    private LinearLayout mSearchContainer;
    private FlowLayout mSearchLayout;

    private ScrollView mScrollContainer;

    private ArrayList<String> mRecentlyUsedTags = new ArrayList<>();
    private List<String> mTags = new ArrayList<>();
    private List<String> mFilePaths = new ArrayList<>();

    private boolean mMultiMode = false;

    private static final int VIEW_TYPE_RECENT = 0;
    private static final int VIEW_TYPE_TAGGED = 1;
    private static final int VIEW_TYPE_SEARCH = 2;

    public interface OnTagChangeListener {
        public void onTagDelete(String tagToDelete);
        public void onTagAdded(String bandName);
    }

    private OnTagChangeListener mOnTagChangeListener;

    public AddTagDialog(Context context, List<String> filePaths, OnTagChangeListener onTagChangeListener) {
        super(context, R.style.BandEditDialog);
        mContext = context;
        mFilePaths = filePaths;
        mOnTagChangeListener = onTagChangeListener;
        if (mFilePaths.size() > 1) {
            mMultiMode = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_dialog);

        mTagName = (EditText) findViewById(R.id.tag_name);
        mCloseDialog = (ImageView) findViewById(R.id.close);
        mAddTag = (ImageView) findViewById(R.id.add_tag);

        mRecentsContainer = (LinearLayout) findViewById(R.id.recents_container);
        mRecentsLayout = (FlowLayout) findViewById(R.id.recents_layout);
        mTaggedContainer = (LinearLayout) findViewById(R.id.tagged_container);
        mTaggedLayout = (FlowLayout) findViewById(R.id.tagged_layout);
        mSearchContainer = (LinearLayout) findViewById(R.id.search_container);
        mSearchLayout = (FlowLayout) findViewById(R.id.search_layout);

        mScrollContainer = (ScrollView) findViewById(R.id.content_container);

        mLayoutInflater = LayoutInflater.from(mContext);

        mTagName.addTextChangedListener(passwordWatcher);
        mTagName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(TextUtils.isEmpty(mTagName.getText().toString())) {
                        AddTagDialog.this.dismiss();
                    } else {
                        addTag(mTagName.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });

        mAddTag.setOnClickListener(this);
        mCloseDialog.setOnClickListener(this);

        if (!mMultiMode) {
            mTags = TagUtils.getTagsForSong(mContext, mFilePaths.get(0));
        }
        mRecentlyUsedTags = TagUtils.getRecentlyUsedTags();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP | Gravity.LEFT;

        wlp.width = LayoutParams.MATCH_PARENT;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        if (!mRecentlyUsedTags.isEmpty()) {
            mRecentsContainer.setVisibility(View.VISIBLE);
            for (int i = mRecentlyUsedTags.size() - 1; i >= 0; i--) {
                mRecentsLayout.addView(getTagView(mContext, mRecentlyUsedTags.get(i).toString(), VIEW_TYPE_RECENT, false));
            }
        } else {
            mRecentsContainer.setVisibility(View.GONE);
        }

        if (mTags != null && mTags.size() > 0) {
            mTaggedContainer.setVisibility(View.VISIBLE);
            for (int i = 0; i < mTags.size(); i++) {
                mTaggedLayout.addView(getTagView(mContext, mTags.get(i).toString(), VIEW_TYPE_TAGGED, true));
            }
        } else {
            mTaggedContainer.setVisibility(View.GONE);
        }

        if (mTagName.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close :
                this.dismiss();
                break;
            case R.id.add_tag :
                addTag(mTagName.getText().toString());
                break;
        }
    }

    // WHAT IN THE FUCKING WORLD IS A PASSWORD WATCHER????
    private final TextWatcher passwordWatcher = new TextWatcher() {

        private String previousQuery;

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String query = s.toString().trim().toLowerCase();
            if (query != previousQuery) {
                previousQuery = query;
                if (query.length() > 0) {
                    setSearchView(TagUtils.searchTags(s.toString()));
                    mTaggedContainer.setVisibility(View.GONE);
                    mAddTag.setVisibility(View.VISIBLE);
                    mRecentsContainer.setVisibility(View.GONE);
                } else {
                    mTaggedContainer.setVisibility(View.VISIBLE);
                    mSearchContainer.setVisibility(View.GONE);
                    mAddTag.setVisibility(View.GONE);
                }
            }
        }
    };

    /**
     * @param context
     * @param name Title of the tag
     * @param type Type of the tag
     * @param isRemove User can remove tag from song
     * @return Tag View
     */
    private View getTagView(Context context, final String name, int type, boolean isRemove) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        final View tagView = View.inflate(context,R.layout.tag_layout, null);
        TextView tagName = (TextView) tagView.findViewById(R.id.tag_name);
        ImageView tagRemove = (ImageView) tagView.findViewById(R.id.tag_remove);
        LinearLayout container = (LinearLayout) tagView.findViewById(R.id.container);
        tagView.setLayoutParams(params);
        tagName.setText(name);

        switch (type) {
            case VIEW_TYPE_RECENT:
                container.setBackgroundResource(R.drawable.recent_tag_bg);
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addTag(name);
                    }
                });
                break;
            case VIEW_TYPE_TAGGED:
                container.setBackgroundResource(R.drawable.tagged_bg);
                break;
            case VIEW_TYPE_SEARCH:
                if (mTags != null && mTags.size() > 0 && mTags.contains(name) && !mMultiMode) {
                    container.setBackgroundResource(R.drawable.tagged_bg);
                } else {
                    container.setBackgroundResource(R.drawable.tag_bg);
                    container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addTag(name);
                        }
                    });
                }
        }

        if (isRemove) {
            tagRemove.setVisibility(View.VISIBLE);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTags.remove(name);
                    mTaggedLayout.removeView(tagView);
                    mOnTagChangeListener.onTagDelete(name);
                }
            });
        } else {
            tagRemove.setVisibility(View.GONE);
        }

        return tagView;
    }

    private void setSearchView(List<String> searchResults) {
        mSearchLayout.removeAllViews();
        if (!searchResults.isEmpty()) {
            mSearchContainer.setVisibility(View.VISIBLE);
            mSearchLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < searchResults.size(); i++) {
                mSearchLayout.addView(getTagView(mContext, searchResults.get(i), VIEW_TYPE_SEARCH, false));
            }
        }
    }

    private void addTag(String tag) {
        if (!tag.trim().isEmpty()) {
            mTagName.setText("");
            tag = tag.toLowerCase();
            if (!mMultiMode && mTags != null) {
                // Check tag count
                if (mTags.size() >= 20) {
                    Toast.makeText(mContext,
                        mContext.getApplicationContext().getResources().getString(R.string.tag_max),
                        Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check for prior existance
                if (mTags.contains(tag)) {
                    Toast.makeText(mContext,
                        mContext.getApplicationContext().getResources().getString(R.string.tag_exists),
                        Toast.LENGTH_SHORT).show();
                    return;
                }

                mTags.add(tag);
            }

            mTaggedContainer.setVisibility(View.VISIBLE);
            mOnTagChangeListener.onTagAdded(tag);
            mTaggedLayout.addView(getTagView(mContext, tag, VIEW_TYPE_TAGGED, true), 0);
            mScrollContainer.fullScroll(View.FOCUS_DOWN);
        }
    }
}