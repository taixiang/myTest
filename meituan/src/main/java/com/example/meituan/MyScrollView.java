package com.example.meituan;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by taixiang on 2015/10/23.
 */
public class MyScrollView extends ScrollView {

    /**
     * 滚动的回调接口，用于在滑动当前scroll组件时把滚动的值回传给Activity进行判断与处理
     * @author xiaanming
     *
     */
    public interface OnScrollListener{
        /**
         * 回调方法， 返回MyScrollView滑动的Y方向距离
         * @param scrollY
         */
        public void onScroll(int scrollY);
    }

    private OnScrollListener onScrollListener;//通过setOnScrollListener()方法把Activity中的监听对象传入本类中使用
    /**
     * 主要是用在用户手指离开MyScrollView，MyScrollView还在继续滑动，我们用来保存Y的距离，然后做比较
     */
    private int lastScrollY;


    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 设置滚动接口
     */
    public void setOnScrollListener(OnScrollListener onScrollListener){
        this.onScrollListener = onScrollListener;
    }

    /**
     * 用于用户手指离开MyScrollView的时候获取MyScrollView滚动的Y距离，然后回调给onScroll方法中
     */
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            int scrollY = MyScrollView.this.getScrollY();//获得当前滚动到的y坐标值

            //此时的距离和记录下的距离不相等，再隔5毫秒给handler发送消息
            if(lastScrollY != scrollY){
                lastScrollY = scrollY;//把当前滚到的y坐标值赋给lastScrollY变量，以便下次比较
                handler.sendMessageDelayed(handler.obtainMessage(), 5);
            }
            if(onScrollListener != null){
                onScrollListener.onScroll(scrollY);//把获得的当前滚动值传入，执行监听器中的处理
            }

        };

    };

    /**
     * 重写onTouchEvent：
     * 当用户的手在MyScrollView上面的时候，直接将MyScrollView滑动的Y方向距离回调给onScroll方法中；
     * 当用户抬起手的时候，MyScrollView可能还在滑动，所以当用户抬起手我们隔5毫秒给handler发送消息，
     * 在handler中处理MyScrollView滑动的距离
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(onScrollListener != null){//如果监听器已创建，则在有触屏事件时把滚动组件滚动到的y坐标记录到lastScrollY,并传给监听器
            onScrollListener.onScroll(lastScrollY = this.getScrollY());//一个赋值语句，一个事件处理语句
        }
        switch(ev.getAction()){
            case MotionEvent.ACTION_UP:
                handler.sendMessageDelayed(handler.obtainMessage(), 5); // 手离开触屏,延时5毫秒发消息给handler
                break;
        }
        return super.onTouchEvent(ev);
    }




}
