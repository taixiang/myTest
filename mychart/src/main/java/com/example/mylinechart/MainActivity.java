package com.example.mylinechart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener{

	Button btn1,btn2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		btn1 = (Button)findViewById(R.id.btn1);
		btn2 = (Button)findViewById(R.id.btn2);
		
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		Intent intent ;
		switch(v.getId()){
		case R.id.btn1:
			intent = new Intent(this,LineChartActivity.class);
			startActivity(intent);
			break;
		case R.id.btn2:
			intent = new Intent(this,BarChartActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		
	}
	
}
