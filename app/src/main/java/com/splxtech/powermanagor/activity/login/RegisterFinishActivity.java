package com.splxtech.powermanagor.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.activity.main.MainActivity;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.PMUserInfo;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.activity.BaseActivity;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestParameter;

import java.util.ArrayList;

/**
 * Created by li300 on 2016/9/19 0019.
 */

public class RegisterFinishActivity extends AppBaseActivity
{
    private Button returnButton;
    private Button finishButton;
    private EditText pass1Edit;
    private EditText pass2Edit;
    private String email;
    private String mobile;
    private PMUserInfo pmUserInfo;
    private BaseActivity baseActivity;

    @Override
    protected void initVariables()
    {
        Intent intent = this.getIntent();
        email = intent.getStringExtra("email");
        mobile = intent.getStringExtra("mobile");
        pmUserInfo = new PMUserInfo();
    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_register2);
        returnButton = (Button)findViewById(R.id.register2_button_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLogin();
            }
        });
        finishButton = (Button)findViewById(R.id.register2_button_finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });
        pass1Edit = (EditText)findViewById(R.id.register2_edit_pass1);
        pass2Edit = (EditText)findViewById(R.id.register2_edit_pass2);
    }

    @Override
    protected void loadData() {

    }
    //跳转至登录
    private void gotoLogin()
    {
        Intent intent = new Intent(RegisterFinishActivity.this,LoginrActivity.class);
        startActivity(intent);
        RegisterFinishActivity.this.finish();
    }
    private void sendData()
    {

        RequestCallback registerCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content,PhalEntity.class);
                if(phalEntity.getCode()==0)
                {
                    pmUserInfo.setPass(pass1Edit.getText().toString());
                    Utils.saveUserInfo(baseActivity,pmUserInfo);
                    gotoMain();
                }
                else if(phalEntity.getCode()==1)
                {
                    Toast toast=Toast.makeText(baseActivity,"用户名已存在请更换邮箱或手机号",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,50);
                    toast.show();
                }
            }
        };

        String pass1 = pass1Edit.getText().toString();
        String pass2 = pass2Edit.getText().toString();
        if(pass1.length()<6)
        {
            //提示错误
            Toast toast= Toast.makeText(this,"密码长度不能小于6",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,50);
            toast.show();
        }
        else if(!pass1.equals(pass2))
        {
            //提示错误
            Toast toast= Toast.makeText(this,"两次密码应该相同",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,50);
            toast.show();
        }
        else
        {
            //提示
            Toast toast= Toast.makeText(this,"邮箱"+email+"\n手机号"+mobile+"\n密码"+pass1,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,50);
            toast.show();
            ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
            if(email!=null)
            {
                RequestParameter rp1 = new RequestParameter("service", "User.getRegisterByEmail");
                RequestParameter rp2 = new RequestParameter("user_email", email);
                RequestParameter rp3 = new RequestParameter("user_pass", pass1);
                params.add(rp1);
                params.add(rp2);
                params.add(rp3);
                RemoteService.getInstance().invoke(this, "loginr", params,
                        registerCallback);

            }
            else if(mobile!=null)
            {
                RequestParameter rp1 = new RequestParameter("service", "User.getRegisterByMobile");
                RequestParameter rp2 = new RequestParameter("user_mobile", mobile);
                RequestParameter rp3 = new RequestParameter("user_pass", pass1);
                params.add(rp1);
                params.add(rp2);
                params.add(rp3);
                RemoteService.getInstance().invoke(this, "loginr", params,
                        registerCallback);

            }
        }
    }

    //跳转至mian
    private void gotoMain()
    {
        Intent intent = new Intent(RegisterFinishActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("PMUserInfo",pmUserInfo);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
        RegisterFinishActivity.this.finish();
    }
}
