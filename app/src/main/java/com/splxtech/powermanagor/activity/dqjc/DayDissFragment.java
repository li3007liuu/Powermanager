package com.splxtech.powermanagor.activity.dqjc;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.entity.Appliance;

import java.util.ArrayList;

/**
 * Created by li300 on 2016/11/8 0008.
 */

public class DayDissFragment extends Fragment {
    private HorizontalBarChart hbarChart;
    private BarChart barChart;
    private Button returnButton;
    private TextView dqw1,dqw2,dqw3,dqw4,dqw5,dqw6;
    private TextView dqp1,dqp2,dqp3,dqp4,dqp5,dqp6;
    private TextView timeText,wasteText,moneyText;
    private ArrayList<String> hchartName = new ArrayList<>();
    private ArrayList<Float>  hchartData = new ArrayList<>();

    private String time;
    private int index=0;
    private ArrayList<Appliance> appliancesList = new ArrayList<>();

    private float   allWaste=0;
    private int[]   appPecent=new int[6];
    private float[] appWaste = new float[6];
    private String[] appName = new String[6];
    private float[][] apphWaste = new float[6][12];
    private int dissNum=0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_daydiss,container,false);
        hbarChart = (HorizontalBarChart)view.findViewById(R.id.fdaydiss_hbarchart);
        barChart = (BarChart)view.findViewById(R.id.fdaydiss_linechart);
        returnButton = (Button)view.findViewById(R.id.fdaydiss_button_next);
        dqw1 = (TextView)view.findViewById(R.id.fdaydiss_text_dqw1);
        dqw2 = (TextView)view.findViewById(R.id.fdaydiss_text_dqw2);
        dqw3 = (TextView)view.findViewById(R.id.fdaydiss_text_dqw3);
        dqw4 = (TextView)view.findViewById(R.id.fdaydiss_text_dqw4);
        dqw5 = (TextView)view.findViewById(R.id.fdaydiss_text_dqw5);
        dqw6 = (TextView)view.findViewById(R.id.fdaydiss_text_dqw6);
        dqp1 = (TextView)view.findViewById(R.id.fdaydiss_text_dqp1);
        dqp2 = (TextView)view.findViewById(R.id.fdaydiss_text_dqp2);
        dqp3 = (TextView)view.findViewById(R.id.fdaydiss_text_dqp3);
        dqp4 = (TextView)view.findViewById(R.id.fdaydiss_text_dqp4);
        dqp5 = (TextView)view.findViewById(R.id.fdaydiss_text_dqp5);
        dqp6 = (TextView)view.findViewById(R.id.fdaydiss_text_dqp6);
        timeText = (TextView)view.findViewById(R.id.fdaydiss_text_title);
        wasteText = (TextView)view.findViewById(R.id.fdaydiss_text_waste);
        moneyText = (TextView)view.findViewById(R.id.fdaydiss_text_money);
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //AllWasteActivity allWasteActivity = (AllWasteActivity)getActivity();
        //appliancesList  = allWasteActivity.getApplianceList();
        sortData();
        wasteText.setText((int)(allWaste)+"");
        moneyText.setText((int)(allWaste*0.53)+"");
        setTextDis();
        setHbarString();
        setHbarChart(hbarChart);
        setHchartData();
        hbarChart.setFitBars(true);
        setBarChart();
        setBarData();
        barChart.setFitBars(true);
        if(time=="今天")
        {
            returnButton.setEnabled(false);
        }
        timeText.setText(time);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllWasteActivity allWasteActivity = (AllWasteActivity)getActivity();
                allWasteActivity.mViewPager.setCurrentItem(19);
            }
        });
    }
    public void setTime(String aa)
    {
        time = aa;

    }
    public void setIndex(int i)
    {
        index = i;
    }
    private void sortData()
    {
        allWaste =0;
        float[] tempWaste = new float[appliancesList.size()];
        String[] tempName = new String[appliancesList.size()];
        float[][] temphWaste = new float[6][12];
        //第一步赋值
        for(int i=0;i<appliancesList.size();i++)
        {
            tempWaste[i] = 0;//appliancesList.get(i).getAppWasteAll().getDayWasteList().get(index).floatValue();
            allWaste = tempWaste[i] + allWaste;
            tempName[i] = appliancesList.get(i).getName();
            for(int j=0;j<12;j++)
            {
                float var = 0;//appliancesList.get(i).getAppWasteAll().getHourWasteList().get(index).daywaste[j];
                temphWaste[i][j]=var;
            }
        }
        //第二步排序
        if(tempWaste.length>=2)
        {
            for(int i=0;i<tempWaste.length-1;i++)
            {
                for(int j=0;j<tempWaste.length-i-1;j++)
                {
                    if(tempWaste[j]<tempWaste[j+1])
                    {
                        float temp = tempWaste[j];
                        tempWaste[j] = tempWaste[j+1];
                        tempWaste[j+1] = temp;
                        String ss = tempName[j];
                        tempName[j] = tempName[j+1];
                        tempName[j+1] = ss;
                        float[] tempf = temphWaste[j];
                        temphWaste[j] = temphWaste[j+1];
                        temphWaste[j+1] = tempf;
                    }
                }
            }
        }

        //第三步计算赋值
        for(int i=0;i<6;i++)
        {
            if(tempWaste.length<=6) {
                dissNum = tempWaste.length;
                if (i < tempWaste.length) {
                    if(allWaste>0) {
                        appPecent[i] = (int) (tempWaste[i] / allWaste * 100);
                    }
                    else
                    {
                        appPecent[i] = 0;
                    }
                    appWaste[i] = tempWaste[i];
                    appName[i] = tempName[i];
                    apphWaste[i] = temphWaste[i];
                } else {
                    appPecent[i] = 0;
                    appWaste[i] = 0;
                    appName[i] = "";
                    apphWaste[i] = new float[12];
                }
            }
            else{
                dissNum = 6;
                if(i<5)
                {
                    if(allWaste>0) {
                        appPecent[i] = (int) (tempWaste[i] / allWaste * 100);
                    }
                    else
                    {
                        appPecent[i]=0;
                    }
                    appWaste[i] = tempWaste[i];
                    appName[i] = tempName[i];
                    apphWaste[i] = temphWaste[i];
                }
                else
                {
                    float[] floats= new float[12];
                    for(int k=5;k<tempName.length;k++){
                        appWaste[5] = tempWaste[k]+appWaste[5];
                        //apphWaste[5] = temphWaste[k];
                        for(int h=0;h<12;h++)
                        {
                            floats[h]= temphWaste[k][h]+floats[h];
                        }
                    }
                    for(int k=0;k<12;k++)
                    {
                        apphWaste[5][k]=floats[k];
                    }
                    appName[5] = "其他";
                    if(allWaste>0) {
                        appPecent[5] = (int) (appWaste[5] / allWaste * 100);
                    }
                    else
                    {
                        appPecent[5]=0;
                    }
                }
            }
        }
    }


    private void setHbarString()
    {
        if(hchartName.size()==0)
        {
            for(int i=0;i<6;i++)
            {
                hchartName.add(appName[5-i]);
            }
        }
    }

    private void setTextDis()
    {
        if(dissNum>0)
        {
            dqw1.setText((int)appWaste[0]+"");
            dqp1.setText(appPecent[0]+"%");
            if(dissNum>1)
            {
                dqw2.setText((int)appWaste[1]+"");
                dqp2.setText(appPecent[1]+"%");
                if(dissNum>2)
                {
                    dqw3.setText((int)appWaste[2]+"");
                    dqp3.setText(appPecent[2]+"%");
                    if(dissNum>3)
                    {
                        dqw4.setText((int)appWaste[3]+"");
                        dqp4.setText(appPecent[3]+"%");
                        if(dissNum>4)
                        {
                            dqw5.setText((int)appWaste[4]+"");
                            dqp5.setText(appPecent[4]+"%");
                            if(dissNum>5)
                            {
                                dqw6.setText((int)appWaste[5]+"");
                                dqp6.setText(appPecent[5]+"%");
                            }
                            else
                            {
                                dqw6.setText("");
                                dqp6.setText("");
                            }
                        }
                        else
                        {
                            dqw5.setText("");
                            dqp5.setText("");
                            dqw6.setText("");
                            dqp6.setText("");
                        }
                    }
                    else
                    {
                        dqw4.setText("");
                        dqp4.setText("");
                        dqw5.setText("");
                        dqp5.setText("");
                        dqw6.setText("");
                        dqp6.setText("");
                    }
                }
                else
                {
                    dqw3.setText("");
                    dqp3.setText("");
                    dqw4.setText("");
                    dqp4.setText("");
                    dqw5.setText("");
                    dqp5.setText("");
                    dqw6.setText("");
                    dqp6.setText("");
                }
            }
            else
            {
                dqw2.setText("");
                dqp2.setText("");
                dqw3.setText("");
                dqp3.setText("");
                dqw4.setText("");
                dqp4.setText("");
                dqw5.setText("");
                dqp5.setText("");
                dqw6.setText("");
                dqp6.setText("");
            }
        }
        else
        {
            dqp1.setText("");
            dqw1.setText("");
            dqw2.setText("");
            dqp2.setText("");
            dqw3.setText("");
            dqp3.setText("");
            dqw4.setText("");
            dqp4.setText("");
            dqw5.setText("");
            dqp5.setText("");
            dqw6.setText("");
            dqp6.setText("");
        }
    }
    private void setHbarChart(HorizontalBarChart hChart){
        hChart.setDrawBarShadow(false);
        hChart.setDrawValueAboveBar(false);
        hChart.setDescription("");
        hChart.setDescriptionColor(Color.WHITE);
        hChart.setMaxVisibleValueCount(7);
        hChart.setPinchZoom(false);
        hChart.setDrawGridBackground(false);
        hChart.setTouchEnabled(false);
        XAxis xAxis = hChart.getXAxis();
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(((int)(value))<=hchartName.size()){
                    return hchartName.get((int)(value));
                }
                else
                {
                    return null;
                }

            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(8);
        xAxis.setTextSize(16f);
        xAxis.setTextColor(Color.rgb(0x5a,0xa9,0xf9));
        xAxis.setAxisLineColor(Color.argb(0,0x5a,0xa9,0xf9));
        YAxis lfAxis = hChart.getAxisLeft();
        lfAxis.setEnabled(false);
        YAxis rfAxis = hChart.getAxisRight();
        rfAxis.setEnabled(false);
        Legend l = hChart.getLegend();
        l.setEnabled(false);
    }
    private void setHchartData()
    {
        float barWidth = 0.1f;
        float spaceForBar = 1f;
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for(int i= 0;i<6;i++)
        {
            float val = (float) (appWaste[5-i]);
            yVals1.add(new BarEntry(i * spaceForBar,val));
        }
        BarDataSet set1;
        if(hbarChart.getData()!=null&&hbarChart.getData().getDataSetCount()>0)
        {
            set1 = (BarDataSet) hbarChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            hbarChart.getData().notifyDataChanged();
            hbarChart.notifyDataSetChanged();
        }
        else
        {
            set1 = new BarDataSet(yVals1, "");
            set1.setColors(Color.argb(255,0x5a,0xa9,0xf9));
            set1.setBarShadowColor(Color.argb(0xff,0xdd,0xdd,0xdd));
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            BarData data1 = new BarData(dataSets);
            data1.setDrawValues(false);
            data1.setBarWidth(barWidth);
            hbarChart.setData(data1);
        }
    }

    private void setBarChart()
    {
        barChart.setDescription("");
        barChart.setDescriptionColor(Color.WHITE);
        barChart.setMaxVisibleValueCount(1);
        barChart.setPinchZoom(false);
        barChart.setScaleXEnabled(false);
        barChart.setScaleYEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setHighlightFullBarEnabled(false);

        YAxis lyAxis = barChart.getAxisLeft();
        lyAxis.setLabelCount(6,false);
        lyAxis.setAxisMinValue(0f);
        lyAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        lyAxis.setDrawGridLines(false);
        lyAxis.setTextSize(12f);
        lyAxis.setSpaceTop(15f);
        lyAxis.setAxisLineColor(Color.argb(0,0,0,0));
        barChart.getAxisRight().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int)(value)*2+":00";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6);
        xAxis.setTextSize(10f);

        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
    }

    private void setBarData(){
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for(int i=0;i<12;i++){
            //
            if(dissNum>0)
            {
                float val1 = (float) apphWaste[0][i];//.get(i).floatValue();
                if(dissNum>1)
                {
                    float val2 = (float) apphWaste[1][i];//.get(i).floatValue();
                    if(dissNum>2)
                    {
                        float val3 = (float) apphWaste[2][i];//.get(i).floatValue();
                        if(dissNum>3)
                        {
                            float val4 = (float) apphWaste[3][i];//.get(i).floatValue();
                            if(dissNum>4)
                            {
                                float val5 = (float) apphWaste[4][i];//.get(i).floatValue();
                                if(dissNum>5)
                                {
                                    float val6 = (float) apphWaste[5][i];//.get(i).floatValue();
                                    yVals1.add(new BarEntry(i,new float[]{val1,val2,val3,val4,val5,val6}));
                                }
                                else
                                {
                                    yVals1.add(new BarEntry(i,new float[]{val1,val2,val3,val4,val5}));
                                }
                            }
                            else
                            {
                                yVals1.add(new BarEntry(i,new float[]{val1,val2,val3,val4}));
                            }
                        }
                        else
                        {
                            yVals1.add(new BarEntry(i,new float[]{val1,val2,val3}));
                        }
                    }
                    else
                    {
                        yVals1.add(new BarEntry(i,new float[]{val1,val2}));
                    }
                }
                else{
                    yVals1.add(new BarEntry(i,new float[]{val1}));
                }
            }
            else
            {
                yVals1.add(new BarEntry(i,new float[]{0}));
            }
           /* float mult = 300;
            float val1 = (float) (Math.random() * mult) + mult / 6;
            float val2 = (float) (Math.random() * mult) + mult / 6;
            float val3 = (float) (Math.random() * mult) + mult / 6;
            float val4 = (float) (Math.random() * mult) + mult / 6;
            float val5 = (float) (Math.random() * mult) + mult / 6;
            float val6 = (float) (Math.random() * mult) + mult / 6;
            yVals1.add(new BarEntry(i,new float[]{val1,val2,val3,val4,val5,val6}));*/
        }
        BarDataSet set1;
        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "");
            set1.setColors(getColors());
            set1.setStackLabels(getTables());//

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setDrawValues(false);
            data.setBarWidth(0.4f);
            barChart.setData(data);
        }
    }

    private int[] getColors()
    {
        switch (dissNum) {
            case 1:
                return new int[]{Color.rgb(49, 194, 124)};
            case 2:
                return new int[]{Color.rgb(49, 194, 124), Color.rgb(247, 147, 41)};
            case 3:
                return new int[]{Color.rgb(49, 194, 124), Color.rgb(247, 147, 41), Color.rgb(243, 102, 102)};

            case 4:
                return new int[]{Color.rgb(49, 194, 124), Color.rgb(247, 147, 41), Color.rgb(243, 102, 102), Color.rgb(0, 174, 239)};

            case 5:
                return new int[]{Color.rgb(49, 194, 124), Color.rgb(247, 147, 41), Color.rgb(243, 102, 102), Color.rgb(0, 174, 239), Color.rgb(255, 205, 0)};

            case 6:
                return new int[]{Color.rgb(49, 194, 124), Color.rgb(247, 147, 41), Color.rgb(243, 102, 102), Color.rgb(0, 174, 239), Color.rgb(255, 205, 0), Color.rgb(232, 106, 204)};

            default:
                return new int[]{Color.WHITE};

        }
    }
    private String[] getTables()
    {
        switch (dissNum) {
            case 1:
                return new String[]{appName[0]};
            case 2:
                return new String[]{appName[0],appName[1]};
            case 3:
                return new String[]{appName[0],appName[1],appName[2]};
            case 4:
                return new String[]{appName[0],appName[1],appName[2],appName[3]};
            case 5:
                return new String[]{appName[0],appName[1],appName[2],appName[3],appName[4]};

            case 6:
                return new String[]{appName[0],appName[1],appName[2],appName[3],appName[4],appName[5]};

            default:
                return new String[]{""};

        }
    }

}
