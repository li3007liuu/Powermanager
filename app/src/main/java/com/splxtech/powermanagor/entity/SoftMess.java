package com.splxtech.powermanagor.entity;

import java.io.Serializable;

/**
 * Created by li300 on 2016/11/4 0004.
 */

public class SoftMess implements Serializable {
    private int process;
    private boolean messageflag;
    private String softversion;
    private String hardversion;
    public static final long serialVersionUID = 9557L;
    public SoftMess()
    {

    }
    public boolean getMessageFlag(){
        return messageflag;
    }
    public int getProcess(){
        return process;
    }
    public String getSoftversion(){
        return softversion;
    }
    public String getHardversion(){
        return hardversion;
    }
    public void setMessageflag(boolean a){
        messageflag = a;
    }
    public void setProcess(int a){
        process = a;
    }
    public void setSoftversion(String s){
        softversion = s;
    }
    public void setHardversion(String s){
        hardversion = s;
    }
}
