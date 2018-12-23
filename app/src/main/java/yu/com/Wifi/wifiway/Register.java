package yu.com.Wifi.wifiway;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import yu.com.Wifi.R;

public class Register extends AppCompatActivity implements View.OnClickListener{
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
//    private UserDataManager mUserDataManager;         //用户数据管理类
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAccount = (EditText) findViewById(R.id.edit_account);
        mPwd = (EditText) findViewById(R.id.edit_pwd);
        mPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mSureButton = (Button) findViewById(R.id.button_sure);
        mCancelButton = (Button) findViewById(R.id.button_cancel);

        mSureButton.setOnClickListener(this);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(this);

//        if (mUserDataManager == null) {
//            mUserDataManager = new UserDataManager(this);
//            mUserDataManager.openDataBase();                              //建立本地数据库
//        }
    }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_sure:                       //确认按钮的监听事件
//                    register_check();
                    dialog = new ProgressDialog(Register.this);
                    dialog.setTitle("正在注册");
                    dialog.setMessage("请稍后");
                    dialog.show();
                    new Thread(new RegThread()).start();
                    break;
                case R.id.button_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Register_to_Login = new Intent(Register.this, Login.class);    //切换Register Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                    break;
            }
        }

    public class RegThread implements Runnable{
        @Override
        public void run() {

            //获取服务器返回数据
            String RegRet = WebServicePost.executeHttpPost(mAccount.getText().toString(),mPwd.getText().toString(),"RegLet");

            //更新UI，界面处理
            showReq(RegRet);
        }
    }
    private void showReq(final String RegRet){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (RegRet.equals("true")) {
                    dialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    builder.setTitle("注册信息");
                    builder.setMessage("注册成功");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.show();
                } else {
                    dialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    builder.setTitle("注册信息");
                    builder.setMessage("注册失败");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                                Intent intent = new Intent(Register.this, Login.class);
//                                startActivity(intent);
//
                        }
                    });
                    builder.show();
                }
            }
        });
    }

//    public void register_check() {                                //确认按钮的监听事件
//            if (isUserNameAndPwdValid()) {
//                String userName = mAccount.getText().toString().trim();
//                String userPwd = mPwd.getText().toString().trim();
//                //检查用户是否存在
//                int count = mUserDataManager.findUserByName(userName);
//                //用户已经存在时返回，给出提示文字
//                if (count > 0) {
//                    Toast.makeText(Register.this, "手机号已存在！", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                UserData mUser = new UserData(userName, userPwd);
//                mUserDataManager.openDataBase();
//                long flag = mUserDataManager.insertUserData(mUser); //新建用户信息
//                if (flag == -1) {
//                    Toast.makeText(Register.this, "注册失败！", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(Register.this, "注册成功！", Toast.LENGTH_SHORT).show();
//                    Intent intent_Register_to_Login = new Intent(Register.this, Login.class);    //切换Register Activity至Login Activity
//                    startActivity(intent_Register_to_Login);
//                    finish();
//                }
//            }
//    }

    public boolean isUserNameAndPwdValid() {
        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(Register.this, "请输入手机号！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Toast.makeText(Register.this, "请输入密码！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}