package com.splxtech.powermanagor.entity;

/**
 * Created by li300 on 2016/9/24 0024.
 */

public class YjglItem
{
    private String name;
    private boolean checked;
    private int icon;
    public YjglItem(String nn,boolean cc,int ii)
    {
        name = nn;
        checked = cc;
        icon = ii;
    }
    public String getName ()
    {
        return name;
    }
    public boolean getChecked()
    {
        return checked;
    }
    public int getIcon()
    {
        return icon;
    }
    public void setName(String nn)
    {
        name = nn;
    }
    public void setChecked(boolean cc)
    {
        checked = cc;
    }
    public void setIcon(int ii)
    {
        icon = ii;
    }
}
