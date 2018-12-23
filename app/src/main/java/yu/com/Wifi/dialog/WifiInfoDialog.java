package yu.com.Wifi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import yu.com.Wifi.util.WifiAdmin;
import yu.com.Wifi.R;


public class WifiInfoDialog extends Dialog implements View.OnClickListener {
    public static WifiInfoDialog WifiInfoDialog;

    private Button btnBack;
    private Button btnCancelSave;
    private WifiAdmin mWifiAdmin;
    private ScanResult result;
    private WifiConfiguration  wifiConfiguration;
    private TextView tvWifiName;
    private Button mBtnConnect;

    public WifiInfoDialog(Context context , WifiAdmin mWifiAdmin ,ScanResult result ,WifiConfiguration wifiConfiguration , boolean isConnected) {
        super(context , R.style.MyNoBakgroundDialog);
        this.mWifiAdmin = mWifiAdmin;
        this.result = result;
        this.wifiConfiguration = wifiConfiguration;
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_wifi_info, null));
        this.btnBack = (Button) findViewById(R.id.btn_wifi_info_back);
        this.btnBack.setOnClickListener(this);

        this.btnCancelSave = (Button) findViewById(R.id.btn_wifi_info_Cancel_save);
        this.btnCancelSave.setOnClickListener(this);

        tvWifiName = (TextView) findViewById(R.id.tv_wifi_info_name);
        tvWifiName.setText(wifiConfiguration.SSID);

        mBtnConnect = (Button) findViewById(R.id.btn_wifi_info_connect);
        mBtnConnect.setOnClickListener(this);

        if(!isConnected){
            mBtnConnect.setVisibility(View.GONE);
        }
    }



    public static void showDialog(Context context , WifiAdmin mWifiAdmin ,  ScanResult scanResult , WifiConfiguration wifiConfiguration , boolean isConnected){
        if(null == WifiInfoDialog){
            WifiInfoDialog = new WifiInfoDialog(context , mWifiAdmin , scanResult ,wifiConfiguration , isConnected);
            WifiInfoDialog.show();
        }
    }

    @Override
    public void dismiss() {
        WifiInfoDialog = null;
        super.dismiss();
    }

    public static void myDismiss() {
        if(null != WifiInfoDialog){
            if(WifiInfoDialog.isShowing()){
                WifiInfoDialog.dismiss();
            }
            WifiInfoDialog = null;
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == btnBack.getId()){
            dismiss();
            return;
        }

        if(v.getId() == btnCancelSave.getId()){
            Log.i("wifi" ,"断开指定的网络");

            mWifiAdmin.removeNetWork(wifiConfiguration);
            dismiss();
            return;
        }

        if(v.getId() == mBtnConnect.getId()){

            mWifiAdmin.enableNetWork(wifiConfiguration.networkId);
            dismiss();
            return;
        }
    }

}
