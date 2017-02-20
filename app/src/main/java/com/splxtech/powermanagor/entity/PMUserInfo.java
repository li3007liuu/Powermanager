package com.splxtech.powermanagor.entity;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by li300 on 2016/9/22 0022.
 */

public class PMUserInfo implements Serializable
{
    private int id;
    private String name;
    private String email;
    private String tel;
    private String pass;
    private Uri faceuri;
    private boolean sex;
    private String birthday;
    private String mqttClientId;


    public PMUserInfo() {

    }

    public int getId()
    {
        return id;
    }
    public void setId(int a)
    {
        id=a;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String a)
    {
        name=a;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String a)
    {
        email = a;
    }
    public String getTel()
    {
        return tel;
    }
    public void setTel(String a)
    {
        tel = a;
    }
    public String getPass()
    {
        return pass;
    }
    public void setPass(String a)
    {
        pass = a;
    }
    public Uri getFaceuri()
    {
        return faceuri;
    }
    public void setFaceuri(Uri u)
    {
        faceuri = u;
    }
    public boolean getSex()
    {
        return sex;
    }
    public void setSex(boolean a)
    {
        sex = a;
    }
    public String getBirthday()
    {
        return birthday;
    }
    public void setBirthday(String a)
    {
        birthday = a;
    }
    public String getMqttClientId()
    {
        return mqttClientId;
    }
    public void setMqttClientId(String a)
    {
        mqttClientId = a;
    }
}
