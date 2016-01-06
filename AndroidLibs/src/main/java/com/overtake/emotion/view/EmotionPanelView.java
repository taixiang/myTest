package com.overtake.emotion.view;

import java.util.HashMap;

import org.taptwo.android.widget.ViewFlow;
import org.taptwo.android.widget.ViewFlow.ViewSwitchListener;

import com.overtake.emotion.EmojiDrawableUtil;
import com.overtake.emotion.SmileArray;
import com.overtake.emotion.view.EmotionGridView.EmotionType;
import com.overtake.utils.LocalDisplay;


import cn.ikinder.androidlibs.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class EmotionPanelView extends LinearLayout {

    public interface OnEmotionClickHandler {
        public void onClickSmile(EmotionType type, int pos, String s);
    }


    private static int displayItemSize = LocalDisplay.getScaledWidthPixelsByDP(38);

    private RadioGroup radioGroup = null;

    private DotView mDotView = null;

    private RadioButton tabDefSmile = null;
    private RadioButton tabEmojiSmile = null;

    private int selectedTabId = 0;

    private OnEmotionClickHandler mOnEmotionClickHandler = null;
    private ViewFlow mPagedView;
    private EmotionViewDataWrapper mEmojiViewDataWrapper;
    private EmotionViewDataWrapper mSysEmotionViewDataWrapper;
    private EmotionViewDataWrapper mCurrentEmotionViewDataWrapper;

    private ImageAdapter mAdapter;

    public EmotionPanelView(Context context) {
        super(context);
        setUpView();
    }

    public EmotionPanelView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        setUpView();
    }

    private void setUpView() {
        inflate(getContext(), R.layout.emotion_smiley_panel, this);

        radioGroup = (RadioGroup) findViewById(R.id.smiley_panel_btn_group);
        tabDefSmile = (RadioButton) findViewById(R.id.smiley_panel_def_btn);
        tabEmojiSmile = (RadioButton) findViewById(R.id.smiley_panel_emoji_btn);
        mDotView = (DotView) findViewById(R.id.smiley_panel_dot);
        radioGroup.setOnCheckedChangeListener(new Controller());
        mPagedView = (ViewFlow) findViewById(R.id.smile_page);

        initDefaultSmiles();
        initEmojiSmiles();

        selectedTabId = R.id.smiley_panel_def_btn;
        setToSelected(tabDefSmile);
        setToUnelected(tabEmojiSmile);
        mCurrentEmotionViewDataWrapper = mSysEmotionViewDataWrapper;
        mDotView.init(mCurrentEmotionViewDataWrapper.totalPage);
        mDotView.setSelected(mCurrentEmotionViewDataWrapper.currentPage);

        int itemSize = LocalDisplay.getScaledWidthPixelsByDP(38) * 4;
        mPagedView.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(-1, itemSize));
        mAdapter = new ImageAdapter(getContext());
        mPagedView.setAdapter(mAdapter);
        mPagedView.setOnViewSwitchListener(new ViewSwitchListener() {

            @Override
            public void onSwitched(View view, int position) {
                mCurrentEmotionViewDataWrapper.currentPage = position;
                mDotView.setSelected(position);
            }

        });
    }

    public static void setDisplayItemWidth(int displayItemWidth) {
        displayItemSize = displayItemWidth;
    }

    private class Controller implements OnCheckedChangeListener {
        public final void onCheckedChanged(RadioGroup group, int checkedId) {
            if (selectedTabId == checkedId)
                return;
            selectedTabId = checkedId;
            if (checkedId == R.id.smiley_panel_def_btn) {
                setToSelected(tabDefSmile);
                setToUnelected(tabEmojiSmile);
                mCurrentEmotionViewDataWrapper = mSysEmotionViewDataWrapper;
            } else if (checkedId == R.id.smiley_panel_emoji_btn) {
                setToSelected(tabEmojiSmile);
                setToUnelected(tabDefSmile);
                mCurrentEmotionViewDataWrapper = mEmojiViewDataWrapper;
            }
            mPagedView.setSelection(mCurrentEmotionViewDataWrapper.currentPage);
            mAdapter.notifyDataSetChanged();
            mDotView.init(mCurrentEmotionViewDataWrapper.totalPage);
            mDotView.setSelected(mCurrentEmotionViewDataWrapper.currentPage);
        }
    }

    private void setToSelected(RadioButton button) {
        MarginLayoutParams lpDef = (MarginLayoutParams) button.getLayoutParams();
        lpDef.topMargin = 0;
        lpDef.bottomMargin = 5;
        button.setLayoutParams(lpDef);
        button.setTextColor(Color.parseColor("#000000"));
    }

    private void setToUnelected(RadioButton button) {
        MarginLayoutParams lpEmoji = (MarginLayoutParams) button.getLayoutParams();
        lpEmoji.topMargin = 2;
        lpEmoji.bottomMargin = 0;
        button.setLayoutParams(lpEmoji);
        button.setTextColor(Color.parseColor("#FF000000"));
    }

    private void initEmojiSmiles() {
        int total = EmojiDrawableUtil.getCodeList(getContext()).length;
        mEmojiViewDataWrapper = new EmotionViewDataWrapper(EmotionType.emoji, total, displayItemSize);
    }

    private void initDefaultSmiles() {
        int total = SmileArray.getSmile(getContext()).length;
        mSysEmotionViewDataWrapper = new EmotionViewDataWrapper(EmotionType.system, total, displayItemSize);
    }

    public final void setOnclickSmileListener(OnEmotionClickHandler bm) {
        mOnEmotionClickHandler = bm;
        mAdapter.notifyDataSetChanged();
    }

    private class EmotionViewDataWrapper {

        public HashMap<Integer, EmotionGridView> viewCacheList = new HashMap<Integer, EmotionGridView>();
        public int total;
        public int currentPage = 0;
        public EmotionType type;
        public int itemsPerPage;
        public int totalPage;
        public int numColumn;

        public EmotionViewDataWrapper(EmotionType type, int total, int size) {
            this.type = type;
            this.total = total;
            numColumn = LocalDisplay.SCREEN_WIDTH_PIXELS / size;

            itemsPerPage = numColumn * 4;
            totalPage = (int) Math.ceil(total / (itemsPerPage - 1));
        }
    }

    public class ImageAdapter extends BaseAdapter {

        public ImageAdapter(Context context) {

        }

        @Override
        public int getCount() {
            return mCurrentEmotionViewDataWrapper.totalPage;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EmotionViewDataWrapper wrapper = mCurrentEmotionViewDataWrapper;
            HashMap<Integer, EmotionGridView> list = wrapper.viewCacheList;
            if (!list.containsKey(position)) {
                EmotionGridView gridView = (EmotionGridView) EmotionPanelView.inflate(getContext(), R.layout.emotion_smiley_grid, null);
                gridView.setParameters(wrapper.type, position, wrapper.total, wrapper.itemsPerPage, wrapper.totalPage, wrapper.numColumn);
                gridView.setOnClickSmile(mOnEmotionClickHandler);
                gridView.setLayoutParams(new LayoutParams(-1, -1));
                list.put(position, gridView);
            }
            list.get(position).setOnClickSmile(mOnEmotionClickHandler);
            return list.get(position);
        }

    }

}
