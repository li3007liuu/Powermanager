package com.splxtech.powermanagor.Base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.splxtech.powermanagor.IMqttService;
import com.splxtech.powermanagor.activity.login.LoginActivity;
import com.splxtech.powermanagor.activity.login.LoginrActivity;
import com.splxtech.powermanagor.engine.AppConstants;
import com.splxtech.powermanagor.engine.MqttService;
import com.splxtech.splxapplib.activity.BaseActivity;
import com.splxtech.splxapplib.net.RequestCallback;

/**
 * Created by li300 on 2016/12/1 0001.
 */

 public abstract class MqttAppBaseActivity extends BaseActivity {
    protected boolean needCallback;

    protected ProgressDialog dlg;

    //全局消息接收器
    public MessageMqttReciver mReciver;
    private IntentFilter mIntentFilter;
    private Intent mServiceIntent;
    private LocalBroadcastManager localBroadcastManager;
    //调用该接口中方法来实现数据发送与主题订阅
    public IMqttService iMqttService;
    //标记是否已经进行了服务绑定与全局消息注册
    private boolean flag;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iMqttService = IMqttService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iMqttService = null;
        }
    };



    public abstract class AbstractRequestCallback implements RequestCallback {

        public abstract void onSuccess(String content);

        public void onFail(String errorMessage) {
            //dlg.dismiss();

           // new AlertDialog.Builder(MqttAppBaseActivity.this).setTitle("出错啦")
           //         .setMessage(errorMessage).setPositiveButton("确定", null)
           //         .show();
        }

        public void onCookieExpired() {
            //dlg.dismiss();

            new AlertDialog.Builder(MqttAppBaseActivity.this)
                    .setTitle("出错啦")
                    .setMessage("Cookie过期，请重新登录")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent(
                                            MqttAppBaseActivity.this,
                                            LoginrActivity.class);
                                    intent.putExtra(AppConstants.NeedCallback,
                                            true);
                                    startActivity(intent);
                                }
                            }).show();
        }
    }

    @Override
    public void onStart()
    {
        flag = false;
        if(mReciver!=null)
        {
            flag = true;
            initMqtt();
            bindService(mServiceIntent,conn,BIND_ABOVE_CLIENT);
        }
        super.onStart();
    }

    @Override
    public void onStop()
    {
        if(flag==true)
        {
            unbindService(conn);

        }
        super.onStop();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        localBroadcastManager.registerReceiver(mReciver,mIntentFilter);
    }
    @Override
    public void onPause()
    {
        localBroadcastManager.unregisterReceiver(mReciver);
        super.onPause();
    }
    public void initMqtt()
    {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mServiceIntent = new Intent(this, MqttService.class);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MqttService.MQTT_RECE_MESSAGE_ACTION);
    }
    public abstract class MessageMqttReciver extends BroadcastReceiver
    {
        @Override
        public abstract void onReceive(Context context, Intent intent);
    }
}
