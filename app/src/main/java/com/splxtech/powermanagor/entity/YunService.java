package com.splxtech.powermanagor.entity;

/**
 * Created by li300 on 2016/11/14 0014.
 */
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseObject;

/*
 * service:usergetinfo
 * parate:data
 * 通过 email或 tel 获取 用户信息
 *
 */
/*
 * service:userupdate
 * parate:index data
 * 通过 index user id 去更新 data中的数据项
 *
 */
/*
 * service:addhardlist
 * parate:data
 * 通过 netid userid type name  去操作hard表进行设备添加
 *
 */
/*
 * service:gethardlist
 * parate:index
 * 通过 userid 获取用户绑定的设备列表
 *
 */
/*
 * service:updatehardlist
 * parate:data
 * 通过 data 中包含的id 确认添加的数据项，进行hard表的其他信息更新
 *
 */
/*
 * service:deletehardlist
 * parate:index data
 * index 为当前用户的id data中包含多组hardid，将绑定的数据从bind表中删除
 *
 */
/*
 * service:addapplist
 * parate:data
 * data 中包含 hardwareid 及appid两组必要参数 返回添加好的电器id外键
 *
 */
/*
 * service:getapplist
 * parate:index
 * 通过hardwareid在 app表中查找全部的电器进行返回
 *
 */

/*
 * service:deleteapplist
 * parate:index data
 * index为需要删除的电器id外键 data数据格式为删除后appid0~n的数据表外键
 *
 */
/*
 * service:updateapplist
 * parate:data
 * data中包含电器id外键，查找对应id外键进行其他数据更新
 *
 */
/*
 * service:deleteallapplist
 * parate:index
 * index为hard设备外键 删除该硬件设备下的全部电器
 *
 */
/*
 * service:gettodaywaste
 * parate:index
 * index为hard设备外键 获取该设备下的所有电器的当日功耗 第一个为hard设备总功耗
 *
 */
/*
 * service:getdaywaste
 * parate:index
 * index为电器设备外键 获取该电器近20日 功耗情况
 *
 */
/*
 * service:getweekwaste
 * parate:index
 * index为电器设备外键 获取该电器近16周 功耗情况
 *
 */
/*
 * service:getmonthwaste
 * parate:index
 * index为电器设备外键 获取该电器近12月 功耗情况
 *
 */
public class YunService
{
    public static final String fstopic = "PowerManager/SER/";
    public static final String frtopic = "PowerManager/SERA/";
    /*public static final String addservice = "MYSQLADD";
    public static final String deleteservice = "MYSQLDELETE";
    public static final String updateservice = "MYSQLUPDATE";
    public static final String seleteservice = "MYSQLSELECT";
    public static final String adddeviceservice = "ADDDEVICE";
    public static final String deletedeviceservice ="SELETEDEVICE";*/
    public static final String serusergetinfo = "usergetinfo";
    public static final String seruserupdate="userupdate";
    public static final String seraddhardlist="addhardlist";
    public static final String sergethardlist="gethardlist";
    public static final String serupdatehardlist="updatehardlist";
    public static final String serdeletehardlist="deletehardlist";
    public static final String seraddapplist = "addapplist";
    public static final String sergetapplist="getapplist";
    public static final String serdeleteapplist="deleteapplist";
    public static final String serupdateapplist="updateapplist";
    public static final String serdeleteallapplist="deleteallapplist";
    public static final String sergettodaywaste="gettodaywaste";
    public static final String sergetdaywaste="getdaywaste";
    public static final String sergetweekwaste="getweekwaste";
    public static final String sergetmonthwaste="getmonthwaste";
    public static final String tusername = "splxapp_user";
    public static final String tuser_id = "id";
    public static final String tuser_name = "name";
    public static final String tuser_pass = "pass";
    public static final String tuser_tel = "tel";
    public static final String tuser_email = "email";
    public static final String tuser_face = "face";
    public static final String tuser_sign = "sign";
    public static final String tuser_online = "online";
    public static final String tuser_appver = "appversion";
    public static final String tuser_sex = "sex";
    public static final String tuser_birthday = "birthday";
    public static final String tuser_messflag = "messageflag";
    public static final String tuser_datasendtime = "datasendtime";
    public static final String tuser_userid = "userid";
    public static final String thardname = "splxapp_hardware";
    public static final String thard_id = "id";
    public static final String thard_name = "name";
    public static final String thard_userid = "userId";
    public static final String thard_netid = "netid";
    public static final String thard_type = "type";
    public static final String thard_ver="version";
    public static final String thard_online = "online";
    public static final String tappliancename = "splxapp_appliance";
    public static final String tappliance_id = "id";
    public static final String tappliance_name = "name";
    public static final String tappliance_appid = "appid";
    public static final String tappliance_hardid = "hardwareid";
    public static final String tappliance_img1 = "imageid1";
    public static final String tappliance_img2 = "imageid2";
    public static final String tappliance_modenum = "modenum";
    public static final String twastename="splxapp_appdaystatic";
    public static final String twaste_id = "id";
    public static final String twaste_name = "name";
    public static final String twaste_appid = "appid";
    public static final String twaste_waste = "waste";
    public static final String twaste_date = "date";
    //顺序是1~12
    public static final String twaste_tbwh = "wasteh";
    public static final String tbindname = "splxapp_userhardware";
    public static final String tbind_id = "id";
    public static final String tbind_userid = "userid";
    public static final String tbind_hardid = "hardwareid";


    public static String ToJsonString(String ser,int index,Object data)
    {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("service",ser);
        map.put("data",data);
        map.put("index",index);
        String jsonString = JSON.toJSONString(map);
        return jsonString;
    }
    public static YunReceData StringToObject(String s)
    {
        YunReceData yunReceData = new YunReceData();
        JSONObject ob1 = JSON.parseObject(s);
        int msg = (int)ob1.get("msg");
        Object jsonArray = ob1.get("data");
        List<YunReData> list = new ArrayList<>();
        if(jsonArray.toString().indexOf("[")>=0)
        {
            list = JSON.parseArray(jsonArray + "", YunReData.class);
        }
        else {
            list = null;
        }

        yunReceData.setMsg(msg);
        yunReceData.setData(list);
        return yunReceData;
    }
    public static YunReData StringToYunReData(String s)
    {
        if(s.length()>0)
        {
            return (YunReData)JSON.parseObject(s,YunReData.class);
        }
        else
        {
            return null;
        }
    }
    public static List<YunReData> StringToListYunReData(String s)
    {
        if(s.length()>0&&s.indexOf("[")>=0)
        {
            List<YunReData> list = new ArrayList<>();
            list=JSON.parseArray(s,YunReData.class);
            return list;
        }
        else
        {
            return null;
        }
    }
}
