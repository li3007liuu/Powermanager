package com.splxtech.powermanagor.activity.dqjc;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
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
import com.splxtech.powermanagor.entity.AppWasteAll;
import com.splxtech.powermanagor.ui.CircularRingPercentageView;

import java.util.ArrayList;

/**
 * Created by li300 on 2016/10/12 0012.
 */

public class ToDayDissFragment extends Fragment {
    private BarChart barChart;
    private CircularRingPercentageView progressCircle;
    private TextView timeText;
    private TextView wasteText;
    private TextView moneyText;
    private Button nextButton;
    private String time;


    int waste=0;
    int[] todaywaste = new int[12];
    float money=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //getData();
        View view = inflater.inflate(R.layout.fragment_todaydiss,container,false);
        barChart = (BarChart)view.findViewById(R.id.ftodaydiss_barchart);
        progressCircle = (CircularRingPercentageView)view.findViewById(R.id.ftodaydiss_progress);
        progressCircle.setMaxColorNumber(100);
        timeText = (TextView)view.findViewById(R.id.ftodaydiss_text_title);
        wasteText = (TextView)view.findViewById(R.id.ftodaydiss_text_waste);
        moneyText = (TextView)view.findViewById(R.id.ftodaydiss_text_money);
        nextButton = (Button)view.findViewById(R.id.ftodaydiss_button_next);
        return view;
    }



    @Override
    public void onStart()
    {
        super.onStart();
        float process = ((float) waste)/38*100;
        if(process>100)
        {
            process = 100;
        }
        progressCircle.setProgress(process);
        setBarChart(barChart);
        setBarData(barChart);
        if(time=="今天")
        {
            nextButton.setEnabled(false);
        }
        timeText.setText(time);
        wasteText.setText(waste+"");
        moneyText.setText(money+"");
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDayDissActivity toDayDissActivity =(ToDayDissActivity)getActivity();
                toDayDissActivity.mViewPager.setCurrentItem(19);
            }
        });
    }
    public void setData(float w ,float[] to){
        waste = (int)w;
        money = (float)( waste*0.53);
        for(int i=0;i<12;i++)
        {
            todaywaste[i] = (int)(to[i]);
        }
    }
    /*
    private void getData()
    {
        for(int i=0;i<12;i++)
        {
            todaywaste[i] = (int)(Math.random()*3);
            waste = waste +todaywaste[i];
        }
        money = (float)(waste*0.53);
    }
    */
    public void setTime(String aa)
    {
        time = aa;

    }
    private void setBarChart(BarChart mChart)
    {
        mChart.setDrawBarShadow(true);
        mChart.setDrawValueAboveBar(false);
        mChart.setDescription("");
        mChart.setDescriptionColor(Color.WHITE);

        mChart.setMaxVisibleValueCount(12);
        mChart.setPinchZoom(false);
        //设置不缩放
        mChart.setScaleXEnabled(false);
        //mChart.setScaleX(2);
        mChart.setScaleYEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        XAxis xAxis = mChart.getXAxis();
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
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(8);
        xAxis.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(6,false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextSize(12f);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f);
        //关闭右侧坐标轴显示
        mChart.getAxisRight().setEnabled(false);
        Legend l = mChart.getLegend();
        l.setEnabled(false);
        /*
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);*/

        //BarData data1 = new BarData();
        //data1.setValueTextSize(10f);
        //data1.setBarWidth(0.9f);
        //mChart.setData(data1);
    }

    private void setBarData(BarChart mChart)
    {
        float start = 0f;
        mChart.getXAxis().setAxisMinValue(start);
        mChart.getXAxis().setAxisMaxValue(start+13);
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for(int i= (int)start;i<(start+12);i++)
        {
            yVals1.add(new BarEntry(i+1f,todaywaste[i]));
        }
        BarDataSet set1;
        if(mChart.getData()!=null&&mChart.getData().getDataSetCount()>0)
        {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        }
        else
        {
            set1 = new BarDataSet(yVals1, "");
            set1.setColors(Color.argb(255,0x5a,0xa9,0xf9));
            set1.setBarShadowColor(Color.argb(0xff,0xdd,0xdd,0xdd));
            set1.setHighLightColor(Color.argb(255,0x5a,0xa9,0xf9));
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            BarData data1 = new BarData(dataSets);
            data1.setDrawValues(true);
            data1.setBarWidth(0.23f);
            mChart.setData(data1);
        }
    }

}
