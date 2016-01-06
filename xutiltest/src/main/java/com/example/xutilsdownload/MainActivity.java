package com.example.xutilsdownload;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	@ViewInject(R.id.edit_address)
	private EditText edit_address;
	@ViewInject(R.id.download)
	private Button download;
	String url = null;
	DownloadManager downloadManager = null;
	@ViewInject(R.id.button_showList)
	private Button button_showList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
		url = edit_address.getText().toString();
		downloadManager = DownloadService
				.getDownloadManager(getApplicationContext());
	}

	@OnClick(R.id.download)
	public void onClickDownload(View view) {
		String target = "/sdcard/XUtils_download/" + System.currentTimeMillis()
				+ ",yitianyitian.mp3";
		try {
			downloadManager.addNewDownload(url, "", target, true, true, null);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnClick(R.id.button_showList)
	public void onShowListClick(View view) {
		Intent intent = new Intent(MainActivity.this,
				DownloadListActivity.class);
		startActivity(intent);
	}
}
