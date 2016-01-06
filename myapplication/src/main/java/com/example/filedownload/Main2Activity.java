package com.example.filedownload;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {

    private Button startRecord;
    private Button startPlay;
    private Button stopRecord;
    private Button stopPlay;
    private Button local;

    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;

    private String FileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        FileName += "/audiorecordtest.amr";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(FileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mPlayer = new MediaPlayer();
        //开始录音
        startRecord = (Button)findViewById(R.id.startRecord);
        //绑定监听器
        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mRecorder.start();
            }
        });

        //结束录音
        stopRecord = (Button)findViewById(R.id.stopRecord);
        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                Log.i("Main2Activity",FileName);
            }
        });

        //开始播放
        startPlay = (Button)findViewById(R.id.startPlay);
        //绑定监听器
        startPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mPlayer.setDataSource(FileName);
                    mPlayer.prepare();
                    mPlayer.start();
                    } catch (IOException e) {
                  e.printStackTrace();
                }
            }
        });

        //结束播放
        stopPlay = (Button)findViewById(R.id.stopPlay);
        stopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.release();
                mPlayer = null;
            }
        });

        local = (Button) findViewById(R.id.local);//打开本地目录
        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("》》》》》", getExternalFilesDir("")+"");


                File file = new File("/storage/emulated/0/Android/data/cn.dajiahui.box.launcher/files/");
                if(file.isDirectory()){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setDataAndType(Uri.fromFile(file), "file/*");
//                    startActivity(intent);
                startActivityForResult(intent,1);
                }
            }
        });

    }

}
