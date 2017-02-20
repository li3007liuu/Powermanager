package com.splxtech.powermanagor.activity.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RadioButton;


import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.PowerManagerApp;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.activity.dlcx.DlcxFragment;
import com.splxtech.powermanagor.activity.dqjc.DqjcFragment;
import com.splxtech.powermanagor.activity.ghtj.GhtjFragment;
import com.splxtech.powermanagor.activity.grzx.GrzxFragment;
import com.splxtech.powermanagor.activity.login.LoginActivity;
import com.splxtech.powermanagor.engine.AppConstants;
import com.splxtech.powermanagor.engine.MqttService;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.powermanagor.entity.PMUserInfo;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.powermanagor.entity.YunReData;
import com.splxtech.powermanagor.entity.YunService;
import com.splxtech.powermanagor.utils.UriUtils;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestManager;
import com.splxtech.splxapplib.net.RequestParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li300 on 2016/9/19 0019.
 * 里面mqtt部分与loading对话框部分多余，有时间进行安全删除
 */

public class MainActivity extends FragmentActivity
{
    //view 变量
    private ViewPager mViewPager;
    private RadioButton dqjcRadio;
    private RadioButton ghtjRadio;
    private RadioButton dlcxRadio;
    private RadioButton grzxRadio;
    private FragmentPagerAdapter mAdapter;
    private ArrayList<Fragment> mDatas;
    private int mCurrentPageIndex;

    private boolean needCallback;
    private RequestCallback hardListCallback;
    private ProgressDialog dlg;

    //后台数据变量
    private PMUserInfo pmUserInfo;
    private List<ElectricityMeter> deviceList = new ArrayList<>();
    private int dataloadfinish = 0x332;

    //请求列表管理器
    RequestManager requestManager = null;


    //全局对象变量
    public PowerManagerApp app;
    private Context activity = this;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what==dataloadfinish)
            {
                mDatas = new ArrayList<Fragment>();
                DlcxFragment dlcxFragment = new DlcxFragment();
                DqjcFragment dqjcFragment = new DqjcFragment();
                GhtjFragment ghtjFragment = new GhtjFragment();
                GrzxFragment grzxFragment = new GrzxFragment();
                mDatas.add(dqjcFragment);
                mDatas.add(ghtjFragment);
                mDatas.add(dlcxFragment);
                mDatas.add(grzxFragment);

                mAdapter = new MyPagerAdapter(getSupportFragmentManager(),mDatas);
                mViewPager.setAdapter(mAdapter);
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        setRadio(position);
                        mCurrentPageIndex = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                mViewPager.setOffscreenPageLimit(4);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        app = (PowerManagerApp)getApplication();
        pmUserInfo = app.getPmUserInfo();
        //新建网络请求队列管理器
        requestManager = new RequestManager(this);
        super.onCreate(savedInstanceState);
        //启动socket服务
        //Intent intent2 = new Intent(this, SocketService.class);
        //startService(intent2);
        //防止出现没有打开的情况 服务不会重复开启
        MqttService.actionStart(this);
        setContentView(R.layout.activity_mainr);
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        dqjcRadio = (RadioButton)findViewById(R.id.main_radio_dqjc);
        Drawable drawabledqjc = getResources().getDrawable(R.drawable.selector_dqjc_radio);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int dpi = dm.densityDpi;
        int radioimagew = dpi/6;
        drawabledqjc.setBounds(0,0,radioimagew,radioimagew);
        dqjcRadio.setCompoundDrawables(null,drawabledqjc,null,null);
        dqjcRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRadio(0);
                mViewPager.setCurrentItem(0);
            }
        });
        ghtjRadio = (RadioButton)findViewById(R.id.main_radio_ghtj);
        Drawable drawableghfx = getResources().getDrawable(R.drawable.selector_ghtj_radio);
        drawableghfx.setBounds(0,0,radioimagew,radioimagew);
        ghtjRadio.setCompoundDrawables(null,drawableghfx,null,null);
        ghtjRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRadio(1);
                mViewPager.setCurrentItem(1);
            }
        });
        dlcxRadio = (RadioButton)findViewById(R.id.main_radio_dlcx);
        Drawable drawabledqsc = getResources().getDrawable(R.drawable.selector_dlcx_radio);
        drawabledqsc.setBounds(0,0,radioimagew,radioimagew);
        dlcxRadio.setCompoundDrawables(null,drawabledqsc,null,null);
        dlcxRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRadio(2);
                mViewPager.setCurrentItem(2);
            }
        });
        grzxRadio = (RadioButton)findViewById(R.id.main_radio_grzx);
        Drawable drawablegrzx = getResources().getDrawable(R.drawable.selector_grzx_radio);
        drawablegrzx.setBounds(0,0,radioimagew,radioimagew);
        grzxRadio.setCompoundDrawables(null,drawablegrzx,null,null);
        grzxRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRadio(3);
                mViewPager.setCurrentItem(3);
            }
        });
        loaduserInfo();

    }

    @Override
    protected void onDestroy()
    {
        if (requestManager != null) {
            requestManager.cancelRequest();
        }
        MqttService.actionStop(this);
        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        if (requestManager == null)
        {
            requestManager = new RequestManager(this);
        }
        super.onResume();
    }

    @Override
    public void onPause()
    {
        if (requestManager != null) {
            requestManager.cancelRequest();
        }
        super.onPause();
    }


    private void setRadio(int p)
    {
        switch (p)
        {
            case 0:
                dqjcRadio.setChecked(true);
                ghtjRadio.setChecked(false);
                dlcxRadio.setChecked(false);
                grzxRadio.setChecked(false);
                break;
            case 1:
                dqjcRadio.setChecked(false);
                ghtjRadio.setChecked(true);
                dlcxRadio.setChecked(false);
                grzxRadio.setChecked(false);
                break;
            case 2:
                dqjcRadio.setChecked(false);
                ghtjRadio.setChecked(false);
                dlcxRadio.setChecked(true);
                grzxRadio.setChecked(false);
                break;
            case 3:
                dqjcRadio.setChecked(false);
                ghtjRadio.setChecked(false);
                dlcxRadio.setChecked(false);
                grzxRadio.setChecked(true);
                break;
            default:
                dqjcRadio.setChecked(true);
                ghtjRadio.setChecked(false);
                dlcxRadio.setChecked(false);
                grzxRadio.setChecked(false);
                break;
        }
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

    //通过网络获取userinfo与hardlist内容
    private void loaduserInfo()
    {
        //获取设备列表回调函数
        hardListCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0) {
                    List<YunReData> yunReDataList=YunService.StringToListYunReData(phalEntity.getData());
                    if(yunReDataList!=null&&yunReDataList.size()>0)
                    {
                        for(int i=0;i<yunReDataList.size();i++)
                        {
                            YunReData yunReData = yunReDataList.get(i);
                            ElectricityMeter electricityMeter = new ElectricityMeter(yunReData.getId());
                            electricityMeter.setName(Utils.utf8decode(yunReData.getName()));
                            electricityMeter.setProductId(yunReData.getNetid());
                            electricityMeter.setProductType(yunReData.getType());
                            if(yunReData.getType()==1)
                            {
                                electricityMeter.setProductImageId(R.drawable.db);
                            }
                            else
                            {
                                electricityMeter.setProductImageId(R.drawable.znkg);
                            }
                            deviceList.add(electricityMeter);
                            app.setElectricityMeters(deviceList,pmUserInfo.getId());
                            Message message1 = new Message();
                            message1.what = dataloadfinish;
                            message1.obj = 1;
                            handler.sendMessage(message1);
                        }
                    }
                    else
                    {
                        Utils.toastShow(MainActivity.this,"hardlist data transform error!");
                        Message message1 = new Message();
                        message1.what = dataloadfinish;
                        message1.obj = 1;
                        handler.sendMessage(message1);
                    }
                }
                else
                {
                    Utils.toastShow(MainActivity.this,"user info get fail!");
                   // Looper.prepare();
                    Message message1 = new Message();
                    message1.what = dataloadfinish;
                    message1.obj = 1;
                    handler.sendMessage(message1);
                  //  Looper.loop();
                }
            }
        };
        //获取用户信息回调函数
        RequestCallback userInfoCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content,PhalEntity.class);
                if(phalEntity.getCode()==0)
                {
                    //loginIn=true;
                    YunReData yunReData= YunService.StringToYunReData(phalEntity.getData());
                    if(yunReData!=null) {
                        pmUserInfo.setId(yunReData.getId());
                        pmUserInfo.setName(yunReData.getName());
                        pmUserInfo.setSex(Utils.string_boolean(yunReData.getSex()));
                        Uri uri = UriUtils.getUriFromFilePath(yunReData.getFace());
                        pmUserInfo.setFaceuri(uri);
                        pmUserInfo.setBirthday(yunReData.getBirthday());
                        pmUserInfo.setTel(yunReData.getTel());
                        pmUserInfo.setEmail(yunReData.getEmail());
                        Utils.saveUserInfo(MainActivity.this, pmUserInfo);
                        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
                        RequestParameter rp1 = new RequestParameter("user_id",pmUserInfo.getId()+"");
                        params.add(rp1);
                        RemoteService.getInstance().invoke(MainActivity.this,requestManager, "Hard_Gethardlist", params,
                                hardListCallback);
                    }
                    else
                    {
                        Utils.toastShow(MainActivity.this,"user data transform error!");
                      //  Looper.prepare();
                        Message message1 = new Message();
                        message1.what = dataloadfinish;
                        message1.obj = 1;
                        handler.sendMessage(message1);
                      //  Looper.loop();
                    }

                }
                else
                {
                    Utils.toastShow(MainActivity.this,"user info get fail!");
                  //  Looper.prepare();
                    Message message1 = new Message();
                    message1.what = dataloadfinish;
                    message1.obj = 1;
                    handler.sendMessage(message1);
                 //   Looper.loop();
                }
            }
        };
        //发送获取userinfo请求
        if(pmUserInfo.getEmail().length()>0)
        {
            ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
            RequestParameter rp1 = new RequestParameter("user_email",pmUserInfo.getEmail());
            params.add(rp1);
            RemoteService.getInstance().invoke(MainActivity.this,requestManager, "Usera_Getinfo", params,
                    userInfoCallback);
        }
        else if(pmUserInfo.getTel().length()>0)
        {
            ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
            RequestParameter rp1 = new RequestParameter("user_tel",pmUserInfo.getTel());
            params.add(rp1);
            RemoteService.getInstance().invoke(MainActivity.this,requestManager, "Usera_Getinfo", params,
                    userInfoCallback);
        }
        else
        {

        }

    }

    //网络数据返回全局类
    private abstract class AbstractRequestCallback implements RequestCallback {

        public abstract void onSuccess(String content);

        public void onFail(String errorMessage) {
            //dlg.dismiss();

            new AlertDialog.Builder(MainActivity.this).setTitle("出错啦")
                    .setMessage(errorMessage).setPositiveButton("确定", null)
                    .show();
        }

        public void onCookieExpired() {
            //dlg.dismiss();

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("出错啦")
                    .setMessage("Cookie过期，请重新登录")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent(
                                            MainActivity.this,
                                            LoginActivity.class);
                                    intent.putExtra(AppConstants.NeedCallback,
                                            true);
                                    startActivity(intent);
                                }
                            }).show();
        }
    }
}
