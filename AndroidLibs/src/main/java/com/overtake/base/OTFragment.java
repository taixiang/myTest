package com.overtake.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.overtake.base.OTFragmentActivity.CommunicationPacket;
import com.overtake.base.OTFragmentActivity.ICommunication;

public abstract class OTFragment extends Fragment implements ICommunication {

    /**
     * 按钮分类
     *
     * @author 畅彬
     */
    public static enum NavigationBarButtonType {
        NavigationBarButtonTypeLeft, // 左侧
        NavigationBarButtonTypeRight;// 右侧
    }

    /**
     * data passed when create
     */
    protected Object mDataIn;
    /**
     * code passed when create
     */
    protected int mCode;


    abstract protected int getLayoutId();

    abstract public Context getApplicationContext();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGrp = (ViewGroup) inflater.inflate(getLayoutId(), null);
        if (viewGrp.getLayoutParams() == null) {
            viewGrp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        OnTouchListener listener = getCustomTouchListener();
        if (listener == null) {
            listener = new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            };
        }
        viewGrp.setOnTouchListener(listener);
        return viewGrp;
    }

    protected OnTouchListener getCustomTouchListener() {
        return null;
    }

    public OTFragmentActivity getContext() {
        return (OTFragmentActivity) getActivity();
    }

    public void onCome(CommunicationPacket packet) {
        mDataIn = packet.data;
        mCode = packet.code;
    }

    /**
     * life cycle
     */
    public CommunicationPacket onLeave() {
        return null;
    }

    public void onBack(CommunicationPacket packet) {
    }

    public boolean onBackPressed() {
        return true;
    }


    /**
     * helpers
     */

    public Toast showToast(String word) {
        if (getContext() != null) {
            return getContext().showToast(word);
        }
        return null;
    }

    public Toast showToast(int wordRID) {
        if (getContext() != null) {
            return getContext().showToast(wordRID);
        }
        return null;
    }

    public void hideKeyboardForCurrentFocus() {
        OTFragmentActivity context = getContext();
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (context.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public void showKeyboardAtView(View view) {
        OTFragmentActivity context = getContext();
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void popTopFragment(OTFragmentActivity.CommunicationPacket packet) {
        OTFragmentActivity context = getContext();
        if (context != null) {
            context.popTopFragment(packet);
        }
    }

    public void popTopFragment() {
        OTFragmentActivity context = getContext();
        if (context != null) {
            context.popTopFragment();
        }
    }

    public void popToRoot() {
        OTFragmentActivity context = getContext();
        if (context != null) {
            context.popToRoot();
        }
    }

    public void pushFragmentToPushStack(Class<?> cls, Object data, Boolean animated) {
        OTFragmentActivity context = getContext();
        if (context != null) {
            context.pushFragmentToPushStack(cls, data, animated);
        }
    }

    public void pushFragmentToPushStack(Class<?> cls, Object data, Boolean animated, int code) {
        OTFragmentActivity context = getContext();
        if (context != null) {
            context.pushFragmentToPushStack(cls, data, animated, code);
        }
    }

}
