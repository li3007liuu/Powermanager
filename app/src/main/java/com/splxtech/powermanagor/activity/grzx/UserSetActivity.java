package com.splxtech.powermanagor.activity.grzx;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.splxtech.powermanagor.PowerManagerApp;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.activity.login.LoginActivity;
import com.splxtech.powermanagor.activity.login.LoginrActivity;
import com.splxtech.powermanagor.engine.AppConstants;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.PMUserInfo;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.powermanagor.utils.ImageUtils;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestManager;
import com.splxtech.splxapplib.net.RequestParameter;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by li300 on 2016/10/14 0014.
 */

public class UserSetActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {

    public static final String DATEPICKER_TAG = "datepicker";
    private PMUserInfo pmUserInfo;
    private TextView nameText;
    private TextView emailText;
    private TextView telText;
    private TextView idText;
    private Button birthdayButton;
    private TextView birthdayText;
    private Button backButton;
    private Button loginoutButton;
    private Button passsetButton;
    private ImageButton imageButton;
    private RadioButton boxRadio;
    private RadioButton grilRadio;
    private PowerManagerApp app;
    private Context context1 = this;


    private boolean needCallback;
    private ProgressDialog dlg;
    //请求列表管理器
    RequestManager requestManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //获取全局变量数据
        app = (PowerManagerApp)getApplication();
        pmUserInfo = app.getPmUserInfo();
        //新建网络请求队列管理器
        requestManager = new RequestManager(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userset);
        nameText = (TextView)findViewById(R.id.userset_text_name);
        emailText = (TextView)findViewById(R.id.userset_text_email);
        telText = (TextView)findViewById(R.id.userset_text_tel);
        idText = (TextView)findViewById(R.id.userset_text_ii);
        birthdayButton = (Button)findViewById(R.id.userset_button_birthday);
        birthdayText = (TextView)findViewById(R.id.userset_text_birthday);
        backButton = (Button)findViewById(R.id.userset_button_return);
        imageButton = (ImageButton)findViewById(R.id.userset_button_face);
        boxRadio = (RadioButton)findViewById(R.id.userset_radio_boy);
        grilRadio = (RadioButton)findViewById(R.id.userset_radio_gril);
        loginoutButton = (Button)findViewById(R.id.userset_button_loginout);
        loginoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLogin();
            }
        });
        passsetButton = (Button)findViewById(R.id.userset_button_cpass);
        passsetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        initViewData();

        boxRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pmUserInfo.setSex(false);
                updatesex(false);
            }
        });
        grilRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pmUserInfo.setSex(true);
                updatesex(true);
            }
        });

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);

        birthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.setVibrate(true);
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(true);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtils.showImagePickDialog(UserSetActivity.this);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }
    }



    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String b = year+"-"+(month+1)+"-"+day;
        birthdayText.setText(b);
        pmUserInfo.setBirthday(b);
        updatebirthday(b);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case ImageUtils.REQUEST_CODE_FROM_ALBUM: {

                if (resultCode == RESULT_CANCELED) {   //取消操作
                    return;
                }

                Uri imageUri = data.getData();
                ImageUtils.copyImageUri(this,imageUri);
                ImageUtils.cropImageUri(this, ImageUtils.getCurrentUri(), 200, 200);
                break;
            }
            case ImageUtils.REQUEST_CODE_FROM_CAMERA: {

                if (resultCode == RESULT_CANCELED) {     //取消操作
                    ImageUtils.deleteImageUri(this, ImageUtils.getCurrentUri());   //删除Uri
                }

                ImageUtils.cropImageUri(this, ImageUtils.getCurrentUri(), 200, 200);
                break;
            }
            case ImageUtils.REQUEST_CODE_CROP: {

                if (resultCode == RESULT_CANCELED) {     //取消操作
                    return;
                }

                Uri imageUri = ImageUtils.getCurrentUri();
                if (imageUri != null) {
                    imageButton.setImageURI(imageUri);
                    pmUserInfo.setFaceuri(imageUri);
                    updateface(imageUri.getPath());
                }
                break;
            }
            default:
                break;
        }
    }
    @Override
    public void onDestroy()
    {
        if (requestManager != null) {
            requestManager.cancelRequest();
        }
        super.onDestroy();
    }

    private void initViewData()
    {
        if(pmUserInfo.getFaceuri()==null)
        {
            imageButton.setImageResource(R.drawable.face);
        }
        else
        {
            imageButton.setImageURI(pmUserInfo.getFaceuri());
        }
        birthdayText.setText(pmUserInfo.getBirthday());
        nameText.setText(pmUserInfo.getName());
        emailText.setText(pmUserInfo.getEmail());
        telText.setText(pmUserInfo.getTel());
        idText.setText(pmUserInfo.getId()+"");
        if(pmUserInfo.getSex()==true)
        {
            boxRadio.setChecked(false);
            grilRadio.setChecked(true);
        }
        else
        {
            grilRadio.setChecked(false);
            boxRadio.setChecked(true);
        }
    }

    private void gotoLogin()
    {
        Intent startIntent = new Intent(UserSetActivity.this, LoginrActivity.class);
        this.startActivity(startIntent);
        this.finish();
    }

    private void updatesex(boolean sex)
    {
        RequestCallback updatesexCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0) {
                    Utils.saveUserInfo(UserSetActivity.this,pmUserInfo);
                    Utils.toastShow(UserSetActivity.this,"性别修改成功");
                }
                else
                {
                    Utils.toastShow(UserSetActivity.this,"性别修改失败");
                }
            }
        };
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("user_id",pmUserInfo.getId()+"");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("sex",sex);
        String jsonString = JSON.toJSONString(map);
        RequestParameter rp2 = new RequestParameter("data",jsonString);
        params.add(rp1);
        params.add(rp2);
        RemoteService.getInstance().invoke(UserSetActivity.this,requestManager, "Usera_Updateinfo", params,
                updatesexCallback);
    }

    private void updatebirthday(String birthday)
    {
        RequestCallback updatesexCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0) {
                    Utils.saveUserInfo(UserSetActivity.this,pmUserInfo);
                    Utils.toastShow(UserSetActivity.this,"出生日期修改成功");
                }
                else
                {
                    Utils.toastShow(UserSetActivity.this,"出生日期修改失败");
                }
            }
        };
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("user_id",pmUserInfo.getId()+"");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("birthday",birthday);
        String jsonString = JSON.toJSONString(map);
        RequestParameter rp2 = new RequestParameter("data",jsonString);
        params.add(rp1);
        params.add(rp2);
        RemoteService.getInstance().invoke(UserSetActivity.this,requestManager, "Usera_Updateinfo", params,
                updatesexCallback);
    }

    private void updateface(String face)
    {
        RequestCallback updatesexCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0) {
                    Utils.saveUserInfo(UserSetActivity.this,pmUserInfo);
                    Utils.toastShow(UserSetActivity.this,"头像修改成功");
                }
                else
                {
                    Utils.toastShow(UserSetActivity.this,"头像修改失败");
                }
            }
        };
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("user_id",pmUserInfo.getId()+"");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("face",face);
        String jsonString = JSON.toJSONString(map);
        RequestParameter rp2 = new RequestParameter("data",jsonString);
        params.add(rp1);
        params.add(rp2);
        RemoteService.getInstance().invoke(UserSetActivity.this,requestManager, "Usera_Updateinfo", params,
                updatesexCallback);
    }


    //网络数据返回全局类
    private abstract class AbstractRequestCallback implements RequestCallback {

        public abstract void onSuccess(String content);

        public void onFail(String errorMessage) {
            //dlg.dismiss();

            new AlertDialog.Builder(UserSetActivity.this).setTitle("出错啦")
                    .setMessage(errorMessage).setPositiveButton("确定", null)
                    .show();
        }

        public void onCookieExpired() {
            //dlg.dismiss();

            new AlertDialog.Builder(UserSetActivity.this)
                    .setTitle("出错啦")
                    .setMessage("Cookie过期，请重新登录")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent(
                                            UserSetActivity.this,
                                            LoginActivity.class);
                                    intent.putExtra(AppConstants.NeedCallback,
                                            true);
                                    startActivity(intent);
                                }
                            }).show();
        }
    }
}
