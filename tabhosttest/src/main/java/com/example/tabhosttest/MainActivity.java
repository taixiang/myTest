package com.example.tabhosttest;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements BlankFragment.OnBtnClick{
    private FragmentTabHost mTabHost;

    private Class fragmentArray[] = {BlankFragment.class,BlankFragment2.class,BlankFragment3.class,BlankFragment.class,BlankFragment2.class};

    private String mTextViewArray[] = {"1","2","3","4","5"};

    private int mImageViewArray[] = {R.drawable.main_icon_tabbar_desktop_pressed,R.drawable.main_icon_tabbar_contact_pressed,R.drawable.main_icon_tabbar_course_pressed,R.drawable.main_icon_tabbar_home_pressed,R.drawable.main_icon_tabbar_portal_pressed};
    TabItemView[] tabItemViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init(){
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realContent);
        mTabHost.getTabWidget().setDividerDrawable(null);
//        mTabHost.setBackgroundResource(R.drawable.global_background_tab_bar);

        int count = fragmentArray.length;
        tabItemViews = new TabItemView[count];
        for(int i=0;i<count;i++){
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
  //          mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.mipmap.ic_launcher);
        }
    }

    //给tab按钮设置文字和图标
    private View getTabItemView(int index){
        View view = LayoutInflater.from(this).inflate(R.layout.tab_iten_view,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(mTextViewArray[index]);

//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        return view;
    }


    @Override
    public void btnClick() {
        mTabHost.setCurrentTab(1);
    }
}
