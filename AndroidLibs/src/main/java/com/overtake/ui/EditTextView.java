package com.overtake.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

import com.overtake.emotion.SpannableStringUtil;

/**
 * Created by boelroy on 14-2-27.
 */
public class EditTextView extends EditText {
    private InputConnection inputConnection = null;

    public EditTextView(Context context){
        super(context);
    }

    public EditTextView(Context context, AttributeSet attributeset){
        super(context, attributeset);
    }

    public EditTextView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
    }

    public final InputConnection getInputConnection(){
        return inputConnection;
    }

    public final void addChar(String s){
        int start = getSelectionStart();
        int end = getSelectionEnd();
        StringBuffer stringBuffer = new StringBuffer(getText());
        setText((new StringBuilder()).append(stringBuffer.substring(0, start)).append(s).append(stringBuffer.substring(end, stringBuffer.length())).toString());

        setText(SpannableStringUtil.showSpannableText(this));
        setSelection(start + s.length());
    }

    public InputConnection onCreateInputConnection(EditorInfo editorinfo){
        inputConnection = super.onCreateInputConnection(editorinfo);
        return inputConnection;
    }

    public boolean onTextContextItem(int i){
        boolean flag = super.onTextContextMenuItem(i);
        if(i == 0x1020022){
            int j = getSelectionStart();
            setText(SpannableStringUtil.showSpannableText(this));
            setSelection(j);
        }

       return flag;
    }

}
