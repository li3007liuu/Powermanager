package com.splxtech.powermanagor;

import android.app.Application;
import android.os.Environment;

import com.splxtech.powermanagor.entity.AppWasteAll;
import com.splxtech.powermanagor.entity.Appliance;
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.powermanagor.entity.PMUserInfo;
import com.splxtech.powermanagor.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by li300 on 2016/10/15 0015.
 */

public class PowerManagerApp extends Application {
    private PMUserInfo pmUserInfo;
    private List<ElectricityMeter> electricityMeters;
    private ElectricityMeter device;
    private AppWasteAll appWasteAll;
    private String electricityMeterPath = Environment.getExternalStorageDirectory().toString()
            + File.separator +"PowerManager"+File.separator + "objdata"+File.separator;
    public PMUserInfo getPmUserInfo()
    {
        if(pmUserInfo==null)
        {
            pmUserInfo = Utils.readUserInfo(this);
        }
        return pmUserInfo;
    }
    public void setPmUserInfo(PMUserInfo pm)
    {
        pmUserInfo = pm;
    }
    public List<ElectricityMeter> getElectricityMeters(int userid)
    {
        if(electricityMeters==null)
        {
            String fname= electricityMeterPath+"electricitymeter"+userid+".dat";
            electricityMeters = (List<ElectricityMeter>)Utils.restoreObject(fname);
        }
        return electricityMeters;
    }
    public void setElectricityMeters(List<ElectricityMeter> e,int userid)
    {
        String fname= electricityMeterPath+"electricitymeter"+userid+".dat";
        Utils.saveObject(fname,e);
        electricityMeters = e;
    }
    public void setDevice(ElectricityMeter e,int hardid)
    {
        String fname= electricityMeterPath+"device"+hardid+".dat";
        Utils.saveObject(fname,e);
        device = e;
    }
    public ElectricityMeter getDevice(int hardid)
    {
        if(device==null)
        {
            String fname= electricityMeterPath+"device"+hardid+".dat";
            device = (ElectricityMeter) Utils.restoreObject(fname);
        }
        return device;
    }
    public void setAppWasteAll(AppWasteAll e,int appid)
    {
        String fname= electricityMeterPath+"allwaste"+appid+".dat";
        Utils.saveObject(fname,e);
        appWasteAll = e;
    }
    public AppWasteAll getAppWasteAll(int appid)
    {
        if(appWasteAll==null)
        {
            String fname= electricityMeterPath+"allwaste"+appid+".dat";
            appWasteAll = (AppWasteAll)Utils.restoreObject(fname);
            if(appWasteAll==null)
            {
                appWasteAll = new AppWasteAll();
            }
        }
        return appWasteAll;
    }
}
