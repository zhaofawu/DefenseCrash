package com.android.tony.defense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                throw new RuntimeException("测试崩溃click");
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                throw new IllegalArgumentException("Thread崩溃");
            }
        }).start();
        throw new NullPointerException("OnCreate测试崩溃生命周期");
    }

    @Override
    protected void onResume() {
        super.onResume();
        throw new NullPointerException("onResume测试崩溃生命周期");
    }

    @Override
    protected void onPause() {
        super.onPause();
        throw new NullPointerException("onPause测试崩溃生命周期");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        throw new NullPointerException("onDestroy测试崩溃生命周期");
    }
}
