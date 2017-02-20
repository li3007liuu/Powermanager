package com.splxtech.powermanagor.activity.grzx;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.integrity_project.smartconfiglib.SmartConfig;
import com.integrity_project.smartconfiglib.SmartConfigListener;
import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.PowerManagerApp;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.powermanagor.entity.PMUserInfo;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.powermanagor.utils.MDnsCallbackInterface;
import com.splxtech.powermanagor.utils.MDnsHelper;
import com.splxtech.powermanagor.utils.NetworkUtil;
import com.splxtech.powermanagor.utils.SmartConfigConstants;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.activity.BaseActivity;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestParameter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li300 on 2016/10/20 0020.
 */

public class SimpleLinkActivity extends AppBaseActivity{
    private final int RECEIVE_UDP_PORT = 49001;
    byte[] freeData;
    SmartConfig smartConfig;
    SmartConfigListener smartConfigListener;
    MDnsHelper mDnsHelper;
    MDnsCallbackInterface mDnsCallback;
    private EditText etPass;
    private TextView etWifiName;
    private Button buttonStart;
    private Button buttonBack;
    private ProgressBar configProgress;

    private PopupWindow setNameWindow;
    private TextView pdTitle;
    private EditText pdName;
    private TextView pdType;
    private TextView pdId;
    private ImageView pdImage;

    private int receive_udp_port;
    private MyTimerCount mtc;
    private UdpReceive udpReceive;
    private int deviceCount;
    private String contentTotal = "";
    private BaseActivity baseActivity = this;
    private String macc="";
    private String namec = "";
    private int typec = 0;
    private int  hardid = 0;

    private PMUserInfo pmUserInfo;
    private PowerManagerApp app;
    private List<ElectricityMeter> deviceList = new ArrayList<>();

    @Override
    protected void initVariables()
    {
        receive_udp_port = RECEIVE_UDP_PORT;
        app = (PowerManagerApp)getApplication();
        pmUserInfo = app.getPmUserInfo();
        deviceList = app.getElectricityMeters(pmUserInfo.getId());
    }
    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_simplelink);
        etPass = (EditText)findViewById(R.id.simplelink_edit_wifipass);
        etWifiName = (TextView)findViewById(R.id.simplelink_text_wifissid);
        configProgress = (ProgressBar)findViewById(R.id.simplelink_progressbar_config);
        buttonStart = (Button)findViewById(R.id.simplelink_button_startconfig);
        buttonStart.setText("开始配置");
        buttonBack = (Button)findViewById(R.id.simplelink_button_return);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkUtil.getConnectionStatus(baseActivity)!=NetworkUtil.WIFI){
                    //提示没有连接wifi
                    return;
                }
                if(buttonStart.getText().equals("开始配置")){
                    String ssid = etWifiName.getText().toString();
                    if(ssid.isEmpty()){
                        //提示ssid为空
                        return;
                    }
                    hideInput();
                    buttonStart.setText("取消");
                    startSmartConfig();
                }
                else{
                    cancel();
                }
            }
        });
        initSetNamePop();

    }
    @Override
    protected void loadData()
    {
        mtc = new MyTimerCount(SmartConfigConstants.MAIN_SCAN_TIME,1000,configProgress,buttonStart);
        registerReceiver(networkChangeReceiver, new IntentFilter(
                SmartConfigConstants.NETWORK_CHANGE_BROADCAST_ACTION));
        registerReceiver(configStopedReceiver, new IntentFilter(
                SmartConfigConstants.CONFIG_STOPED));
        initMDns();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        cancel();
        unregisterReceiver(networkChangeReceiver);
        unregisterReceiver(configStopedReceiver);
    }



    public void initMDns(){
        mDnsCallback = new MDnsCallbackInterface() {
            @Override
            public void onDeviceResolved(JSONObject deviceJSON) {
                System.out.println("MDnsCallbackInterface---------------->"
                        + deviceJSON.toString());
            }
        };
        mDnsHelper = new MDnsHelper();
        mDnsHelper.init(this,mDnsCallback);
    }

    public void startSmartConfig(){
        String passwordKey = etPass.getText().toString().trim();
        byte[] paddedEncryptionKey;
        String SSID = etWifiName.getText().toString().trim();
        String gateway = NetworkUtil.getGateway(this);
        paddedEncryptionKey = null;
        freeData = new byte[1];
        freeData[0] = 0x03;
        smartConfig = null;
        smartConfigListener = new SmartConfigListener() {
            @Override
            public void onSmartConfigEvent(SmtCfgEvent event, Exception e) {
                System.out.println("onSmartConfigEvent----------->"+event.name()+" toString:"+event.toString());
            }
        };
        try{
            smartConfig = new SmartConfig(smartConfigListener,freeData,
                    passwordKey,paddedEncryptionKey,gateway,SSID,(byte)0,"");
            smartConfig.transmitSettings();
            deviceCount = 0;
            contentTotal = "";
            scanForDevices();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void scanForDevices(){
        mtc.start();
        udpReceive = new UdpReceive(new Handler(Looper.getMainLooper()),receive_udp_port);
        udpReceive.setConfigSuccessListener(new UdpReceive.ConfigSuccessListener() {
            @Override
            public void onConfigSuccess(String[] msgs) {
                if(contentTotal.contains(msgs[2]))
                    return;
                String msg = "IP:"+msgs[1]+"MAC:"+msgs[2]+"DEV:"+msgs[3]+"VER:"+msgs[4]+"\n";
                macc = Utils.stringlowtolarge(msgs[2]);
                contentTotal = msg;
                deviceCount++;
                //提示配置成功
                //弹出popwindows窗口，修改配置添加列表
                //setNameDlg(View view)
                Utils.toastShow(baseActivity,msg);
            }
        });
        udpReceive.start();
        new Thread(){
            @Override
            public void run(){
                mDnsHelper.startDiscovery();
            }
        }.start();
    }
    private void stopConfig(){
        buttonStart.setText("取消");
        buttonStart.setEnabled(false);
        configProgress.setProgress(0);
        if(udpReceive!=null)
        {
            udpReceive.stopReceive();;
            udpReceive = null;
        }
        new Thread(){
            public void run(){
                try{
                    smartConfig.stopTransmitting();
                    mDnsHelper.stopDiscovery();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        if(deviceCount>0)
        {
            Utils.toastShow(baseActivity,"配置成功！");
            setNameDlg(etWifiName);
        }
        else{
            Utils.toastShow(baseActivity,"配置失败！");

        }
    }

    private void hideInput(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }

    private void cancel(){
        buttonStart.setText("开始配置");
        mtc.cancel();
        mtc.onFinish();
    }
    private class MyTimerCount extends CountDownTimer{
        private ProgressBar progressBar;
        private Button button;
        public MyTimerCount(long millisInFuture,long countDownInterval,ProgressBar progressBar,Button button)
        {
            super(millisInFuture,countDownInterval);
            this.progressBar = progressBar;
            this.button = button;
        }
        @Override
        public void onTick(long millisUntilFinished){
            int secs = (int)(millisUntilFinished/1000);
            progressBar.setProgress((40-secs)*5/2);
        }
        @Override
        public void onFinish(){
            progressBar.setProgress(100);
            button.setText("开始配置");
            stopConfig();
        }
    }
    BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获得当前的SSID
            etWifiName.setText(NetworkUtil.getWifiName(SimpleLinkActivity.this));
        }
    };
    //停止配置完成广播接收
    BroadcastReceiver configStopedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            buttonStart.setText("开始配置");
            buttonStart.setEnabled(true);
        }
    };

    private void initSetNamePop(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view3 = inflater.inflate(R.layout.dlg_yjgl_dq,null);
        pdTitle = (TextView)view3.findViewById(R.id.dyjgl_text_title);
        pdName = (EditText)view3.findViewById(R.id.dyjgl_edit_name);
        pdType = (TextView)view3.findViewById(R.id.dyjgl_text_producttype);
        pdId = (TextView)view3.findViewById(R.id.dyjgl_text_productid);
        pdImage = (ImageView)view3.findViewById(R.id.dyjgl_image_product);
        Button okbutton = (Button)view3.findViewById(R.id.dyjgl_button_ok);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加设备到数据库中 service 内容需要重新修改
                //提供参数 name netid type userid
                namec = pdName.getText().toString();
                typec = 1;
                addhardlist();
                setNameWindow.dismiss();
            }
        });
        Button cancelbutton = (Button)view3.findViewById(R.id.dyjgl_button_cancel);
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNameWindow.dismiss();
            }
        });
        setNameWindow = new PopupWindow(view3,600, WindowManager.LayoutParams.WRAP_CONTENT, false);
        setNameWindow.setOutsideTouchable(false);
        setNameWindow.setFocusable(true);
    }

    private void setNameDlg(View view){
        pdTitle.setText("添加设备");
        pdId.setText(macc);
        pdType.setText(1+"");
        pdName.setText("未命名");
        pdImage.setImageResource(R.drawable.db);
        setNameWindow.showAtLocation(view, Gravity.CENTER,0,0);
    }
    private void addhardlist()
    {
        //新增设备回调函数
        RequestCallback addHardCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0)
                {
                    hardid = Integer.parseInt(phalEntity.getData());
                    ElectricityMeter meter = new ElectricityMeter(namec, macc, typec);
                    meter.setTableid(hardid);
                    deviceList.add(meter);
                    app.setElectricityMeters(deviceList, pmUserInfo.getId());
                    Utils.toastShow(SimpleLinkActivity.this,"设备添加完成");
                }
                else
                {
                    Utils.toastShow(SimpleLinkActivity.this,"设备添加失败");
                }
            }
        };
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("user_id",pmUserInfo.getId()+"");
        RequestParameter rp2 = new RequestParameter("hard_netid",macc);
        RequestParameter rp3 = new RequestParameter("hard_name",Utils.utf8encode(namec));
        RequestParameter rp4 = new RequestParameter("hard_type",typec+"");
        params.add(rp1);
        params.add(rp2);
        params.add(rp3);
        params.add(rp4);
        RemoteService.getInstance().invoke(this, "Hard_Addhardlist", params,
                addHardCallback);
    }
}
