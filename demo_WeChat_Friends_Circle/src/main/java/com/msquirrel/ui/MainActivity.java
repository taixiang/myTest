package com.msquirrel.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.msquirrel.adapter.WeChatAdapter;
import com.msquirrel.bean.UserImgs;
import com.msquirrel.bean.UserInfo;

public class MainActivity extends Activity implements WeChatAdapter.ICommentListener{

	private ListView mListView = null;
	private RelativeLayout mCommentView,container;
	private EditText editText;
	InputMethodManager imm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mListView = (ListView) findViewById(R.id.lv_main);
		mCommentView = (RelativeLayout) findViewById(R.id.comment_view);
		editText = (EditText) findViewById(R.id.edit);
		container = (RelativeLayout) findViewById(R.id.container);
		mListView.addHeaderView(getheadView());
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// mListView.setDividerHeight(0);
		setData();
//		mListView.scrollTo(0,400);

	}

	private void setData() {
		List<UserInfo> mList = new ArrayList<>();
		UserInfo mUserInfo = new UserInfo();
		UserImgs m = new UserImgs();
		m.setUrls("http://m1.img.srcdd.com/farm2/d/2011/0817/01/5A461954F44D8DC67A17838AA356FE4B_S64_64_64.JPEG");
		mUserInfo.getUi().add(m);
		mList.add(mUserInfo);
		//---------------------------------------------
		UserInfo mUserInfo2 = new UserInfo();
		UserImgs m2 = new UserImgs();
		m2.setUrls("http://m1.img.srcdd.com/farm2/d/2011/0817/01/5A461954F44D8DC67A17838AA356FE4B_S64_64_64.JPEG");
		mUserInfo2.getUi().add(m2);
		UserImgs m21 = new UserImgs();
		m21.setUrls("http://m1.img.srcdd.com/farm2/d/2011/0817/01/5A461954F44D8DC67A17838AA356FE4B_S64_64_64.JPEG");
		mUserInfo2.getUi().add(m21);
		mList.add(mUserInfo2);

		mList.add(mUserInfo2);
		mList.add(mUserInfo2);
		mList.add(mUserInfo2);

		WeChatAdapter mWeChatAdapter = new WeChatAdapter(this);
		mWeChatAdapter.setiCommentListener(this);
		mWeChatAdapter.setData(mList);
		mListView.setAdapter(mWeChatAdapter);
	}

	private View getheadView() {
		View view = LayoutInflater.from(MainActivity.this).inflate(
				R.layout.friends_circle_head, null);
		return view;
	}

	@Override
	public void comment() {
		mCommentView.setVisibility(View.VISIBLE);
		editText.requestFocus();

		imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

		editText.getText().toString();

	//	getPosition(mListView);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			if(isShouldHideInput(mCommentView,ev)){
				if(imm!= null){
					imm.hideSoftInputFromWindow(mCommentView.getWindowToken(),0);
					mCommentView.setVisibility(View.GONE);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		if(getWindow().superDispatchTouchEvent(ev)){
			return true;
		}
		return onTouchEvent(ev);
	}

	private void getPosition(View v){
		if(v != null ){
			int[] leftTop = {0,0};
			editText.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			v.scrollTo(0,top);
		}
	}

	public boolean isShouldHideInput(View v,MotionEvent event){
		if(v != null && v instanceof RelativeLayout){
			int[] leftTop = {0,0};
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top +v.getHeight();
			int right = left + v.getWidth();

			if(event.getX() > left && event.getX() < right && event.getY()>top &&event.getY()<bottom){
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			}else {
				return true;
			}
		}
		return false;
	}

}
