package com.example.meituan;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.zdp.aseo.content.AseoZdpAseo;

public class MainActivity extends Activity implements MyScrollView.OnScrollListener{

    private MyScrollView myScrollView;
    private LinearLayout mBuyLayout;
    private WindowManager mWindowManager;

    private int screenWidth;//手机屏幕宽度
    private static View suspendView;//悬浮框View
    private static WindowManager.LayoutParams suspendLayoutParams;//悬浮框的参数

    private int buyLayoutHeight;//购买布局的高度
    private int myScrollViewTop;//myScrollView与其父容器布局的顶部距离
    private int buyLayoutTop;	//购买布局与其父容器布局的顶部距离


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myScrollView = (MyScrollView) findViewById(R.id.scrollView);//自定义ScrollView组件
        myScrollView.setOnScrollListener(this);						//设置滚动事件

        mBuyLayout = (LinearLayout) findViewById(R.id.buy);			//要滚动到顶的组件
    //    AseoZdpAseo.init(this, AseoZdpAseo.SCREEN_TYPE);
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();//获得屏幕的宽
    }

    /**
     * 窗口有焦点的时候，即所有的布局绘制完毕的时候，我们来获取
     * 购买布局的高度和myScrollView组件距离父容器布局顶部的位置
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
     //   AseoZdpAseo.initFinalTimer(this, AseoZdpAseo.BOTH_TYPE);
        if(hasFocus){
            buyLayoutHeight = mBuyLayout.getHeight();//买布局的高
            buyLayoutTop = mBuyLayout.getTop();		//获得买布局距父容器的顶部边距--292px

            myScrollViewTop = myScrollView.getTop();//获得自定义滚动布局距父容器的顶部边距--90px
            //Log.d("mylog","buyLayoutHeight:"+buyLayoutHeight);
            Log.d("mylog", "-------------buyLayoutTop:" + buyLayoutTop + ",myScrollViewTop:" + myScrollViewTop);
        }
    }



    @Override
    public void onScroll(int scrollY) {
        if(scrollY >= buyLayoutTop){
            if(suspendView == null){
                showSuspend();//显示悬浮框
                Log.d("mylog","显示scrollY："+scrollY+"------------buyLayoutTop:"+buyLayoutTop+",myScrollViewTop:"+myScrollViewTop);
            }
        }else {
            if(suspendView != null){
                removeSuspend();//去掉悬浮框
                Log.d("mylog","去掉scrollY："+scrollY+"------------buyLayoutTop:"+buyLayoutTop+",buyLayoutHeight:"+buyLayoutHeight);
            }
        }
    }

    /**
     * 显示购买的悬浮框
     */
    private void showSuspend(){
        if(suspendView == null){
            suspendView = LayoutInflater.from(this).inflate(R.layout.buy_layout, null);
            if(suspendLayoutParams == null){
                suspendLayoutParams = new WindowManager.LayoutParams();//悬浮框布局参数
                //悬浮框类型2003和2002的区别就在于2003类型的View比2002类型的还要top，能显示在系统下拉状态栏之上！
                suspendLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE; //悬浮框类型TYPE_PHONE2002
                suspendLayoutParams.format = PixelFormat.RGBA_8888; //颜色？
                suspendLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;  	//不获得触屏事件与焦点
                suspendLayoutParams.gravity = Gravity.TOP;  	//顶部对齐
                suspendLayoutParams.width = screenWidth;		//宽
                suspendLayoutParams.height = buyLayoutHeight;  	//高
                suspendLayoutParams.x = 0;
                suspendLayoutParams.y = myScrollViewTop;  		//位置坐标，顶部与滚动组件的顶部同高
            }
        }

        mWindowManager.addView(suspendView, suspendLayoutParams);//把当前布局添加到窗口
    }


    /**
     * 移除购买的悬浮框
     */
    private void removeSuspend(){
        if(suspendView != null){
            mWindowManager.removeView(suspendView);
            suspendView = null;
        }
    }



}
