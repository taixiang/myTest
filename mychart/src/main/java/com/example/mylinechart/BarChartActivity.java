package com.example.mylinechart;

import java.util.ArrayList;

import com.data.RaceCommon;
import com.data.MyData;
import com.data.XY;
import com.raceView.RaceAxisXView;
import com.raceView.RaceBarView;
import com.view.AxisYView_NormalType;
import com.view.TitleView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class BarChartActivity extends Activity {
	private LinearLayout axisYLayout = null;
	private LinearLayout axisXLayout = null;
	private LinearLayout threndLine_Layout = null;
	private LinearLayout title_layout = null;
	
	private TitleView titleView;
	private RaceBarView raceBar;
	private AxisYView_NormalType axisY_2;
	private RaceAxisXView axisX;
	
	private XY xy = new XY();
	private float oldX = 0;
	private float oldY = 0;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chartactivity);
		
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		RaceCommon.screenWidth = mDisplayMetrics.widthPixels;
		RaceCommon.screenHeight = mDisplayMetrics.heightPixels;
		
		//设置分组柱状图参数
		RaceCommon.raceWidth = RaceCommon.screenWidth *1/3;
		RaceCommon.barWidth = 30;
		RaceCommon.space = 10;
		
		//自定义参数
		setTitle();
		setYName();
		setKey();
		setAxis();
		setData();
		
		//设置图区宽高、内容宽高
		RaceCommon.layoutWidth = RaceCommon.screenWidth *5/2;
		RaceCommon.layoutHeight = RaceCommon.screenHeight * 6/8;
		RaceCommon.viewWidth = RaceCommon.raceWidth*(RaceCommon.xScaleArray.length);
		RaceCommon.viewHeight = RaceCommon.screenHeight *6/8;
		
		init();
		
		//填充
		addView();
	}
	
	/**
	 * 初始化各绘图组件
	 * 包括设置高宽、位置
	 */
	private void init(){
		title_layout = (LinearLayout) findViewById(R.id.titleView);

		axisXLayout = (LinearLayout) findViewById(R.id.axisX);
		LayoutParams xParams = (LayoutParams) axisXLayout.getLayoutParams();
		xParams.height = RaceCommon.layoutHeight;
		xParams.width = RaceCommon.layoutWidth;
		xParams.setMargins(xParams.leftMargin+RaceCommon.YWidth, xParams.topMargin, xParams.rightMargin, xParams.bottomMargin);
		axisXLayout.setLayoutParams(xParams);
		
		axisYLayout = (LinearLayout) findViewById(R.id.axisY);
		LayoutParams yParams = (LayoutParams) axisYLayout.getLayoutParams();
		yParams.height = RaceCommon.layoutHeight;
		yParams.setMargins(yParams.leftMargin, yParams.topMargin, yParams.rightMargin, yParams.bottomMargin + RaceCommon.XHeight);
		axisYLayout.setLayoutParams(yParams);
		
		threndLine_Layout = (LinearLayout) findViewById(R.id.thrend_line);
		LayoutParams hParams = (LayoutParams) threndLine_Layout.getLayoutParams();
		hParams.height = RaceCommon.layoutHeight;
		hParams.width = RaceCommon.layoutWidth;
		hParams.setMargins(hParams.leftMargin+RaceCommon.YWidth, hParams.topMargin, hParams.rightMargin, hParams.bottomMargin + RaceCommon.XHeight);
		threndLine_Layout.setLayoutParams(hParams);
		

		
		//实例化View
//		axisY = new AxisYView_LevelType(this);
		axisY_2 = new AxisYView_NormalType(this);
		axisX = new RaceAxisXView(this);
		raceBar = new RaceBarView(this);
		titleView = new TitleView(this);
	}
	
	private void setData() {
		MyData data1 = new MyData();
		data1.setName("SO2");
		data1.setData( new int[]{55,202,178,158,256,299,  
								87,99,101,213,119,233,  
								95,45,76,68,149,56,  
								47,72,23,192,115,214} );
		data1.setColor(0xff8d77ea);
		
		MyData data2 = new MyData();
		data2.setName("CO");
		data2.setData( new int[]{-1,210,190,-1,240,250,  
								80,85,90,230,100,220,  
								70,30,70,80,130,40,  
								30,80,40,160,100,210} );
		data2.setColor(0xff43ce17);
		
		MyData data3 = new MyData();
		data3.setName("NO2");
		data3.setData( new int[]{55,202,178,158,256,299,  
								87,99,101,213,119,233,  
								95,45,76,68,149,56,  
								47,72,23,192,115,214} );
		data3.setColor(Color.rgb(255, 100, 100));
		
		RaceCommon.DataSeries = new ArrayList<MyData>();
		RaceCommon.DataSeries.add(data1);
		RaceCommon.DataSeries.add(data2);
		RaceCommon.DataSeries.add(data3);
		
	}

	private void setTitle(){
		RaceCommon.title = "废水进水口";
		RaceCommon.secondTitle = "化学需氧量（COD）";
		RaceCommon.titleX = 40;
		RaceCommon.titleY = 70;
		RaceCommon.StitleX =50;
		RaceCommon.StitleY = 110;
		RaceCommon.titleColor = Color.GRAY;
	}
	
	private void setYName(){
		RaceCommon.YName = "浓度（毫克/升）";
		RaceCommon.YName2Left = 40;
		RaceCommon.YName2Top = 450;
		RaceCommon.titleColor = Color.GRAY;
	}
	
	private void setKey(){
		//设置图例参数
		RaceCommon.keyWidth = 30;
		RaceCommon.keyHeight = 10;
		RaceCommon.keyToLeft = 300;
		RaceCommon.keyToTop = 80;
		RaceCommon.keyToNext = 80;
		RaceCommon.keyTextPadding = 5;
	}
	
	private void setAxis(){
		//设置轴参数
		RaceCommon.xScaleArray = new String[]{"0","100","200","300","400","500","600","700","800","900","1000","1100","1200","1300","1400","1500","1600","1700","1800","1900","2000","2100","2200","2300"};
//		RaceCommon.xScaleColor = Color.YELLOW;
		
		//yScaleArray需要比levelName和color多出一个数
		RaceCommon.yScaleArray = new int[]{23,25,50,100,200,300,500};
		RaceCommon.levelName = new String[]{"优","良","轻度","中度","重度","严重"};
		RaceCommon.yScaleColors = new int[]{0xff00ff00,0xffffff00,0xffffa500,0xffff4500,0xffdc143c,0xffa52a2a};
	}
	
	private void addView(){
		int width=0;
//		if(mp==false)
//			width=RaceCommon.screenWidth*7/8+10;
//		else
			width=RaceCommon.viewWidth;
			
		//设定初始定位Y坐标
		xy.y = RaceCommon.viewHeight - RaceCommon.layoutHeight;
		
		raceBar.initValue(RaceCommon.viewHeight);//传入宽、高、是否在折线图上显示数据
		raceBar.scrollTo(0, xy.y);
		
		axisY_2.initValue(RaceCommon.viewHeight);//传入高度
		axisY_2.scrollTo(0, xy.y);
		
		axisX.initValue(width, RaceCommon.viewHeight);//传入高度
		axisX.scrollTo(0, xy.y);
		
		axisYLayout.removeAllViews();
		axisYLayout.addView(axisY_2);
		
		axisXLayout.removeAllViews();
		axisXLayout.addView(axisX);
		
		threndLine_Layout.removeAllViews();
		threndLine_Layout.addView(raceBar);
		
		title_layout.removeAllViews();
		title_layout.addView(titleView);
		
		//监听滑动事件
		raceBar.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					oldX = event.getX();
					oldY = event.getY();
				}
				if(event.getAction() == MotionEvent.ACTION_MOVE){
					parseXY( xy.x+=oldX-event.getX() , xy.y+=oldY-event.getY() , raceBar.getWidth() , raceBar.getHeight() , threndLine_Layout);
					System.out.println("x="+xy.x+"  y="+xy.y);
					raceBar.scrollTo(xy.x, xy.y);
					axisY_2.scrollTo(0, xy.y);
					axisX.scrollTo(xy.x, RaceCommon.viewHeight - RaceCommon.layoutHeight);
					oldX = event.getX();
					oldY = event.getY();
				}
				return true;
			}
		});
	}
	
	protected void parseXY(float x,float y,int width,int height,LinearLayout parent) {
		int parentWidth = parent.getWidth();
		int parentHeight = parent.getHeight();
		if(x<0)
			xy.x = 0;
		else if(x > width-parentWidth)
			xy.x = width-parentWidth;
		else
			xy.x = (int) x;
		
		if(y<0)
			xy.y = 0;
		else if(y > height-parentHeight)
			xy.y = height-parentHeight;
		else
			xy.y = (int) y;
		
		//初步防抖
		if(width<=parentWidth)
			xy.x = 0;
		if(height<=parentHeight)
			xy.y = 0;
	}
}
