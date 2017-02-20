package com.splxtech.powermanagor.activity.dqjc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.Base.MqttAppBaseActivity;
import com.splxtech.powermanagor.PowerManagerApp;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.adapter.DqItemAdapter;
import com.splxtech.powermanagor.adapter.DqTopItemAdapter;
import com.splxtech.powermanagor.engine.MqttService;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.Appliance;
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.powermanagor.entity.MainButton;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.powermanagor.entity.YunReData;
import com.splxtech.powermanagor.entity.YunService;
import com.splxtech.powermanagor.utils.AppProtocol;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.activity.BaseActivity;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li300 on 2016/10/6 0006.
 */

public class DqActivity extends MqttAppBaseActivity
{
    private Button returnButton;
    private Button dqsjkButton;
    private ListView listView;
    private TextView titleText;
    private ArrayList<Appliance> applianceList = new ArrayList<>();
    private ArrayList<Appliance> tempappList = new ArrayList<>();
    private DqItemAdapter dqItemAdapter;
    private BaseActivity baseActivity = this;
    private GridView gridView;
    private ArrayList<MainButton> mainButtonArrayList = new ArrayList<>();
    private DqTopItemAdapter dqTopItemAdapter;

    public static String hardstopic = "PM/APP/";
    public static String hardrtopic = "PM/APPA/";
    private ElectricityMeter device;
    private boolean flag = false;//查询定时器的开启与关闭标志位
    private boolean sflag = false;//消息是否发送标志位
    private boolean loaddata = false;
    private boolean getwasteflag = false;
    private int sendnum = 0;
    private PowerManagerApp app;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(flag==true) {
                if(sflag==true) {
                    if(getwasteflag==true) {
                        //获取 功耗
                        getWasteList();
                    }
                    //获取电器状态
                    sendMessage();
                }
                try {
                    handler.postDelayed(this, 500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(loaddata==true)
                {
                    loaddata = false;
                    //订阅主题
                    try{
                        iMqttService.mqttSubscribe(hardrtopic+device.getProductId(),0);
                    }
                    catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    protected void initVariables()
    {
        //接收参数
        app = (PowerManagerApp)getApplication();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra("devicebundle");
        device = (ElectricityMeter) bundle.getSerializable("device");
        initMainButtonList();

        //给全局消息接收器赋值，并进行消息处理
        mReciver = new MessageMqttReciver(){
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();

                if(action.equals(MqttService.MQTT_RECE_MESSAGE_ACTION))
                {
                    String topic = intent.getStringExtra("Topic");
                    byte[] data = intent.getByteArrayExtra("Message");
                    if(topic.equals(hardrtopic+device.getProductId()))
                    {
                        if(sflag==true) {
                            int[] receData = AppProtocol.getReceiverData(AppProtocol.DQLJSBPORTNUM, data);
                            upDataAppList(receData);
                        }
                    }
                }
            }
        };
    }
    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_dqjc);
        returnButton = (Button)findViewById(R.id.dq_button_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dqsjkButton = (Button)findViewById(R.id.dq_button_dqsjk);
        dqsjkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoDqsjk();
            }
        });
        titleText = (TextView)findViewById(R.id.dq_text_title);
        if(device!=null)
        {
            titleText.setText(device.getName());
        }

        dqItemAdapter = new DqItemAdapter(this,R.layout.item_dqlist,applianceList);

        listView = (ListView)findViewById(R.id.dq_list_content);
        listView.setAdapter(dqItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Appliance appliance = applianceList.get(i);
                Intent intent = new Intent(DqActivity.this,ToDayDissActivity.class);
                //intent.putExtra("dqname",appliance.getName());
                Bundle bundle = new Bundle();
                bundle.putSerializable("appliance",appliance);
                intent.putExtra("dqbundle",bundle);
                startActivity(intent);
            }
        });
        dqTopItemAdapter = new DqTopItemAdapter(this,R.layout.item_dq_top,mainButtonArrayList);
        gridView = (GridView)findViewById(R.id.dq_grid_content);
        gridView.setAdapter(dqTopItemAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {
                    Intent intent = new Intent(DqActivity.this,DlcsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("device",device);//applianceList);
                    intent.putExtra("dvbundle",bundle);
                    startActivity(intent);
                }
                else if(i==1)
                {
                    Intent intent = new Intent(DqActivity.this,DlbxActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("device",device);//applianceList);
                    intent.putExtra("dvbundle",bundle);
                    startActivity(intent);
                }
                else if(i==2)
                {
                    Intent intent = new Intent(DqActivity.this,XbfxActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("device",device);//applianceList);
                    intent.putExtra("dvbundle",bundle);
                    startActivity(intent);
                }
                else if(i==3)
                {

                    Intent intent = new Intent(DqActivity.this,ToDayDissActivity.class);
                    Bundle bundle = new Bundle();
                    device.getAllapp().setName("总功耗");
                    bundle.putSerializable("appliance",device.getAllapp());
                    intent.putExtra("dqbundle",bundle);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void loadData()
    {
        handler.postDelayed(runnable,500);
        flag = true;
        //订阅主题
        loaddata = true;
        //发送获取applist数据的请求
        getApplist();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2) {
            Bundle bundle = data.getBundleExtra("bundle");
            applianceList.removeAll(applianceList);
            tempappList.removeAll(tempappList);
            ArrayList<Appliance> temp = (ArrayList<Appliance>)bundle.getSerializable("applianceList");
            for(int i=0;i<temp.size();i++)
            {
                tempappList.add(temp.get(i));
                applianceList.add(temp.get(i));
            }
            dqItemAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onStart()
    {
        super.onStart();
        sflag = true;
    }
    @Override
    public void onStop()
    {
        sflag = false;
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        //Utils.saveObject(applianceStorePath,applianceList);
        //app.setApplianceList(applianceList,device.getTableid());
        device.setApplianceArrayList(applianceList);
        app.setDevice(device,device.getTableid());
        getwasteflag = false;
        flag = false;
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        DqActivity.this.finish();
        //super.onBackPressed();
    }
    private void initMainButtonList()
    {
        if(mainButtonArrayList.size()==0)
        {
            mainButtonArrayList.add(new MainButton("电力参数",R.drawable.idlcs));
            mainButtonArrayList.add(new MainButton("电力波形",R.drawable.ibxt));
            mainButtonArrayList.add(new MainButton("谐波分析",R.drawable.ixbfx));
            mainButtonArrayList.add(new MainButton("总功耗",R.drawable.circle,"29"));
        }
    }

    private void gotoDqsjk()
    {
        Intent intent = new Intent(DqActivity.this, DqsjkActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("device",device);//applianceList);
        intent.putExtra("dvbundle",bundle);
        startActivityForResult(intent, 2);
    }

    private void sendMessage()
    {
        try {
            String Message = new String(AppProtocol.getSendData(AppProtocol.DQLJSBPORTNUM, 0), "ISO-8859-1");
            try {
                iMqttService.mqttPubMessage(hardstopic+device.getProductId(),Message , 0);
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getApplist()
    {
        //获取applist列表
        RequestCallback getApplistCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0) {
                    List<YunReData> yunReDataList= YunService.StringToListYunReData(phalEntity.getData());
                    if(yunReDataList!=null&&yunReDataList.size()>0) {
                        for (int i = 0; i < yunReDataList.size(); i++) {
                            YunReData yunReData = yunReDataList.get(i);
                            Appliance appliance = new Appliance();
                            appliance.setId(yunReData.getId());
                            appliance.setAppId(yunReData.getAppid());
                            appliance.setName(Utils.utf8decode(yunReData.getName()));
                            appliance.setImageId(yunReData.getImageid1());
                            appliance.setImageId2(yunReData.getImageid2());
                            appliance.setModeNum(yunReData.getModenum());
                            appliance.setOnline(false);
                            appliance.setMode(0);
                            appliance.setWaste(0);
                            if(appliance.getAppId()==0)
                            {
                                appliance.setName("总功耗");
                                device.setAllapp(appliance);
                            }
                            else {
                                applianceList.add(appliance);
                                tempappList.add(appliance);
                            }

                        }
                        device.setApplianceArrayList(tempappList);
                        app.setDevice(device,device.getTableid());
                        Utils.toastShow(DqActivity.this,"电器列表获取成功！");
                    }
                }
                else
                {
                    device = app.getDevice(device.getTableid());
                    applianceList = device.getApplianceArrayList();
                    tempappList = device.getApplianceArrayList();
                    device.setApplianceArrayList(tempappList);
                    Utils.toastShow(DqActivity.this,"电器列表获取失败！");
                }
                dqItemAdapter.notifyDataSetChanged();
                getwasteflag = true;
            }
        };
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("hard_id",device.getTableid()+"");
        params.add(rp1);
        RemoteService.getInstance().invoke(this, "App_Getapplist", params,
                getApplistCallback);
    }


    private void getWasteList()
    {
        //更新回调函数
        RequestCallback getWasteListCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0) {
                    List<YunReData> yunReDataList= YunService.StringToListYunReData(phalEntity.getData());
                    if(yunReDataList!=null&&yunReDataList.size()>0) {
                        for (int i = 0; i < yunReDataList.size(); i++) {
                            YunReData yunReData = yunReDataList.get(i);
                            if(yunReData.getAppid()==0)
                            {
                                device.getAllapp().setWaste(yunReData.getWaste());
                                mainButtonArrayList.get(3).setTable(yunReData.getWaste());
                            }
                            else
                            {
                                device.getApplianceArrayList().get(yunReData.getAppid()-1).setWaste(yunReData.getWaste());
                            }
                        }
                        dqItemAdapter.notifyDataSetChanged();
                        dqTopItemAdapter.notifyDataSetChanged();
                    }
                }
                else
                {

                }
            }
        };
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("hard_id",device.getTableid()+"");
        params.add(rp1);
        RemoteService.getInstance().invoke(this, "Waste_Gettodaywaste", params,
                getWasteListCallback);
    }

    private void upDataAppList(int[] receData){
        if(receData.length>0) {
            applianceList.removeAll(applianceList);
            for (int i = 0; i < tempappList.size(); i++) {
                tempappList.get(i).setOnline(false);
            }
            for (int i = 0; i < receData.length; i++) {
                for (int j = 0; j < tempappList.size(); j++) {
                    if ((j + 1) == (receData[i] & 0xff)) {
                        int ttt = receData[i] / 256;
                        tempappList.get(j).setMode(ttt);
                        tempappList.get(j).setOnline(true);
                    }
                }
            }
            for (int i = 0; i < tempappList.size(); i++) {
                if (tempappList.get(i).getOnline() == true) {
                    applianceList.add(tempappList.get(i));
                }
            }
            for (int i = 0; i < tempappList.size(); i++) {
                if (tempappList.get(i).getOnline() == false) {
                    applianceList.add(tempappList.get(i));
                }
            }
            dqItemAdapter.notifyDataSetChanged();
        }
        else {
        }
    }
}
