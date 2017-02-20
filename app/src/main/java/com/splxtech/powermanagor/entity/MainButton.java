package com.splxtech.powermanagor.entity;

/**
 * Created by li300 on 2016/9/24 0024.
 */

public class MainButton {
    private int imageId;
    private String name;
    private String table;

    public MainButton(String name,int imageid)
    {
        this.name = name;
        this.imageId = imageid;
        this.table = "";
    }
    public MainButton(String name,int imageId,String table)
    {
        this.name = name;
        this.imageId = imageId;
        this.table = table;
    }
    public void setImageId(int i)
    {
        imageId = i;
    }
    public void setName(String n)
    {
        name = n;
    }
    public int getImageId()
    {
        return imageId;
    }
    public String getName()
    {
        return name;
    }
    public String getTable() {return table;}
    public void setTable(int a)
    {
        String tt = a+"";
        if(tt.length()>2)
        {
            table = tt.substring(0,2);
        }
        else
        {
            table = tt;
        }
    }
}
