package com.example.heyukun.serialportdemo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.example.heyukun.serialportdemo.serial.RevType;
import com.example.heyukun.serialportdemo.serial.SerialHelper;
import com.example.heyukun.serialportdemo.serial.SerialRevCallBack;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SerialRevCallBack {
    private AppCompatTextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTv = findViewById(R.id.tv);

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_forward).setOnClickListener(this);
        findViewById(R.id.btn_led_black).setOnClickListener(this);
        findViewById(R.id.btn_led_white).setOnClickListener(this);
        findViewById(R.id.btn_led_red).setOnClickListener(this);
        findViewById(R.id.btn_led_yellow).setOnClickListener(this);
        findViewById(R.id.btn_led_green).setOnClickListener(this);
        findViewById(R.id.btn_led_purple).setOnClickListener(this);
        findViewById(R.id.btn_led_blue).setOnClickListener(this);
        findViewById(R.id.btn_led_cyan).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);

        SerialHelper.getInstance().setOnSerialRevListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                SerialHelper.getInstance().turnBack();
                break;
            case R.id.btn_forward:
                SerialHelper.getInstance().turnForward();
                break;
            case R.id.btn_led_black:
                SerialHelper.getInstance().changeLedColor(SerialHelper.LedColor.BLACK);
                break;
            case R.id.btn_led_white:
                SerialHelper.getInstance().changeLedColor(SerialHelper.LedColor.WHITE);
                break;
            case R.id.btn_led_red:
                SerialHelper.getInstance().changeLedColor(SerialHelper.LedColor.RED);
                break;
            case R.id.btn_led_yellow:
                SerialHelper.getInstance().changeLedColor(SerialHelper.LedColor.YELLOW);
                break;
            case R.id.btn_led_green:
                SerialHelper.getInstance().changeLedColor(SerialHelper.LedColor.GREEN);
                break;
            case R.id.btn_led_purple:
                SerialHelper.getInstance().changeLedColor(SerialHelper.LedColor.PURPLE);
                break;
            case R.id.btn_led_blue:
                SerialHelper.getInstance().changeLedColor(SerialHelper.LedColor.BLUE);
                break;
            case R.id.btn_led_cyan:
                SerialHelper.getInstance().changeLedColor(SerialHelper.LedColor.CYAN);
                break;
            case R.id.btn_stop:
                SerialHelper.getInstance().turnStop();
                break;
            default:
                break;
        }
    }

    @Override
    public void OnSuccess(final RevType revType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (revType == RevType.CRASH) {
                    mTv.setText("碰撞");
                }
                if (revType == RevType.INFRARED) {
                    mTv.setText("红外");
                }
                return;
            }
        });
    }
}
