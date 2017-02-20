package com.splxtech.powermanagor.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;

/**
 * Created by li300 on 2016/9/19 0019.
 */

public class RegisterMessageActivity extends AppBaseActivity
{
    Button returnButton;
    Button nextButton;
    Button sendButton;
    TextView textView;
    EditText messEdit;
    TimeCount timeCount;
    String mobile;
    @Override
    protected void initVariables()
    {
        Intent intent = this.getIntent();
        mobile = intent.getStringExtra("mobile");
    }
    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_register3);
        returnButton = (Button)findViewById(R.id.register3_button_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLogin();
            }
        });
        nextButton = (Button)findViewById(R.id.register3_button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRegisterFinish();
            }
        });
        sendButton = (Button)findViewById(R.id.register3_button_resend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeCount.start();
            }
        });
        messEdit = (EditText)findViewById(R.id.register3_edit);
        textView = (TextView)findViewById(R.id.register3_text);
        textView.setText("请输入"+mobile+"收到的短信验证码");
    }
    @Override
    protected void loadData() {
        timeCount = new TimeCount(60000,1000);
        timeCount.start();
    }

    //跳转至登录
    private void gotoLogin()
    {
        Intent intent = new Intent(RegisterMessageActivity.this,LoginrActivity.class);
        startActivity(intent);
        RegisterMessageActivity.this.finish();
    }

    private void gotoRegisterFinish()
    {
        Intent intent = new Intent(RegisterMessageActivity.this,RegisterFinishActivity.class);
        intent.putExtra("mobile",mobile);
        startActivity(intent);
        RegisterMessageActivity.this.finish();
    }
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished)
        {
            sendButton.setEnabled(false);
            sendButton.setText("重新获取验证码("+millisUntilFinished / 1000+")");
        }

        @Override
        public void onFinish()
        {
            sendButton.setText("重新获取验证码");
            sendButton.setEnabled(true);
        }
    }

}
