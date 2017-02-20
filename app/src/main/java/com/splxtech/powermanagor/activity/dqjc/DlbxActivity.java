package com.splxtech.powermanagor.activity.dqjc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

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
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.powermanagor.utils.AppProtocol;
import com.splxtech.powermanagor.utils.Utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by li300 on 2016/10/15 0015.
 */

public class DlbxActivity extends MqttBaseActivity {
    private Button backButton;
    private LineChart dlChart;
    private LineChart dyChart;

    private int BxtNum = 128;
    //private int jj=0;
    private ElectricityMeter device;
   // private float[] initData = new float[BxtNum*2];

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
                    String message = "";
                    byte[] data = intent.getByteArrayExtra("Message");
                    if(topic.equals(DqActivity.hardrtopic+device.getProductId())) {
                        for (int i = 0; i < data.length; i++) {
                            message = message + data[i] + "\t";
                        }
                        int[] receData = AppProtocol.getReceiverData(AppProtocol.DLBXPORTNUM, data);
                        upDataChart(receData);
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

        setContentView(R.layout.activity_dlbx);
        backButton = (Button)findViewById(R.id.dlbx_button_return);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dlChart = (LineChart)findViewById(R.id.dlbx_linechart_dl);
        dyChart = (LineChart)findViewById(R.id.dlbx_linechart_dy);
        setChart(dlChart);
        setChart(dyChart);


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
        //第一个参数的意思是初始化之后多久执行timertask,第二个参数是间隔多久执行一次timertask
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
        flag = false;
        super.onDestroy();
    }
    private void setChart(LineChart lineChart){
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
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        legend.setTextSize(14f);
        //legend.setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(false);
        YAxis lyAxis = lineChart.getAxisLeft();
        lyAxis.setTextColor(Color.BLACK);
        lyAxis.setDrawGridLines(false);
        lyAxis.setGranularityEnabled(true);
        //lyAxis.setEnabled(false);
        YAxis ryAxis = lineChart.getAxisRight();
        ryAxis.setEnabled(false);
    }
    private void setData(float[] disdata,LineChart lineChart,boolean flag)
    {
        lineChart.clear();
        ArrayList<Entry> yVals1 = new ArrayList<>();
        for(int i=0;i<BxtNum;i++){
            if(flag==false)
                yVals1.add(new Entry(i,disdata[i+BxtNum]));
            else
                yVals1.add(new Entry(i,disdata[i]));
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
            if(flag==false)
            {
                set1 = new LineDataSet(yVals1,"电流波形图");
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(Color.rgb(252,104,15));
            }
            else
            {
                set1 = new LineDataSet(yVals1,"电压波形图");
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(Color.rgb(3,154,244));
            }
            set1.setLineWidth(1.5f);
            //不填充
            set1.setDrawFilled(false);
            //set1.setFillAlpha(65);
            //set1.setFillColor(Color.WHITE);
            set1.setHighLightColor(Color.argb(0,0xff,0xff,0xff));
            set1.setDrawCircles(false);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            data.setDrawValues(true);
            //data.setValueTextColor(Color.BLACK);
            //data.setValueTextSize(16f);

            lineChart.setData(data);
            lineChart.invalidate();

        }
    }

    private void sendMessage()
    {
        try {
            String Message = new String(AppProtocol.getSendData(AppProtocol.DLBXPORTNUM, 0),"ISO-8859-1");
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
    private void upDataChart(int[] receData){

        if(receData.length>255){
            float[] fdata=new float[receData.length];
            for(int i=0;i<receData.length;i++)
            {
                fdata[i] = (float)receData[i];
            }
            setData(fdata,dlChart,false);
            setData(fdata,dyChart,true);
        }
    }
}
