package com.splxtech.powermanagor.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.PowerManagerApp;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.activity.main.MainActivity;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.PMUserInfo;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.activity.BaseActivity;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestParameter;
import com.splxtech.splxapplib.utils.BaseUtils;

import java.util.ArrayList;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;


/**
 * Created by li300 on 2016/9/18 0018.
 */

public class LoginrActivity extends AppBaseActivity
{
    private Button loginButton;
    private Button registerButton;
    private Button notloginButton;
    private Button fuxyButton;
    private EditText userEdit;
    private EditText passEdit;
    private CheckBox isyesCheck;
    private BaseActivity baseActivity = this;
    private PMUserInfo pmUserInfo;
    private PowerManagerApp app;
    @Override
    protected void initVariables() {
        app = (PowerManagerApp)getApplication();
        pmUserInfo = app.getPmUserInfo();
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_loginr);
        loginButton = (Button)findViewById(R.id.loginr_button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        registerButton = (Button)findViewById(R.id.loginr_button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRegister();
            }
        });
        notloginButton = (Button)findViewById(R.id.loginr_button_notlogin);
        fuxyButton = (Button)findViewById(R.id.loginr_button_fwtk);
        userEdit = (EditText)findViewById(R.id.loginr_edit_user);
        userEdit.setText(getLoginName());
        userEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b)
            {
                if(b==false)
                {
                    //findUser();
                }
            }
        });
        passEdit = (EditText)findViewById(R.id.loginr_edit_pass);
        isyesCheck = (CheckBox)findViewById(R.id.loginr_checkbox);
        isyesCheck.setChecked(true);
        isyesCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                loginButton.setEnabled(b);
            }
        });
    }
    @Override
    protected void loadData() {
        //pmUserInfo = new PMUserInfo();
        //pmUserInfo.setId(0);
    }
//登录函数
    /*
    private void findUser()
    {
        //登录回调函数
        RequestCallback findUserCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content,PhalEntity.class);
                if(phalEntity.getCode()==0)
                {
                    //gotoMain();
                    pmUserInfo = JSON.parseObject(phalEntity.getInfo(),PMUserInfo.class);
                    Toast toast=Toast.makeText(baseActivity,pmUserInfo.getId()+"",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,50);
                    toast.show();
                }
                else
                {
                    Toast toast=Toast.makeText(baseActivity,"该用户未注册",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,50);
                    toast.show();
                }
            }
        };
        String user = userEdit.getText().toString();
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        if(user==null)
        {
            Toast toast=Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,50);
            toast.show();
        }
        else
        {

            if(BaseUtils.isEmail(user))
            {
                //email + pass 请求
                RequestParameter rp1 = new RequestParameter("service", "User.getEmailInfo");
                RequestParameter rp2 = new RequestParameter("user_email", user);
                params.add(rp1);
                params.add(rp2);
                RemoteService.getInstance().invoke(this, "loginr", params,
                        findUserCallback);
            }
            else if(BaseUtils.isMobileNo(user))
            {
                RequestParameter rp1 = new RequestParameter("service", "User.getMobileInfo");
                RequestParameter rp2 = new RequestParameter("user_mobile", user);
                params.add(rp1);
                params.add(rp2);
                RemoteService.getInstance().invoke(this, "loginr", params,
                        findUserCallback);
            }
            else
            {
                Toast toast=Toast.makeText(this,"请输入正确的邮箱或手机号",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,50);
                toast.show();
            }
        }
    }*/
    private void login()
    {

        //登录回调函数
        RequestCallback loginCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content,PhalEntity.class);
                if(phalEntity.getCode()==0)
                {
                    pmUserInfo.setPass(passEdit.getText().toString());

                    Utils.saveUserInfo(baseActivity,pmUserInfo);

                    gotoMain();
                }
                else if(phalEntity.getCode()==1)
                {
                    Toast toast=Toast.makeText(baseActivity,"该用户未注册",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,50);
                    toast.show();
                }
                else if(phalEntity.getCode()==2)
                {
                    Toast toast=Toast.makeText(baseActivity,"密码错误",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,50);
                    toast.show();
                }
            }
        };
        String pass = passEdit.getText().toString();
        String user = userEdit.getText().toString();
        //ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        if(pass.length()<6)
        {
            Utils.toastShow(LoginrActivity.this,"密码必须大于6位");
        }
        else{
            if(BaseUtils.isEmail(user))
            {
                pmUserInfo.setEmail(user);
                ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
                RequestParameter rp1 = new RequestParameter("user_email", user);
                RequestParameter rp2 = new RequestParameter("user_pass", pass);
                params.add(rp1);
                params.add(rp2);
                RemoteService.getInstance().invoke(this, "Usera_Loginin", params,
                        loginCallback);
            }
            else if(BaseUtils.isMobileNo(user))
            {
                pmUserInfo.setTel(user);
                ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
                RequestParameter rp1 = new RequestParameter("user_tel", user);
                RequestParameter rp2 = new RequestParameter("user_pass", pass);
                params.add(rp1);
                params.add(rp2);
                RemoteService.getInstance().invoke(this, "Usera_Loginin", params,
                        loginCallback);
            }
            else
            {
                Utils.toastShow(LoginrActivity.this,"账号格式错误，必须为邮箱或者手机号");
            }
        }

        /*
        if(pmUserInfo.getId()==0||pass.length()<6)
        {
            Toast toast=Toast.makeText(this,"请输入正确的用户名和密码",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,50);
            toast.show();
        }
        else
        {
            RequestParameter rp1 = new RequestParameter("service", "User.getLoginById");
            RequestParameter rp2 = new RequestParameter("user_id", pmUserInfo.getId()+"");
            RequestParameter rp3 = new RequestParameter("user_pass", pass);
            params.add(rp1);
            params.add(rp2);
            params.add(rp3);
            RemoteService.getInstance().invoke(this, "loginr", params,
                    loginCallback);
        }*/
    }

    //跳转至注册
    private void gotoRegister()
    {
        //Intent intent = new Intent(LoginrActivity.this, RegisterActivity.class);
        //startActivity(intent);
        //LoginrActivity.this.finish();

        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
// 解析注册结果
                if (result == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");

// 提交用户信息（此方法可以不调用）
                    //registerUser(country, phone);
                }
            }
        });
        registerPage.show(this);
    }
    //跳转至mian
    private void gotoMain()
    {

        app.setPmUserInfo(pmUserInfo);
        Utils.saveUserInfo(LoginrActivity.this,pmUserInfo);
        Intent intent = new Intent(LoginrActivity.this, MainActivity.class);
        //Bundle bundle = new Bundle();
        //bundle.putSerializable("PMUserInfo",pmUserInfo);
        //intent.putExtra("bundle",bundle);
        startActivity(intent);
        LoginrActivity.this.finish();
    }
    //获取pmUser的登录名
    private String getLoginName()
    {
        if(pmUserInfo==null)
        {
            return "";
        }
        else if(pmUserInfo.getEmail()!="")
        {
            return pmUserInfo.getEmail();
        }
        else if(pmUserInfo.getTel()!="")
        {
            return pmUserInfo.getTel();
        }
        else
        {
            return "";
        }
    }
}
