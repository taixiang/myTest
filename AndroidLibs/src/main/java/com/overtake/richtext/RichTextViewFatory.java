package com.overtake.richtext;

import java.util.ArrayList;

import org.json.JSONObject;

import com.overtake.emotion.EmojiUtil;
import com.overtake.emotion.SmileArray;
import com.overtake.richtext.view.BaseRichTextView;
import com.overtake.richtext.view.EmotionView;
import com.overtake.richtext.view.IconImageView;
import com.overtake.richtext.view.LocationView;
import com.overtake.richtext.view.RawTextView;
import com.overtake.utils.OTLog;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

public class RichTextViewFatory {

    private static final boolean DEBUG = false;
    private static final String TAG = "RichTextViewFatory";

    public static BaseRichTextView crateFromJSONObject(JSONObject jsonObject) {
        int type = jsonObject.optInt("t");
        BaseRichTextView view = null;
        switch (type) {
            case BaseRichTextView.TYPE_EMOTION:
                view = new EmotionView();
                break;

            case BaseRichTextView.TYPE_TEXT:
                view = new RawTextView();
                break;

            case BaseRichTextView.TYPE_IMAGE:
                view = new IconImageView();
                break;
            case BaseRichTextView.TYPE_LOCATION:
                view = new LocationView();
                break;

            default:
                break;
        }

        if (null != view)
            view.initFromJSONObject(jsonObject);
        return view;
    }

    public static ArrayList<BaseRichTextView> createViewListFromRawString(String srcText, Context context) {
        ArrayList<BaseRichTextView> arrayList = new ArrayList<BaseRichTextView>();
        boolean found = false;
        String[] smilesWithBracket = SmileArray.getSmileWithBracket(context);
        String[] smiles = SmileArray.getSmile(context);

        for (int i = 0; i < smilesWithBracket.length; i++) {
            String smile = smilesWithBracket[i];

            int index = srcText.indexOf(smile);
            if (index != -1) {

                String preText = srcText.substring(0, index);
                String postText = srcText.substring(index + smile.length());

                if (preText != null && preText.length() > 0) {
                    ArrayList<BaseRichTextView> pre = createViewListFromRawString(preText, context);
                    if (pre != null && pre.size() > 0) {
                        pre.addAll(arrayList);
                        arrayList = pre;
                    }
                }

                BaseRichTextView info = new EmotionView();
                info.setContent(smiles[i]);
                info.setSubType(BaseRichTextView.EMOTION_SMILE);
                arrayList.add(info);

                if (postText != null && postText.length() > 0) {
                    ArrayList<BaseRichTextView> post = createViewListFromRawString(postText, context);
                    if (post != null && post.size() > 0) {
                        arrayList.addAll(post);
                    }
                }

                found = true;
                break;
            }
        }

        if (!found) {
            ArrayList<BaseRichTextView> list = processEmojiForCreateInfos(srcText);
            arrayList.addAll(list);
        }

        return arrayList;
    }

    private static ArrayList<BaseRichTextView> processEmojiForCreateInfos(String richText) {
        ArrayList<BaseRichTextView> list = new ArrayList<BaseRichTextView>();
        if (!TextUtils.isEmpty(richText)) {
            int start = 0;
            int i = start;
            int nextEmoji = -1;
            int length = richText.length();
            while (i < length) {
                char c = richText.charAt(i);
                nextEmoji = EmojiUtil.getEmojiPos(c);

                if (DEBUG)
                    OTLog.i(TAG, nextEmoji);

                if (nextEmoji >= 0) {

                    String content = richText.substring(start, i);
                    BaseRichTextView info = new RawTextView();
                    info.setContent(content);
                    list.add(info);

                    BaseRichTextView emoji = new EmotionView();
                    emoji.setSubType(BaseRichTextView.EMOTION_EMOJI);
                    emoji.setContent(String.valueOf(c));
                    list.add(emoji);

                    start = i + 1;
                    nextEmoji = -1;
                } else if (i == length - 1 && start <= i) {
                    String content = richText.substring(start, i + 1);
                    BaseRichTextView info = new RawTextView();
                    info.setContent(content);
                    list.add(info);
                }
                i++;
            }
        }
        return list;
    }
}
