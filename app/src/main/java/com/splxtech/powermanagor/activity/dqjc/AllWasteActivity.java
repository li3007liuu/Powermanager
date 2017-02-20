package com.splxtech.powermanagor.activity.dqjc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.IMqttService;
import com.splxtech.powermanagor.PowerManagerApp;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.activity.login.LoginActivity;
import com.splxtech.powermanagor.engine.AppConstants;
import com.splxtech.powermanagor.engine.MqttService;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.AppWasteAll;
import com.splxtech.powermanagor.entity.DayWaste;
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.powermanagor.entity.YunReData;
import com.splxtech.powermanagor.entity.YunReceData;
import com.splxtech.powermanagor.entity.YunService;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestManager;
import com.splxtech.splxapplib.net.RequestParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li300 on 2016/11/7 0007.
 */

public class AllWasteActivity extends FragmentActivity {
    public ViewPager mViewPager;
    private ArrayList<Fragment> mDatas;
    private ArrayList<String> mTables;
    private Button nextButton;
    private Button returnButton;
    private TextView titleText;
    private int mCurrentPageIndex;
    private String title;
    //private ArrayList<Appliance> applianceList;
    private ElectricityMeter device;
    private FragmentPagerAdapter mAdapter;

    private AppWasteAll appWasteAll = new AppWasteAll();
    private PowerManagerApp app;
    private int dataloadfinish = 0x332;




    private boolean needCallback;
    private RequestCallback monthsListCallback,weekListCallback,dayListCallback;
    private ProgressDialog dlg;
    //请求列表管理器
    RequestManager requestManager = null;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == dataloadfinish) {
                mDatas = new ArrayList<Fragment>();
                mTables = new ArrayList<String>();
                for(int i=0;i<20;i++)
                {
                    if(i==19)
                    {
                        mTables.add("今天");
                    }
                    else if(i==18)
                    {
                        mTables.add("昨天");
                    }
                    else
                    {
                        mTables.add(Utils.getMouth(i-19)+"月"+Utils.getDay(i-19)+"日");
                    }
                    DayDissFragment dayDissFragment = new DayDissFragment();
                    dayDissFragment.setTime(mTables.get(i));
                    dayDissFragment.setIndex(i);
                    mDatas.add(dayDissFragment);

                }
                mAdapter = new AllWasteActivity.MyPagerAdapter(getSupportFragmentManager(),mDatas);
                mViewPager.setAdapter(mAdapter);
                mViewPager.setCurrentItem(19);
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        mCurrentPageIndex = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                mViewPager.setOffscreenPageLimit(3);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra("dvbundle");
        device = (ElectricityMeter)bundle.getSerializable("device");
        app = (PowerManagerApp)getApplication();
        //新建网络请求队列管理器
        requestManager = new RequestManager(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todaydiss);
        mViewPager = (ViewPager)findViewById(R.id.todaydiss_viewpager);
        nextButton = (Button)findViewById(R.id.todaydiss_button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllWasteActivity.this,AllWastelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("device",device);
                intent.putExtra("dvbundle",bundle);
                startActivity(intent);
            }
        });
        returnButton = (Button)findViewById(R.id.todaydiss_button_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleText = (TextView)findViewById(R.id.todaydiss_text_title);
        titleText.setText("总功耗");

        //loadingdata();
        loadwasteInfo();
    }

    @Override
    public void onPause()
    {
        if (requestManager != null) {
            requestManager.cancelRequest();
        }
        super.onPause();
    }
    @Override
    public void onDestroy()
    {
        if (requestManager != null) {
            requestManager.cancelRequest();
        }
        super.onDestroy();
    }
    private void loadwasteInfo()
    {
        monthsListCallback = new AbstractRequestCallback() {
            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0) {
                    List<YunReData> yunReDataList = YunService.StringToListYunReData(phalEntity.getData());
                    if (yunReDataList != null && yunReDataList.size() > 0) {
                        for (int i = 0; i < yunReDataList.size(); i++) {
                            YunReData yunReData = yunReDataList.get(i);
                            appWasteAll.getMonthsWasteList().add(new Float(yunReData.getWaste()));
                        }
                        app.setAppWasteAll(appWasteAll,device.getAllapp().getId());
                    }
                    //send消息
                    Message message1 = new Message();
                    message1.what = dataloadfinish;
                    message1.obj = 1;
                    handler.sendMessage(message1);
                }
                else
                {
                    //发送send消息
                    appWasteAll = app.getAppWasteAll(device.getAllapp().getId());
                    Message message1 = new Message();
                    message1.what = dataloadfinish;
                    message1.obj = 1;
                    handler.sendMessage(message1);
                }
            }
        };
        weekListCallback = new AbstractRequestCallback() {
            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0) {
                    List<YunReData> yunReDataList = YunService.StringToListYunReData(phalEntity.getData());
                    if (yunReDataList != null && yunReDataList.size() > 0) {
                        for(int i=0;i<yunReDataList.size();i++) {
                            YunReData yunReData = yunReDataList.get(i);
                            appWasteAll.getWeeksWasteList().add(new Float(yunReData.getWaste()));
                        }
                    }
                    ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
                    RequestParameter rp1 = new RequestParameter("app_id",device.getAllapp().getId()+"");
                    params.add(rp1);
                    RemoteService.getInstance().invoke(AllWasteActivity.this,requestManager, "Waste_Getmonthwaste", params,
                            monthsListCallback);
                }
                else
                {
                    //发送send消息
                    appWasteAll = app.getAppWasteAll(device.getAllapp().getId());
                    Message message1 = new Message();
                    message1.what = dataloadfinish;
                    message1.obj = 1;
                    handler.sendMessage(message1);
                }
            }
        };
        dayListCallback = new AbstractRequestCallback() {
            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content,PhalEntity.class);
                if(phalEntity.getCode()==0)
                {
                    List<YunReData> yunReDataList= YunService.StringToListYunReData(phalEntity.getData());
                    if(yunReDataList!=null&&yunReDataList.size()>0)
                    {
                        for(int i=0;i<yunReDataList.size();i++)
                        {
                            YunReData yunReData = yunReDataList.get(i);
                            DayWaste dayWaste = new DayWaste();
                            dayWaste.daywaste[0] = (float)yunReData.getWasteh1();
                            dayWaste.daywaste[1] = (float)yunReData.getWasteh2();
                            dayWaste.daywaste[2] = (float)yunReData.getWasteh3();
                            dayWaste.daywaste[3] = (float)yunReData.getWasteh4();
                            dayWaste.daywaste[4] = (float)yunReData.getWasteh5();
                            dayWaste.daywaste[5] = (float)yunReData.getWasteh6();
                            dayWaste.daywaste[6] = (float)yunReData.getWasteh7();
                            dayWaste.daywaste[7] = (float)yunReData.getWasteh8();
                            dayWaste.daywaste[8] = (float)yunReData.getWasteh9();
                            dayWaste.daywaste[9] = (float)yunReData.getWasteh10();
                            dayWaste.daywaste[10] = (float)yunReData.getWasteh11();
                            dayWaste.daywaste[11] = (float)yunReData.getWasteh12();
                            appWasteAll.getDayWasteList().add(new Float(yunReData.getWaste()));
                            appWasteAll.getHourWasteList().add(dayWaste);
                        }
                    }
                    ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
                    RequestParameter rp1 = new RequestParameter("app_id",device.getAllapp().getId()+"");
                    params.add(rp1);
                    RemoteService.getInstance().invoke(AllWasteActivity.this,requestManager, "Waste_Getweekwaste", params,
                            weekListCallback);
                }
                else
                {
                    //发送send消息
                    appWasteAll = app.getAppWasteAll(device.getAllapp().getId());
                    Message message1 = new Message();
                    message1.what = dataloadfinish;
                    message1.obj = 1;
                    handler.sendMessage(message1);
                }
            }
        };
        //发送获取day统计请求
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("app_id",device.getAllapp().getId()+"");
        params.add(rp1);
        RemoteService.getInstance().invoke(AllWasteActivity.this,requestManager, "Waste_Getdaywaste", params,
                dayListCallback);

    }
    class MyPagerAdapter extends FragmentPagerAdapter
    {
        private List<Fragment> fragments = new ArrayList<Fragment>();
        public MyPagerAdapter(FragmentManager fm){
            super(fm);
        }
        public MyPagerAdapter(FragmentManager fragmentManager,
                              ArrayList<Fragment> fragments)
        {
            super(fragmentManager);
            this.fragments = fragments;
        }
        @Override
        public Fragment getItem(int index)
        {
            return fragments.get(index);
        }
        @Override
        public int getCount()
        {
            return fragments.size();
        }
    }

    private abstract class AbstractRequestCallback implements RequestCallback {

        public abstract void onSuccess(String content);

        public void onFail(String errorMessage) {
            //dlg.dismiss();

            new AlertDialog.Builder(AllWasteActivity.this).setTitle("出错啦")
                    .setMessage(errorMessage).setPositiveButton("确定", null)
                    .show();
        }

        public void onCookieExpired() {
            //dlg.dismiss();

            new AlertDialog.Builder(AllWasteActivity.this)
                    .setTitle("出错啦")
                    .setMessage("Cookie过期，请重新登录")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent(
                                            AllWasteActivity.this,
                                            LoginActivity.class);
                                    intent.putExtra(AppConstants.NeedCallback,
                                            true);
                                    startActivity(intent);
                                }
                            }).show();
        }
    }


   /*
    public class MessageMqttReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MqttService.MQTT_RECE_MESSAGE_ACTION)) {
                String topic = intent.getStringExtra("Topic");
                byte[] data = intent.getByteArrayExtra("Message");
                if(topic.equals(YunService.frtopic+Utils.getAndroidMac(AllWasteActivity.this))) {
                    YunReceData yunReceData = YunService.StringToObject(new String(data).toString());
                    if(yunReceData.getMsg()==1)
                    {
                        if(sendnum==1)
                        {
                            if(yunReceData.getData()!=null) {
                                if (yunReceData.getData().size() > 0) {
                                    for(int i= 0;i<yunReceData.getData().size();i++)
                                    {
                                        YunReData yunReData = yunReceData.getData().get(i);
                                        DayWaste dayWaste = new DayWaste();
                                        dayWaste.daywaste[0] = (float)yunReData.getWasteh1();
                                        dayWaste.daywaste[1] = (float)yunReData.getWasteh2();
                                        dayWaste.daywaste[2] = (float)yunReData.getWasteh3();
                                        dayWaste.daywaste[3] = (float)yunReData.getWasteh4();
                                        dayWaste.daywaste[4] = (float)yunReData.getWasteh5();
                                        dayWaste.daywaste[5] = (float)yunReData.getWasteh6();
                                        dayWaste.daywaste[6] = (float)yunReData.getWasteh7();
                                        dayWaste.daywaste[7] = (float)yunReData.getWasteh8();
                                        dayWaste.daywaste[8] = (float)yunReData.getWasteh9();
                                        dayWaste.daywaste[9] = (float)yunReData.getWasteh10();
                                        dayWaste.daywaste[10] = (float)yunReData.getWasteh11();
                                        dayWaste.daywaste[11] = (float)yunReData.getWasteh12();
                                        appWasteAll.getDayWasteList().add(new Float(yunReData.getWaste()));
                                        appWasteAll.getHourWasteList().add(dayWaste);
                                        String Message = YunService.ToJsonString(YunService.sergetweekwaste,device.getAllapp().getId(),null);
                                        sendnum = 2;
                                        sendMessage(Message);
                                    }

                                }
                                else
                                {
                                    appWasteAll = app.getAppWasteAll(device.getAllapp().getId());
                                    sendnum = 100;
                                }
                            }
                            else
                            {
                                appWasteAll = app.getAppWasteAll(device.getAllapp().getId());
                                sendnum = 100;
                            }
                        }
                        else if(sendnum==2)
                        {
                            if(yunReceData.getData()!=null) {
                                if (yunReceData.getData().size() > 0) {
                                    for(int i= 0;i<yunReceData.getData().size();i++)
                                    {
                                        YunReData yunReData = yunReceData.getData().get(i);
                                        appWasteAll.getWeeksWasteList().add(new Float(yunReData.getWaste()));
                                        String Message = YunService.ToJsonString(YunService.sergetmonthwaste,device.getAllapp().getId(),null);
                                        sendnum = 3;
                                        sendMessage(Message);
                                    }
                                }
                                else
                                {
                                    appWasteAll = app.getAppWasteAll(device.getAllapp().getId());
                                    sendnum = 100;
                                }
                            }
                            else
                            {
                                appWasteAll = app.getAppWasteAll(device.getAllapp().getId());
                                sendnum = 100;
                            }
                        }
                        else if(sendnum == 3)
                        {
                            if(yunReceData.getData()!=null) {
                                if (yunReceData.getData().size() > 0) {
                                    for(int i= 0;i<yunReceData.getData().size();i++)
                                    {
                                        YunReData yunReData = yunReceData.getData().get(i);
                                        appWasteAll.getMonthsWasteList().add(new Float(yunReData.getWaste()));
                                        //String Message = YunService.ToJsonString(YunService.sergetmonthwaste,appliance.getId(),null);
                                        app.setAppWasteAll(appWasteAll,device.getAllapp().getId());
                                        sendnum = 100;

                                        //sendMessage(Message);
                                    }
                                }
                                else
                                {
                                    appWasteAll = app.getAppWasteAll(device.getAllapp().getId());
                                    sendnum = 100;
                                }
                            }
                            else
                            {
                                appWasteAll = app.getAppWasteAll(device.getAllapp().getId());
                                sendnum = 100;
                            }
                        }
                    }
                    else
                    {

                    }
                }
            }
        }
    }
    private void sendMessage(String msg)
    {
        try
        {
            iMqttService.mqttPubMessage(YunService.fstopic+Utils.getAndroidMac(AllWasteActivity.this), msg, 0);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    public void initMqtt()
    {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mServiceIntent = new Intent(this, MqttService.class);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MqttService.MQTT_RECE_MESSAGE_ACTION);
        //sendMessage();

    }

    private void loadingdata()
    {

        final Dialog mDialog;
        mDialog = new AlertDialog.Builder(this).create();
        mDialog.show();
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.dlg_loading);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(0,0,0,0)));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width =147;
        lp.height =147;
        lp.gravity = Gravity.CENTER;
        mDialog.getWindow().setAttributes(lp);

        new Thread(){
            @Override
            public void run()
            {
                for(int i= 0;i<200;i++)
                {
                    if(sendnum==100)
                    {
                        mDialog.cancel();
                        sendnum = 0;
                        Looper.prepare();
                        Message message1 = new Message();
                        message1.what = dataloadfinish;
                        message1.obj = 1;
                        handler.sendMessage(message1);
                        Looper.loop();
                        break;
                    }
                    try {
                        sleep(50);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                mDialog.cancel();
                Looper.prepare();
                Message message1 = new Message();
                message1.what = dataloadfinish;
                message1.obj = 1;
                handler.sendMessage(message1);
                Looper.loop();
            }
        }.start();
    }*/
}
