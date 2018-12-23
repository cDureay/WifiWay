package yu.com.Wifi.wifiway;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import yu.com.Wifi.MainActivity;
import yu.com.Wifi.MainInterface;
import yu.com.Wifi.R;
import yu.com.Wifi.TopActivityViewer;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private Button mRegisterButton;                   //注册按钮
    private Button mLoginButton;                      //登录按钮
    private Button mChangepwdButton;                    //修改密码按钮
    private CheckBox mRememberCheck;                  //是否记住密码
//  private UserDataManager mUserDataManager;         //用户数据管理类
    //提示框
    private ProgressDialog dialog;
    //服务器返回的数据
    private String infoString;
    public static String user;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //通过id找到相应的控件
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        mAccount = (EditText) findViewById(R.id.edit_account);
        mPwd = (EditText) findViewById(R.id.edit_pwd);
        mLoginButton = (Button) findViewById(R.id.button_login);
        mRegisterButton = (Button) findViewById(R.id.button_register);
        mRememberCheck = (CheckBox) findViewById(R.id.checkbox_remember);
        mChangepwdButton = (Button) findViewById(R.id.button_change_pwd);
        boolean isRemember = pref.getBoolean("remember_password",false);
        if (isRemember) {
            String name = pref.getString("USER_NAME", "");
            String pwd = pref.getString("PASSWORD", "");
            mAccount.setText(name);
            mPwd.setText(pwd);
            mRememberCheck.setChecked(true);
        }

        mLoginButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        mChangepwdButton.setOnClickListener(this);

//        if (mUserDataManager == null) {
//            mUserDataManager = new UserDataManager(this);
//            mUserDataManager.openDataBase();        //建立本地数据库
//        }
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
        test();
    }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_login:                              //登录界面的登录按钮
//                    login();

                    String userName = mAccount.getText().toString().trim();    //获取当前输入的用户名和密码信息
                    String userPwd = mPwd.getText().toString().trim();
                    user = userName;
                    Log.e("Login", "user="+user );
                    editor = pref.edit();
                    if(mRememberCheck.isChecked()){
                        editor.putBoolean("remember_password",true);
                        editor.putString("USER_NAME",userName);
                        editor.putString("PASSWORD",userPwd);
                    }else{
                        editor.clear();
                    }
                    editor.apply();
                    dialog = new ProgressDialog(Login.this);
                    dialog.setTitle("正在登录");
                    dialog.setMessage("请稍后");
                    dialog.setCancelable(false);//设置可以通过back键取消
                    dialog.show();
                    //设置子线程，分别进行Get和Post传输数据
                    new Thread(new MyThread()).start();
                    break;
                case R.id.button_register:                            //登录界面的注册按钮
                    Intent intent_Login_to_Register = new Intent(Login.this, Register.class);    //切换Login Activity至User Activity
                    startActivity(intent_Login_to_Register);
                    finish();
                    break;
                case R.id.button_change_pwd:                         //登录界面的修改密码按钮
                    Intent intent_Login_to_reset = new Intent(Login.this, Resetpwd.class);    //切换Login Activity至User Activity
                    startActivity(intent_Login_to_reset);
                    finish();
                    break;
            }
        }

    public class MyThread implements Runnable{
        @Override
        public void run() {
            infoString = WebServiceGet.executeHttpGet(mAccount.getText().toString(),mPwd.getText().toString(),"LogLet");//获取服务器返回的数据

            //更新UI，使用runOnUiThread()方法
            showResponse(infoString);
        }
    }

    private void showResponse(final String response){
        runOnUiThread(new Runnable(){
            //更新UI
            @Override
            public void run() {
                if(response.equals("false")){
                    Toast.makeText(Login.this,"登录失败！", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(Login.this,MainInterface.class) ;    //切换Login Activity至User Activity
                    startActivity(intent);
                    finish();
                }
                dialog.dismiss();
            }
        });
    }

//    public void login() {    //登录按钮监听事件
//        if (isUserNameAndPwdValid()) {
//            String userName = mAccount.getText().toString().trim();    //获取当前输入的用户名和密码信息
//            String userPwd = mPwd.getText().toString().trim();
//            mPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            SharedPreferences.Editor editor = login_sp.edit();
//            int result = mUserDataManager.findUserByNameAndPwd(userName, userPwd);
//            if (result == 1) {                                             //返回1说明用户名和密码均正确
//                //保存用户名和密码
//                editor.putString("USER_NAME", userName);
//                editor.putString("PASSWORD", userPwd);
//
//                //是否记住密码
//                if (mRememberCheck.isChecked()) {
//                    editor.putBoolean("mRememberCheck", true);
//                } else {
//                    editor.putBoolean("mRememberCheck", false);
//                }
//                editor.apply();
//
//                Intent intent = new Intent(Login.this, MainInterface.class);  //切换Login Activity至User Activity
//                startActivity(intent);
//                Toast.makeText(Login.this, "登录成功！", Toast.LENGTH_SHORT).show();//登录成功提示
//                //finish();
//            } else if (result == 0) {
//                Toast.makeText(Login.this, "登录失败！请输入正确的用户名和密码！", Toast.LENGTH_SHORT).show();//登录失败提示
//            }
//        }
//    }

    public boolean isUserNameAndPwdValid() {
        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请输入手机号！", Toast.LENGTH_LONG).show();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请输入密码！", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void test() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!TopActivityViewer.hasPermission(this)) {
                Toast.makeText(this, "Request Permission", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);
            } else {
                TopActivityViewer.getTopActivityTask(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (!TopActivityViewer.hasPermission(this)) {
                Toast.makeText(this, "Denied Permission", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                TopActivityViewer.getTopActivityTask(this);
            }
        }
    }

//    @Override
//    protected void onResume() {
//        if (mUserDataManager == null) {
//            mUserDataManager = new UserDataManager(this);
//            mUserDataManager.openDataBase();
//        }
//        super.onResume();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onPause() {
//        if (mUserDataManager != null) {
//            mUserDataManager.closeDataBase();
//            mUserDataManager = null;
//        }
//        super.onPause();
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "发生未知错误！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }

    }
}
