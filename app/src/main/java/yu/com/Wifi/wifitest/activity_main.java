package yu.com.Wifi.wifitest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import yu.com.Wifi.adapter.WifiListAdapter;
import yu.com.Wifi.dialog.InputWifiPWDDialog;
import yu.com.Wifi.dialog.WifiInfoDialog;
import yu.com.Wifi.util.WifiAdmin;
import yu.com.Wifi.R;

import java.util.List;

public class activity_main extends Activity implements CompoundButton.OnCheckedChangeListener ,WifiAdmin.OnWifiStateChangeListener,WifiAdmin.OnWifiRSSIListener, WifiAdmin.OnWifiScanResultsListener,WifiListAdapter.OnWifiItemClickListener,WifiAdmin.OnWifiSupplicantStateChangeListener{
    public static final String WIFI_LOG = "WIFI_LOGO";
    private CheckBox cbWifi;
    private ListView lvWifi;
    private TextView tvWifi;
    private WifiListAdapter wifiListAdapter;
    private WifiAdmin wifiAdmin;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_activity_main);
        wifiAdmin = new WifiAdmin(getApplicationContext());
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            Toast.makeText(this,"请打开位置信息！",Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CHANGE_NETWORK_STATE,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_NETWORK_STATE},
                    1);

        } else {
            initLinsen();
        }
        initView();
        initLinsen();
    }
public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
{
    if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
        initLinsen();

        //Toast.makeText(this,"权限申请成功！",Toast.LENGTH_SHORT).show();
    }
}




    private void initView(){
        tvWifi = (TextView) findViewById(R.id.tv_wifi_state);
        cbWifi = (CheckBox) findViewById(R.id.cb_wifi);
        lvWifi = (ListView) findViewById(R.id.lv_wifi);
        wifiListAdapter = new WifiListAdapter(getApplicationContext());
        lvWifi.setAdapter(wifiListAdapter);
    }


    private void initLinsen(){
        cbWifi.setOnCheckedChangeListener(this);
        lvWifi.setAdapter(wifiListAdapter);
        wifiListAdapter.setOnItemClickListener(this);
        wifiAdmin.setOnRSSIListener(this);
        wifiAdmin.setOnScanResultsListener(this);
        wifiAdmin.setOnWifiStateChangeListener(this);
        wifiAdmin.setOnWifiSupplicantStateChangeListener(this);

    }


    public void scanWifi(View view){
        wifiAdmin.startScan();
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        wifiAdmin.setWifiEnabled(isChecked);
    }


    @Override
    public void onWifiScanResults(List<ScanResult> scanResult, List<WifiConfiguration> configurations) {
        Log.i("wifi" , "wifi连接的状态" + wifiAdmin.getConnectInfo().getSupplicantState());
        wifiListAdapter.setData(scanResult ,wifiAdmin.getConnectInfo() ,configurations );
    }


    @Override
    public void onWifiRSSI(List<ScanResult> scanResult, List<WifiConfiguration> configurations) {
        Log.i("wifi" , "wifi连接onWifiRSSI的状态" + wifiAdmin.getConnectInfo().getSupplicantState());
        wifiListAdapter.setData(scanResult ,wifiAdmin.getConnectInfo() ,configurations );
    }


    @Override
    public void onWifiStateChange(int state) {
        Log.i("wifi" , "wifi的状态" + state);
        switch (state){
            case WifiAdmin.DISABLED: tvWifi.setText("wifi已关闭");  cbWifi.setChecked(false); cbWifi.setEnabled(true); break;
            case WifiAdmin.DISABLING:  tvWifi.setText("wifi正在关闭...");  cbWifi.setChecked(false); cbWifi.setEnabled(false); wifiListAdapter.clearData(); break;
            case WifiAdmin.ENABLE:  tvWifi.setText("wifi已打开");  cbWifi.setChecked(true); cbWifi.setEnabled(true); break;
            case WifiAdmin.ENABLING:  tvWifi.setText("wifi正在打开...");  cbWifi.setChecked(true); cbWifi.setEnabled(false);  break;
            default:  tvWifi.setText("wifi未找到"); break;
        }
    }

    @Override
    public void onWifiItemClick(List<ScanResult> resultList ,ScanResult result, WifiConfiguration wifiConfiguration ,int position, int capabilityType ,int state) {
        Log.i("wifi" , "wifi点击名称" + result.SSID + "---信号" + result.level + "---下标" + position + "---加密类型" + capabilityType + "---状态" + state );
        switch (state){

            case WifiListAdapter.CONNECTED:
                WifiInfoDialog.showDialog(this , wifiAdmin ,result ,wifiConfiguration,false);
                break;

            case WifiListAdapter.CONNECTING:
                WifiInfoDialog.showDialog(this , wifiAdmin ,result ,wifiConfiguration,false);
                break;

            case WifiListAdapter.SAVED:
                WifiInfoDialog.showDialog(this , wifiAdmin ,result ,wifiConfiguration,true);
                break;

            case WifiListAdapter.UNSAVED:

                if(capabilityType == 3)
                {
                    InputWifiPWDDialog.showDialog(this , wifiAdmin ,resultList ,result ,capabilityType);
                }

                else if(capabilityType == 1)
                {

                    Log.i("wifi" , "连接一个新的网络" + wifiAdmin.connectNewNetwork(resultList,result.SSID,""));
                }

                else if(capabilityType == 0)
                {
                    Log.i("wifi" , "连接一个新的网络" + wifiAdmin.connectNewNetwork(resultList,result.SSID,""));
                }
                break;
            default:break;
        }
    }


    @Override
    public void OnWifiSupplicantStateChange(List<ScanResult> scanResult, List<WifiConfiguration> configurations) {
        Log.i("wifi" , "请求时触发的状态" + wifiAdmin.getConnectInfo().getSupplicantState() + "---详细的状态" + wifiAdmin.getConnectInfo().getDetailedStateOf(wifiAdmin.getConnectInfo().getSupplicantState()));
        wifiListAdapter.setData(scanResult ,wifiAdmin.getConnectInfo() ,configurations );
    }

    @Override
    protected void onDestroy() {
        wifiAdmin.closeReceiver();
        super.onDestroy();
    }
}
