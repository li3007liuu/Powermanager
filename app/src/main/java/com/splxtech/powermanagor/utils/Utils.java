package com.splxtech.powermanagor.utils;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.view.Gravity;
import android.widget.Toast;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.entity.PMUserInfo;
import com.splxtech.splxapplib.activity.BaseActivity;
import com.splxtech.splxapplib.utils.BaseUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class Utils extends BaseUtils {
	/**
	 * 
	 * @Title: convertToInt
	 * @Description: 对象转化为整数数字类型
	 * @param value
	 * @param defaultValue
	 * @return integer
	 * @throws
	 */
	public final static int convertToInt(Object value, int defaultValue) {
		if (value == null || "".equals(value.toString().trim())) {
			return defaultValue;
		}
		try {
			return Integer.valueOf(value.toString());
		} catch (Exception e) {
			try {
				return Double.valueOf(value.toString()).intValue();
			} catch (Exception e1) {
				return defaultValue;
			}
		}
	}
	
	/**
	 * 
	 * @Title: createProgressDialog
	 * @Description: 创建ProgressDialog
	 * @param activity
	 * @param msg
	 * @return ProgressDialog
	 */
    public static ProgressDialog createProgressDialog(final BaseActivity activity, final String msg)
    {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage(msg);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

	public static String utf8encode(String input)
	{
		String utf8name="";
		try{
			utf8name= URLEncoder.encode(input,"UTF-8");
		}catch (Exception e){
			e.printStackTrace();
		}
		return utf8name;
	}
	public static String utf8decode(String input)
	{
		String nameutf8="";
		try{
			nameutf8= URLDecoder.decode(input,"UTF-8");
		}catch (Exception e){
			e.printStackTrace();
		}
		return nameutf8;
	}



	/*
	* 将登录参数保存至文件中
	* */
	public static void saveUserInfo(final Context activity,final PMUserInfo pmUserInfo)
	{
		SharedPreferences sharedPreferences = activity.getSharedPreferences("pmUserInfo1", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.putInt("ID",pmUserInfo.getId());
		edit.putString("NAME",pmUserInfo.getName());
		edit.putString("EMAIL",pmUserInfo.getEmail());
		edit.putString("TEL",pmUserInfo.getTel());
		edit.putString("PASS",pmUserInfo.getPass());
		edit.putBoolean("SEX",pmUserInfo.getSex());
		edit.putString("BIRTHDAY",pmUserInfo.getBirthday());
		if(pmUserInfo.getFaceuri()!=null) {
			edit.putString("URIPATH", pmUserInfo.getFaceuri().getPath());
		}
		else
		{
			edit.putString("URIPATH", "");
		}
		edit.putString("MQTTCLIENTID",pmUserInfo.getMqttClientId());
		edit.commit();
	}
	/*
	* 将用户参数从文件中读取到类中
	 */
	public static PMUserInfo readUserInfo(final Context activity)
	{
		PMUserInfo pmUserInfo = new PMUserInfo();
		SharedPreferences preferences = activity.getSharedPreferences("pmUserInfo1", Context.MODE_PRIVATE);
		pmUserInfo.setId(preferences.getInt("ID",0));
		pmUserInfo.setName(preferences.getString("NAME",""));
		pmUserInfo.setEmail(preferences.getString("EMAIL",""));
		pmUserInfo.setTel(preferences.getString("TEL",""));
		pmUserInfo.setPass(preferences.getString("PASS",""));
		pmUserInfo.setSex(preferences.getBoolean("SEX",false));
		pmUserInfo.setBirthday(preferences.getString("BIRTHDAY",""));
		pmUserInfo.setMqttClientId(preferences.getString("MQTTCLIENTID",""));
		String path = preferences.getString("URIPATH","");
		Uri uri = UriUtils.getUriFromFilePath(path);
		pmUserInfo.setFaceuri(uri);
		return pmUserInfo;
	}

	//检测网络是否可用
	public static boolean isNetworkAvailable(final Context baseActivity) {
		ConnectivityManager connectivity = (ConnectivityManager) baseActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected())
			{
				// 当前网络是连接的
				if (info.getState() == NetworkInfo.State.CONNECTED)
				{
					// 当前所连接的网络可用
					return true;
				}
			}
		}
		return false;
	}

	//获取wifi的mac地址作为系统内部唯一标识码
	public static String getAndroidMac(Context context)
	{
		WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		return wm.getConnectionInfo().getMacAddress();
	}


	public static void toastShow(Context baseActivity,String msg)
	{
		Toast toast=Toast.makeText(baseActivity,msg,Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP,0,50);
		toast.show();
	}

	public static int getTypeImage(int type)
	{
		switch (type)
		{
			case 1:
				return R.drawable.db;
			case 2:
				return R.drawable.znkg;
			default:
				return R.drawable.dq;
		}
	}


	public static int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}
	public static int px2dip(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue / scale + 0.5f);
	}

	public static int getDay(int a)
	{
		Calendar c = new GregorianCalendar();
		c.add(c.DATE,a);
		return c.get(Calendar.DAY_OF_MONTH);
	}
	public static int getDayOfYear()
	{
		Calendar c = new GregorianCalendar();
		return c.get(Calendar.DAY_OF_YEAR);
	}
	public static int getMouth(int a)
	{
		Calendar c = new GregorianCalendar();
		c.add(c.DATE,a);
		return c.get(Calendar.MONTH)+1;
	}
	public static int getMouth2(int a)
	{
		Calendar c = Calendar.getInstance();
		int d = c.get(Calendar.MONTH);
		d = d+a;
		if(d<0)
		{
			d = d%12;
			d = d+12;
		}
		else
		{
			d= d%12;
		}
		return d+1;
	}
	public static int getYear(int a)
	{
		Calendar c = Calendar.getInstance();
		int y= c.get(Calendar.YEAR);
		int m=c.get(Calendar.MONTH);
		m = m+a-1;
		return y+((m+a)/12);
	}
	public static int getHours()
	{
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.HOUR_OF_DAY);
	}
	// 1 == 周日  2==周一 3 == 周二 4==周三
	public static int getDayOfWeek(int a)
	{
		Calendar c = new GregorianCalendar();
		c.add(c.DATE,a);
		return c.get(Calendar.DAY_OF_WEEK);
	}
	public static long getTime(){
		Calendar c = Calendar.getInstance();
		return c.getTimeInMillis();
	}
	//根据天数获取相差的月份
	public static int getBetweenMonth(int day){
		int b = getMouth(0-day);
		int now = getMouth(0);
		int dec;
		if(day>365)
		{
			int ynum = day/365;
			if(now<b)
			{
				dec = 12+now-b+day*12;
			}
			else
			{
				dec = now-b+day*12;
			}
		}
		else
		{
			if(now<b)
			{
				dec = 12+now -b;
			}
			else
			{
				dec = now -b;
			}
		}
		return dec;
	}
	//判断服务是否正在运行
	public static boolean isServiceRunning(Context mContext,String className) {

		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager)
				mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList
				= activityManager.getRunningServices(30);

		if (!(serviceList.size()>0)) {
			return false;
		}

		for (int i=0; i<serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
	public static float int_pl(int temp)
	{
		//short plshort = (short)temp;
		//低位
		//byte plbyte1 = (byte)(plshort&0xff);
		//高位
		//byte plbyte2 = (byte)((plshort>>>8)&0xff);
		float plfloat = 65535+temp;//plbyte2*256+plbyte1;
		return plfloat/1000;
	}

	public static String stringlowtolarge(String s)
	{
		StringBuffer sb = new StringBuffer();
		if(s!=null){
			for(int i=0;i<s.length();i++){
				char c = s.charAt(i);
				if(Character.isLowerCase(c))
				{
					sb.append(Character.toUpperCase(c));
				}
				else
				{
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	public static boolean string_boolean(String s)
	{
		if(s.length()==0)
		{
			return false;
		}
		if(s.equals("true"))
		{
			return true;
		}
		if(s.equals("TRUE"))
		{
			return true;
		}
		if(s.equals("1"))
		{
			return true;
		}
		if(s.equals("0"))
		{
			return false;
		}
		if(s.equals("false"))
		{
			return false;
		}
		if(s.equals("FALSE"))
		{
			return false;
		}
		else
		{
			return false;
		}
	}
}
