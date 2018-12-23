package yu.com.Wifi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import yu.com.Wifi.util.WifiAdmin;
import yu.com.Wifi.R;

import java.util.List;


public class InputWifiPWDDialog extends Dialog implements View.OnClickListener {
    public static InputWifiPWDDialog shutdownDialog;

    private Button btnCancel;
    private Button btnSet;
    private EditText editTextPWD;
    private WifiAdmin mWifiAdmin;
    private ScanResult scanResult;
    private List<ScanResult> resultList;
    private int capabilityType;
    private TextView tvWifiName;

    public InputWifiPWDDialog(Context context , WifiAdmin mWifiAdmin , List<ScanResult> resultList, ScanResult scanResult , int capabilityType) {
        super(context , R.style.MyNoBakgroundDialog);
        this.mWifiAdmin = mWifiAdmin;
        this.scanResult = scanResult;
        this.resultList = resultList;
        this.capabilityType = capabilityType;
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_input_wifi_pwd, null));
        this.btnCancel = (Button) findViewById(R.id.btn_wifi_cancel);
        this.btnCancel.setOnClickListener(this);

        this.btnSet = (Button) findViewById(R.id.btn_wifi_connect);
        this.btnSet.setOnClickListener(this);

        tvWifiName = (TextView) findViewById(R.id.tv_wifi_name);
        tvWifiName.setText(scanResult.SSID);

        editTextPWD = (EditText) findViewById(R.id.edtTxt_wifi_pwd);

        setListener();
    }

    public void setListener(){
        editTextPWD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 8){
                    btnSet.setEnabled(true);
                }else{
                    btnSet.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public static void showDialog(Context context , WifiAdmin mWifiAdmin , List<ScanResult> resultList, ScanResult scanResult , int capabilityType){
        if(null == shutdownDialog){
            shutdownDialog = new InputWifiPWDDialog(context , mWifiAdmin , resultList ,scanResult , capabilityType);
            shutdownDialog.show();
        }
    }

    @Override
    public void dismiss() {
        shutdownDialog = null;
        super.dismiss();
    }


    public static void myDismiss() {
        if(null != shutdownDialog){
            if(shutdownDialog.isShowing()){
                shutdownDialog.dismiss();
            }
            shutdownDialog = null;
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == btnCancel.getId()){
            dismiss();
            return;
        }

        if(v.getId() == btnSet.getId()){

            mWifiAdmin.connectNewNetwork(resultList ,scanResult.SSID,editTextPWD.getText().toString());
            dismiss();
            return;
        }
    }

}
