package com.splxtech.powermanagor.activity.dqjc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.PowerManagerApp;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.activity.login.LoginActivity;
import com.splxtech.powermanagor.engine.AppConstants;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.AppWasteAll;
import com.splxtech.powermanagor.entity.Appliance;
import com.splxtech.powermanagor.entity.DayWaste;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.powermanagor.entity.YunReData;
import com.splxtech.powermanagor.entity.YunService;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestManager;
import com.splxtech.splxapplib.net.RequestParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li300 on 2016/10/12 0012.
 */

public class ToDayDissActivity extends FragmentActivity {
    public ViewPager mViewPager;
    private ArrayList<Fragment> mDatas;
    private ArrayList<String> mTables;
    private Button nextButton;
    private Button returnButton;
    private TextView titleText;
    private int mCurrentPageIndex;
    private String title;
    private Appliance appliance;
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
                    ToDayDissFragment toDayDissFragment = new ToDayDissFragment();
                    toDayDissFragment.setTime(mTables.get(i));
                    toDayDissFragment.setData(appWasteAll.getDayWasteList().get(i),
                            appWasteAll.getHourWasteList().get(i).daywaste);
                    mDatas.add(toDayDissFragment);

                }
                mAdapter = new MyPagerAdapter(getSupportFragmentManager(),mDatas);
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
        Bundle bundle = intent.getBundleExtra("dqbundle");
        appliance = (Appliance)bundle.getSerializable("appliance");
        title = appliance.getName();
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
                Intent intent = new Intent(ToDayDissActivity.this,DayDissActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("appliance",appliance);
                intent.putExtra("dqbundle",bundle);
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
        titleText.setText(title);
        //localBroadcastManager.registerReceiver(mReciver,mIntentFilter);
        //
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

    //通过网络获取daylist weeklist monthslist内容
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
                        app.setAppWasteAll(appWasteAll,appliance.getId());
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
                    appWasteAll = app.getAppWasteAll(appliance.getId());
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
                    RequestParameter rp1 = new RequestParameter("app_id",appliance.getId()+"");
                    params.add(rp1);
                    RemoteService.getInstance().invoke(ToDayDissActivity.this,requestManager, "Waste_Getmonthwaste", params,
                            monthsListCallback);
                }
                else
                {
                    //发送send消息
                    appWasteAll = app.getAppWasteAll(appliance.getId());
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
                    RequestParameter rp1 = new RequestParameter("app_id",appliance.getId()+"");
                    params.add(rp1);
                    RemoteService.getInstance().invoke(ToDayDissActivity.this,requestManager, "Waste_Getweekwaste", params,
                            weekListCallback);
                }
                else
                {
                    //发送send消息
                    appWasteAll = app.getAppWasteAll(appliance.getId());
                    Message message1 = new Message();
                    message1.what = dataloadfinish;
                    message1.obj = 1;
                    handler.sendMessage(message1);
                }
            }
        };
        //发送获取day统计请求
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("app_id",appliance.getId()+"");
        params.add(rp1);
        RemoteService.getInstance().invoke(ToDayDissActivity.this,requestManager, "Waste_Getdaywaste", params,
                dayListCallback);

    }
    private abstract class AbstractRequestCallback implements RequestCallback {

        public abstract void onSuccess(String content);

        public void onFail(String errorMessage) {
            //dlg.dismiss();

            new AlertDialog.Builder(ToDayDissActivity.this).setTitle("出错啦")
                    .setMessage(errorMessage).setPositiveButton("确定", null)
                    .show();
        }

        public void onCookieExpired() {
            //dlg.dismiss();

            new AlertDialog.Builder(ToDayDissActivity.this)
                    .setTitle("出错啦")
                    .setMessage("Cookie过期，请重新登录")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent(
                                            ToDayDissActivity.this,
                                            LoginActivity.class);
                                    intent.putExtra(AppConstants.NeedCallback,
                                            true);
                                    startActivity(intent);
                                }
                            }).show();
        }
    }
}
