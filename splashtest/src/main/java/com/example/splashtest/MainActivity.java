package com.example.splashtest;

import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RelativeLayout;

public class MainActivity extends FragmentActivity {

    private RelativeLayout frameLayout;
    private SplashView splashView;
    private BlankFragment fragment ;
    FragmentManager fm;
    FragmentTransaction ft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        fragment = new BlankFragment();
        // 帧布局
        frameLayout = (RelativeLayout) findViewById(R.id.container);


        // 先添加ContentView
        ContentView contentView=new ContentView(this);

      //  frameLayout.addView( fragment.getView());
        // 再添加SplashView
        splashView = new SplashView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        splashView.setLayoutParams(params);
        frameLayout.addView(splashView);
        setContentView(frameLayout);
        // 开启Splash动画 --- 模拟后台加载数据
        startLoad();
        startView();
    }

    private Handler handler=new Handler();
    private void startLoad() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 数据加载完毕执行后面的动画 -- 让ContentView显示
                splashView.splashAndDisapper();
            }
        }, 3000);
    }


    private void startView() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 数据加载完毕执行后面的动画 -- 让ContentView显示
                ft.add(R.id.container,fragment);
                ft.commit();
            }
        }, 4500);
    }

}
