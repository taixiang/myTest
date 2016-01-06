package com.overtake.base;

import com.overtake.data.OTDataTask;
import com.overtake.data.OTRequestObserver;
import com.overtake.utils.OTLog;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.widget.Toast;

import cn.ikinder.androidlibs.R;

public abstract class OTFragmentActivity extends FragmentActivity implements OTRequestObserver {

    /**
     * 通讯接口
     *
     * @author 畅彬
     */
    public static interface ICommunication {
        /**
         * 进入
         *
         * @param packet
         */
        void onCome(CommunicationPacket packet);

        /**
         * 返回到
         *
         * @param packet
         */
        void onBack(CommunicationPacket packet);

        /**
         * 点击后退按键，返回false代表不退出当前
         *
         * @return
         */
        boolean onBackPressed();
    }


    /**
     * fragment之间通信协议
     *
     * @author 畅彬
     */
    public static class CommunicationPacket {
        public OTFragment from;// 来源方fragment
        public Class<?> cls;// 消息接收方fragment class
        public Object data;// 数据
        public int code;// 码
        public boolean animated;// 是否使用general动画 note:如果要对推入进行定制动画，该参数设置为false,并且重载CommunicationListener.prepare
        public int containerId;// 容器id
        public CommunicationListener listener;//监听器


        public static interface CommunicationListener {
            /**
             * make preparation for transition, eg: setCustomAnimations
             *
             * @param fragment
             * @param ft
             */
            public void prepare(OTFragment fragment, FragmentTransaction ft);
        }

        public static CommunicationPacket createProtocol(OTFragment from, Class<?> cls, int containerId, Object data, int code, boolean animated) {
            CommunicationPacket protocol = new CommunicationPacket();
            protocol.from = from;
            protocol.cls = cls;
            protocol.containerId = containerId;
            protocol.data = data;
            protocol.code = code;
            protocol.animated = animated;

            return protocol;
        }
    }

    abstract public void pushFragmentToPushStack(Class<?> cls, Object data, Boolean animated);

    abstract public void pushFragmentToPushStack(Class<?> cls, Object data, Boolean animated, int code);

    public void gotoFragment(CommunicationPacket packet) {
        Class<?> cls = packet.cls;
        if (cls == null) {
            return;
        }
        try {
            OTFragment topFragment = getTopFragment();
            if (topFragment != null) {
                topFragment.onLeave();
            } else {
                onStackFilled(packet);
            }

            OTFragment fragment = (OTFragment) cls.newInstance();
            fragment.onCome(packet);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            //custom prepare
            if (packet.listener != null) {
                packet.listener.prepare(fragment, ft);
            }

            if (packet.animated) {
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            }

            if (fragment.isAdded()) {
                ft.show(fragment);
            } else {
                ft.add(packet.containerId, fragment, cls.toString());
            }

            ft.addToBackStack(cls.toString());
            ft.commitAllowingStateLoss();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public OTFragment getFragment(Class<?> cls) {
        OTFragment fragment = (OTFragment) getSupportFragmentManager().findFragmentByTag(cls.toString());
        return fragment;
    }

    public void popToFragment(CommunicationPacket protocol) {
        Class<?> cls = protocol.cls;
        if (cls == null) {
            return;
        }

        String tag = getFragmentTag(protocol);
        FragmentManager fm = getSupportFragmentManager();
        OTFragment fragment = (OTFragment) fm.findFragmentByTag(tag);
        if (fragment != null) {
            fragment.onBack(protocol);
        }

        fm.popBackStackImmediate(tag, 0);
    }

    public void pushFragment(Class<?> cls, int containerId, Object data, Boolean animated, int code) {
        CommunicationPacket packet = new CommunicationPacket();
        packet.cls = cls;
        packet.containerId = containerId;
        packet.data = data;
        packet.animated = animated;
        packet.code = code;

        gotoFragment(packet);
    }

    public void presentFragmentToPushStack(Class<?> cls, int containerId, Object data, Boolean animated, int code) {
        CommunicationPacket packet = new CommunicationPacket();
        packet.cls = cls;
        packet.containerId = containerId;
        packet.data = data;
        packet.animated = animated;
        packet.code = code;

        if (animated) {
            packet.listener = new CommunicationPacket.CommunicationListener() {
                @Override
                public void prepare(OTFragment fragment, FragmentTransaction ft) {
                    ft.setCustomAnimations(R.anim.fragment_slide_up_in, R.anim.fragment_slide_down_out, R.anim.fragment_slide_up_in, R.anim.fragment_slide_down_out);
                }
            };
        }
        gotoFragment(packet);
    }

    public void popTopFragment(CommunicationPacket packet) {
        popTop(packet);
    }

    public void popTopFragment() {
        popTop(null);
    }

    public void popTop(CommunicationPacket packet) {
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        if (cnt > 0)
            getSupportFragmentManager().popBackStackImmediate();

        OTFragment topFragment = getTopFragment();
        if (topFragment != null) {
            topFragment.onBack(packet);
        } else {
            onStackEmpty(packet);
        }
    }

    public void popToRoot() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            popTopFragment();
        }
    }

    @Override
    public void onBackPressed() {
        boolean close = true;

        OTFragment topFragment = getTopFragment();
        if (topFragment != null) {
            close = topFragment.onBackPressed();
        }

        if (close) {
            super.onBackPressed();

            topFragment = getTopFragment();
            if (topFragment != null) {
                topFragment.onBack(null);
            } else {
                onStackEmpty(null);
            }
        }
    }

    protected String getFragmentTag(CommunicationPacket param) {
        StringBuilder sb = new StringBuilder(param.cls.toString());
        return sb.toString();
    }

    public OTFragment getTopFragment() {
        OTFragment topFragment = null;
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        if (cnt > 0) {
            String name = getSupportFragmentManager().getBackStackEntryAt(cnt - 1).getName();
            topFragment = (OTFragment) getSupportFragmentManager().findFragmentByTag(name);
        }
        return topFragment;
    }


    public Toast showToast(String word) {
        Toast toast = Toast.makeText(this, word, Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return toast;
    }

    public Toast showToast(int wordRID) {
        Toast toast = Toast.makeText(this, getString(wordRID), Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return toast;
    }

    public void onStackEmpty(CommunicationPacket packet) {

    }

    public void onStackFilled(CommunicationPacket packet) {

    }

    public void requestSuccessForTask(OTDataTask task) {

    }

    public void requestFailedForTask(OTDataTask task, Throwable error) {

    }

    public void requestDataModifyForTask(OTDataTask task) {

    }

    public void taskAddedToRequestManager(OTDataTask task) {

    }

}
