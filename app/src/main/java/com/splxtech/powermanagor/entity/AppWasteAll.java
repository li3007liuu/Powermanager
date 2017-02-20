package com.splxtech.powermanagor.entity;


import com.splxtech.powermanagor.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by li300 on 2016/10/26 0026.
 */

public class AppWasteAll implements Serializable
{
    private ArrayList<Float> dayWasteList;
    private ArrayList<Float> weeksWasteList;
    private ArrayList<Float> monthsWasteList;
    private ArrayList<DayWaste> hourWasteList;
    private long upTime;
    public static final long serialVersionUID = 9530L;
    //将数据初始化为0
    public AppWasteAll(){
        dayWasteList = new ArrayList<>();
        weeksWasteList = new ArrayList<>();
        monthsWasteList = new ArrayList<>();
        hourWasteList = new ArrayList<>();
        for(int i=0;i<20;i++)
        {
            dayWasteList.add(new Float(0));
            hourWasteList.add(new DayWaste());
        }
        for(int i=0;i<16;i++)
        {
            weeksWasteList.add(new Float(0));
        }
        for(int i=0;i<12;i++)
        {
            monthsWasteList.add(new Float(0));
        }
        upTime = Utils.getTime();
    }

    public ArrayList<Float> getDayWasteList()
    {
        return dayWasteList;
    }
    public ArrayList<Float> getWeeksWasteList()
    {
        return weeksWasteList;
    }
    public ArrayList<Float>getMonthsWasteList()
    {
        return monthsWasteList;
    }
    public ArrayList<DayWaste> getHourWasteList()
    {
        return hourWasteList;
    }
    //设置某一天的功耗
    public void setDayWaste(DayWaste dayWaste)
    {
        float temp=0;
        for(int i=0;i<12;i++){
            temp = dayWaste.daywaste[i] + temp;
        }
        long nowTime = Utils.getTime();
        long btweenDay =  (nowTime-upTime)/(1000*3600*24);
        int btday = Integer.parseInt(String.valueOf(btweenDay));
        setDayWasteList(dayWaste,btday);
        setWeeksWasteList(dayWaste,btday);
        setMouthWasteList(dayWaste,btday);
    }

    //第一个参数为当天的功耗统计尾为最新日期，删除最前面的日期
    private void setDayWasteList(DayWaste dayWaste,int a)
    {
        if(a>0)
        {
            for(int i=0;i<a;i++){
                hourWasteList.remove(0);
                dayWasteList.remove(0);
                if(i==(a-1)){
                    dayWasteList.add(new Float(getDayWasteByHour(dayWaste)));
                    hourWasteList.add(dayWaste);
                }
                else
                {
                    dayWasteList.add(new Float(0));
                    hourWasteList.add(new DayWaste());
                }
            }
        }
        else if(a==0)
        {
            hourWasteList.remove(hourWasteList.size()-1);
            hourWasteList.add(dayWaste);
            dayWasteList.remove(dayWasteList.size()-1);
            dayWasteList.add(new Float(getDayWasteByHour(dayWaste)));
        }
    }

    private void setWeeksWasteList(DayWaste dayWaste,int a){
        int b = a/7;
        int c = Utils.getDayOfWeek(0); //c==1 周日
        //如果是本周
        if(b==0)
        {
            //如果是星期天，则添加新一周数据
            if(c==1)
            {
                weeksWasteList.remove(0);
                weeksWasteList.add(new Float(getDayWasteByHour(dayWaste)));
            }
            else
            {
                if(weeksWasteList.size()==16) {
                    Float temp = weeksWasteList.get(15);
                    float ww = temp + getDayWasteByHour(dayWaste);
                    weeksWasteList.remove(15);
                    weeksWasteList.add(new Float(ww));
                }
            }
        }
        else if(b>0)
        {
            for(int i=0;i<b;i++)
            {
                weeksWasteList.remove(0);
                weeksWasteList.add(new Float(0));
            }
            if(c==1)
            {
                weeksWasteList.remove(0);
                weeksWasteList.add(new Float(getDayWasteByHour(dayWaste)));
            }
            else
            {
                if(weeksWasteList.size()==16) {
                    Float temp = weeksWasteList.get(15);
                    float ww = temp + getDayWasteByHour(dayWaste);
                    weeksWasteList.remove(15);
                    weeksWasteList.add(new Float(ww));
                }
            }
        }
    }

    private void setMouthWasteList(DayWaste dayWaste,int a){
        int b =Utils.getBetweenMonth(a);
        int c = Utils.getDay(0); //c==1 周日
        //如果是本月
        if(b==0)
        {
            //如果是星期天，则添加新一周数据
            if(c==1)
            {
                monthsWasteList.remove(0);
                monthsWasteList.add(new Float(getDayWasteByHour(dayWaste)));
            }
            else
            {
                if(monthsWasteList.size()==12) {
                    Float temp = monthsWasteList.get(11);
                    float ww = temp + getDayWasteByHour(dayWaste);
                    monthsWasteList.remove(11);
                    monthsWasteList.add(new Float(ww));
                }
            }
        }
        else if(b>0)
        {
            for(int i=0;i<b;i++)
            {
                monthsWasteList.remove(0);
                monthsWasteList.add(new Float(0));
            }
            if(c==1)
            {
                monthsWasteList.remove(0);
                monthsWasteList.add(new Float(getDayWasteByHour(dayWaste)));
            }
            else
            {
                if(monthsWasteList.size()==12) {
                    Float temp = monthsWasteList.get(11);
                    float ww = temp + getDayWasteByHour(dayWaste);
                    monthsWasteList.remove(11);
                    monthsWasteList.add(new Float(ww));
                }
            }
        }
    }

    private Float getDayWasteByHour(DayWaste dayWaste){
        float temp=0;
        for(int i=0;i<12;i++)
        {
            temp=dayWaste.daywaste[i]+temp;
        }
        return new Float(temp);
    }
}
