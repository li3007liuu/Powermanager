package com.splxtech.powermanagor.activity.dqjc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.splxtech.powermanagor.Base.MqttBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.engine.MqttService;
import com.splxtech.powermanagor.entity.Appliance;
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.powermanagor.utils.AppProtocol;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by li300 on 2016/10/15 0015.
 */

public class DlcsActivity extends MqttBaseActivity {

    private LineChart lineChart;
    private TextView dlyxzText;
    private TextView dyyxzText;
    private TextView plText;
    private TextView xwjText;
    private TextView ygglText;
    private TextView szglText;
    private TextView glyzText;
    private Button backButton;
    private ElectricityMeter device;
    private BaseActivity baseActivity=this;

    private ArrayList<Entry> yVals1 = new ArrayList<>();
    private ArrayList<Float> yValsf = new ArrayList<>();

    private boolean flag = false;//查询定时器的开启与关闭标志位
    private boolean sflag = false;//消息是否发送标志位
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(flag==true) {
                if(sflag==true) {
                    sendMessage();
                }
                try {
                    handler.postDelayed(this, 500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    @Override
    protected void initVariables()
    {
        Intent intent = this.getIntent();
        Bundle bundle = (Bundle)intent.getBundleExtra("dvbundle");
        device =  (ElectricityMeter)bundle.getSerializable("device");
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
                    if(topic.equals(DqActivity.hardrtopic+device.getProductId())) {
                        int[] receData = AppProtocol.getReceiverData(AppProtocol.DLCSPORTNUM, data);
                        upDataText(receData);
                    }
                }
                else
                {

                }
            }
        };
    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_dlcs);
        lineChart = (LineChart)findViewById(R.id.dlcs_linechart_content);
        setLineChart();
        dlyxzText = (TextView)findViewById(R.id.dlcs_text_dlyxz);
        dyyxzText = (TextView)findViewById(R.id.dlcs_text_dyyxz);
        plText = (TextView)findViewById(R.id.dlcs_text_pl);
        xwjText = (TextView)findViewById(R.id.dlcs_text_xwj);
        ygglText = (TextView)findViewById(R.id.dlcs_text_yggl);
        szglText = (TextView)findViewById(R.id.dlcs_text_szgl);
        glyzText = (TextView)findViewById(R.id.dlcs_text_glys);
        backButton = (Button)findViewById(R.id.dlcs_button_return);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    public void loadData()
    {
        handler.postDelayed(runnable,500);
        flag = true;
    }
    @Override
    public void onStart(){
        super.onStart();
        sflag = true;
        //第一个参数的意思是初始化之后多久执行timertask,第二个参数是间隔多久执行一次timertask
        //timer.schedule(timerTask,250,500);
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
        flag = false;
        super.onDestroy();
    }

    private void upDataText(int [] receData){
        if(receData.length==7)
        {
            float dyyxz = ((float)receData[0])/100;
            float dlyxz = ((float)receData[1])/1000;
            float pl = Utils.int_pl(receData[2]);
            float xwj = ((float)receData[3])/100;
            float yggl = ((float)receData[4])/1000;
            float wggl = ((float)receData[5])/1000;
            float glys = ((float)receData[6])/10000;
            dlyxzText.setText(dlyxz+"");
            dyyxzText.setText(dyyxz+"");
            plText.setText(pl+"");
            xwjText.setText(xwj+"");
            ygglText.setText(yggl+"");
            szglText.setText(wggl+"");
            glyzText.setText(glys+"");
            setData(dlyxz);
        }
        else
        {

        }
    }

    private void sendMessage()
    {
        try {
            String Message = new String(AppProtocol.getSendData(AppProtocol.DLCSPORTNUM, 0),"ISO-8859-1");
            try {
                iMqttService.mqttPubMessage(DqActivity.hardstopic+device.getProductId(), Message, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void setLineChart()
    {
        lineChart.setTouchEnabled(true);
        lineChart.setDragDecelerationEnabled(true);
        lineChart.setDragDecelerationFrictionCoef(0.9f);
        lineChart.setDragEnabled(true);
        lineChart.setDescriptionColor(Color.WHITE);
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleYEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setPinchZoom(false);
        LineData data = new LineData();
        lineChart.setData(data);
        //不要图例
        Legend legend = lineChart.getLegend();
        //legend.setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(false);
        YAxis lyAxis = lineChart.getAxisLeft();
        //lyAxis.setEnabled(false);
        lyAxis.setTextColor(Color.BLACK);
        lyAxis.setDrawGridLines(false);
        lyAxis.setGranularityEnabled(true);
        lyAxis.setAxisMaxValue(20f);
        lyAxis.setMinWidth(0f);
        //lyAxis.setEnabled(false);
        YAxis ryAxis = lineChart.getAxisRight();
        ryAxis.setEnabled(false);
    }
    private void setData(float disdata)
    {
        lineChart.clear();
        if(yValsf.size()<100)
        {
            yValsf.add(new Float(disdata));
        }
        else
        {
            yValsf.remove(0);
            yValsf.add(new Float(disdata));
        }
        yVals1.removeAll(yVals1);
        for(int i=0;i<yValsf.size();i++){
            yVals1.add(new Entry(i,yValsf.get(i)));
        }
        LineDataSet set1;
        if(lineChart.getData()!=null
                &&lineChart.getData().getDataSetCount()>0)
        {
            set1 = (LineDataSet)lineChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);

            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        }
        else
        {

            set1 = new LineDataSet(yVals1,"电流");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(Color.rgb(252,104,15));
            set1.setLineWidth(1.5f);
            //不填充
            set1.setDrawFilled(false);
            set1.setHighLightColor(Color.argb(0,0xff,0xff,0xff));
            set1.setDrawCircles(false);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            data.setDrawValues(false);
            lineChart.setData(data);
            lineChart.invalidate();
        }
    }
}
