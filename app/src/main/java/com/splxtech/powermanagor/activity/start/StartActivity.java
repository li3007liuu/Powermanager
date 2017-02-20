package com.splxtech.powermanagor.activity.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.PowerManagerApp;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.activity.login.LoginrActivity;
import com.splxtech.powermanagor.activity.main.MainActivity;
import com.splxtech.powermanagor.engine.MqttService;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.PMUserInfo;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestParameter;

import java.util.ArrayList;

import cn.smssdk.SMSSDK;

/**
 * Created by li300 on 2016/9/17 0017.
 */

public class StartActivity extends AppBaseActivity
{
    private Runnable delaystart_run;
    private PMUserInfo pmUserInfo;
    private boolean loginIn;
    private PowerManagerApp app;
    @Override
    protected void initVariables()
    {
        app = (PowerManagerApp)getApplication();
        pmUserInfo = app.getPmUserInfo();
        SMSSDK.initSDK(this, "174510afd78b8", "386d6c5d518a28ec9f1b398ad3186279");
        loginIn = false;
    }
    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
    }
    @Override
    protected void loadData()
    {
        //检查是否有网络连接
        if(Utils.isNetworkAvailable(this)==true&&pmUserInfo.getId()!=0) {
            login();
        }
        MqttService.actionStart(this);
        delaystart_run=new Runnable()
        {
          @Override
          public void run()
          {

              if(loginIn==false) {
                  Intent startIntent = new Intent(StartActivity.this, LoginrActivity.class);
                  app.setPmUserInfo(pmUserInfo);
                  StartActivity.this.startActivity(startIntent);
                  StartActivity.this.finish();
              }
              else
              {
                  Intent startIntent = new Intent(StartActivity.this, MainActivity.class);
                  app.setPmUserInfo(pmUserInfo);
                  StartActivity.this.startActivity(startIntent);
                  StartActivity.this.finish();
              }
          }
        };
        new Handler().postDelayed(delaystart_run,2000);

    }

    private void login()
    {

        //登录回调函数
        RequestCallback loginCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content,PhalEntity.class);
                if(phalEntity.getCode()==0)
                {
                    loginIn=true;
                }
                else
                {
                    loginIn=false;
                }
            }
        };
            if(pmUserInfo.getEmail().length()>0)
            {
                ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
                RequestParameter rp1 = new RequestParameter("user_email", pmUserInfo.getEmail());
                RequestParameter rp2 = new RequestParameter("user_pass", pmUserInfo.getPass());
                params.add(rp1);
                params.add(rp2);
                RemoteService.getInstance().invoke(this, "Usera_Loginin", params,
                        loginCallback);
            }
            else if(pmUserInfo.getTel().length()>0)
            {
                ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
                RequestParameter rp1 = new RequestParameter("user_tel", pmUserInfo.getTel());
                RequestParameter rp2 = new RequestParameter("user_pass", pmUserInfo.getPass());
                params.add(rp1);
                params.add(rp2);
                RemoteService.getInstance().invoke(this, "Usera_Loginin", params,
                        loginCallback);
            }
            else
            {
                loginIn=false;
            }
    }
}
