package com.splxtech.powermanagor.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by li300 on 2016/10/26 0026.
 */

public class ElectricityMeter implements Serializable {
    private String name;
    private int tableid;
    private String productId;
    private int productType;
    private int productImageId;
    private boolean checked;
    private int imageId;
    private int allwaste;//月总功耗
    private ArrayList<Appliance> applianceArrayList;
    private Appliance allapp;
    public static final long serialVersionUID = 9528L;
    public ElectricityMeter(String n,String p,int i)
    {
        name = n;
        productId = p;
        imageId = i;
        applianceArrayList = new ArrayList<>();
    }
    public ElectricityMeter(String n,int t,int i)
    {
        name = n;
        productImageId = i;
        productType = t;
        applianceArrayList = new ArrayList<>();
    }
    public ElectricityMeter(int id)
    {
        name = "";
        productImageId = 0;
        productType = 0;
        tableid = id;
        applianceArrayList = new ArrayList<>();

    }
    public void setName(String n){
        name = n;
    }
    public void setProductId(String i){
        productId = i;
    }
    public void setImageId(int i){
        imageId = i;
    }
    public void setAllwaste(int a){
        allwaste = a;
    }
    public void setApplianceArrayList(ArrayList<Appliance> app){
        applianceArrayList = app;
    }
    public void setProductType(int a){
        productType = a;
    }
    public void setProductImageId(int a){
        productImageId = a;
    }
    public void setChecked(boolean a){
        checked = a;
    }
    public String getName(){
        return name;
    }
    public String getProductId(){
        return productId;
    }
    public int getImageId(){
        return imageId;
    }
    public int getAllwaste(){
        return allwaste;
    }
    public ArrayList<Appliance> getApplianceArrayList()
    {
        return applianceArrayList;
    }
    public int getProductType(){
        return productType;
    }
    public int getProductImageId(){
        return productImageId;
    }
    public boolean getChecked(){
        return checked;
    }
    public int getTableid()
    {
        return tableid;
    }
    public void setTableid(int i)
    {
        tableid = i;
    }
    public void setAllapp(Appliance a)
    {
        allapp = a;
    }
    public Appliance getAllapp()
    {
        return  allapp;
    }

}
