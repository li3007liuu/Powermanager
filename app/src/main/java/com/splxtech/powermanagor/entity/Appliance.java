package com.splxtech.powermanagor.entity;

import java.io.Serializable;

/**
 * Created by li300 on 2016/10/6 0006.
 */

public class Appliance implements Serializable //implements Parcelable
{
    private int appId;
    private int id;
    private int imageId;
    private int imageId2;
    private boolean online;
    private String name;
    private int modeNum;
    private int mode;
    private int waste;
    //private AppWasteAll appWasteAll;
    public static final long serialVersionUID = 9527L;

    public Appliance () {
        super();
    }
    public Appliance(int appId,String name,int imageId,int imageId2)
    {
        super();
        this.appId = appId;
        this.imageId = imageId;
        this.imageId2 = imageId2;
        this.name = name;
    }
    public Appliance(int appId,int id,int imageId,int imageId2,String name,int modeNum )
    {
        super();
        this.appId = appId;
        this.id = id;
        this.imageId = imageId;
        this.imageId2 = imageId2;
        this.name = name;
        this.modeNum = modeNum;
    }
    public Appliance(int appId,int id,int imageId,int imageId2,String name,int modeNum,boolean line )
    {
        super();
        this.appId = appId;
        this.id = id;
        this.imageId = imageId;
        this.imageId2 = imageId2;
        this.name = name;
        this.modeNum = modeNum;
        this.online = line;
    }
    public boolean getOnline()
    {
        return online;
    }
    public void setOnline(boolean a)
    {
        online = a;
    }
    public int getImageId2()
    {
        return imageId2;
    }
    public void setImageId2(int a)
    {
        imageId2 = a;
    }
    public int getOnlineImage()
    {
        if(online==false)
            return imageId2;
        else
            return imageId;
    }
    public int getAppId()
    {
        return appId;
    }
    public int getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public int getImageId()
    {
        return imageId;
    }

    public void setAppId(int a)
    {
        appId=a;
    }
    public void setId(int a)
    {
        id = a;
    }
    public void setImageId(int a)
    {
        imageId = a;
    }
    public void setName(String a)
    {
        name = a;
    }

    public void setMode(int a){
        mode = a;
    }
    public int getMode(){
        return mode;
    }
    public void setModeNum(int a)
    {
        modeNum = a;
    }
    public int getModeNum()
    {
        return modeNum;
    }

    public int getWaste()
    {
        return waste;
    }
    public void setWaste(int a)
    {
        waste = a;
    }
   // public void setAppWasteAll(AppWasteAll a)
   // {
   //     appWasteAll = a;
    //}
    //public AppWasteAll getAppWasteAll(){
    //    return appWasteAll;
    //}

}
