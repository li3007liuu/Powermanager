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
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.splxapplib.activity.BaseActivity;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestParameter;
import com.splxtech.splxapplib.utils.BaseUtils;

import java.util.ArrayList;

/**
 * Created by li300 on 2016/9/19 0019.
 */

public class RegisterActivity extends AppBaseActivity
{
    private CheckBox isyesCheck;
    private Button   nextButton;
    private Button   returnButton;
    private Button   fuxyButton;
    private EditText userEdit;
    private BaseActivity baseActivity = this;
    private boolean isEmailRegister;


    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState){
        setContentView(R.layout.activity_register);
        returnButton = (Button)findViewById(R.id.register_button_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLogin();
            }
        });
        fuxyButton = (Button)findViewById(R.id.register_button_fwtk);
        nextButton = (Button)findViewById(R.id.register_button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });
        isyesCheck = (CheckBox)findViewById(R.id.register_checkbox);
        isyesCheck.setChecked(true);
        isyesCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                nextButton.setEnabled(b);
            }
        });
        userEdit = (EditText)findViewById(R.id.register_edit_user);
    }

    @Override
    protected void loadData() {

    }

    private void sendData()
    {
        //注册回调函数
        RequestCallback registerCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content,PhalEntity.class);
                if(phalEntity.getCode()!=0)
                {
                    if(isEmailRegister==true)
                    {
                        gotoRegisterFinish();
                    }
                    else
                    {
                        //如果有短信验证码，则跳转至短信验证码处，如果没有则跳转至注册结束处
                        //gotoRegisterMessage();
                        gotoRegisterFinish();
                    }
                }
                else
                {
                    Toast toast=Toast.makeText(baseActivity,"该用户已经注册",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,50);
                    toast.show();
                }
            }
        };
        String user = userEdit.getText().toString();
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        if(BaseUtils.isEmail(user))
        {
            //启动邮箱注册
            isEmailRegister=true;
            RequestParameter rp1 = new RequestParameter("service", "User.getEmailInfo");
            RequestParameter rp2 = new RequestParameter("user_email", user);
            params.add(rp1);
            params.add(rp2);
            RemoteService.getInstance().invoke(this, "loginr", params,
                    registerCallback);


        }
        else if(BaseUtils.isMobileNo(user))
        {
            //启动短信验证注册
            isEmailRegister=false;
            RequestParameter rp1 = new RequestParameter("service", "User.getMobileInfo");
            RequestParameter rp2 = new RequestParameter("user_mobile", user);
            params.add(rp1);
            params.add(rp2);
            RemoteService.getInstance().invoke(this, "loginr", params,
                    registerCallback);

        }
        else
        {
            //提示错误
            Toast toast= Toast.makeText(this,"请输入正确的邮箱或手机号",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,50);
            toast.show();
        }
    }

    //跳转至登录
    private void gotoLogin()
    {
        Intent intent = new Intent(RegisterActivity.this,LoginrActivity.class);
        startActivity(intent);
        RegisterActivity.this.finish();
    }
    //跳转至注册结束
    private void gotoRegisterFinish()
    {
        String email = userEdit.getText().toString();
        Intent intent = new Intent(RegisterActivity.this,RegisterFinishActivity.class);
        intent.putExtra("email",email);
        String mobile = userEdit.getText().toString();
        intent.putExtra("mobile",mobile);
        startActivity(intent);
        RegisterActivity.this.finish();
    }
    //跳转至短信验证
    private void gotoRegisterMessage()
    {
        String mobile = userEdit.getText().toString();
        Intent intent = new Intent(RegisterActivity.this,RegisterMessageActivity.class);
        intent.putExtra("mobile",mobile);
        startActivity(intent);
        RegisterActivity.this.finish();
    }
}
