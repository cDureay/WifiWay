package yu.com.Wifi.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;


public  class WifiAdmin {
    private WifiManager mWifiManager;

    private OnWifiStateChangeListener onWifiStateChangeListener;
    private OnWifiSupplicantStateChangeListener onWifiSupplicantStateChangeListener;
    private OnWifiScanResultsListener onWifiScanResultsListener;
    private OnWifiConnectSuccessListener onWifiConnectSuccessListener;
    private OnWifiConnectintListener onWifiConnectintListener;
    private OnWifiIDLEListener onWifiIDLEListener;
    private OnWifiRSSIListener onWifiRSSIListener;
    private OnWifiPWDErrorListener onWifiPWDErrorListener;
    private Context context;
    private WifiBroadcast  mWifiReceiver;


    public WifiAdmin (Context context) {
        this.context = context;

        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        registerReceiver(context);
    }




    private void registerReceiver(Context context){

        mWifiReceiver = new WifiBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(mWifiReceiver ,intentFilter);
    }

    public void closeReceiver(){
        if(mWifiReceiver != null){
            context.unregisterReceiver(mWifiReceiver);
        }
    }


    public boolean connectNewNetwork(List<ScanResult> results, String SSID, String pwd){
        int netId = addWifiConfig(results , SSID , pwd);

        if(netId != -1){
            return mWifiManager.enableNetwork(netId , true);
        }
        return false;
    }


    public boolean connectNewNetwork(ScanResult result , String pwd){
        int netId = addWifiConfig(result , pwd);

        if(netId != -1){
            return mWifiManager.enableNetwork(netId , true);
        }
        return false;
    }


    private int addWifiConfig(List<ScanResult> wifiList,String ssid,String pwd){
        int wifiId = -1;
        for(int i = 0;i < wifiList.size(); i++){
            ScanResult wifi = wifiList.get(i);
            if(wifi.SSID.equals(ssid)){
                WifiConfiguration wifiCong = new WifiConfiguration();
                wifiCong.allowedAuthAlgorithms.clear();
                wifiCong.allowedGroupCiphers.clear();
                wifiCong.allowedKeyManagement.clear();
                wifiCong.allowedPairwiseCiphers.clear();
                wifiCong.allowedProtocols.clear();

                wifiCong.SSID = "\""+wifi.SSID+"\"";

                if(pwd == null || pwd.equals(""))
                {
                    wifiCong.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                }

                else
                {
                    wifiCong.preSharedKey = "\""+pwd+"\"";
                    wifiCong.hiddenSSID = false;
                    wifiCong.status = WifiConfiguration.Status.ENABLED;
                }
                wifiId = mWifiManager.addNetwork(wifiCong);
                if(wifiId != -1){
                    return wifiId;
                }
            }
        }
        return wifiId;
    }


    private int addWifiConfig(ScanResult result,String pwd){
        int wifiId = -1;
        WifiConfiguration wifiCong = new WifiConfiguration();
        wifiCong.allowedAuthAlgorithms.clear();
        wifiCong.allowedGroupCiphers.clear();
        wifiCong.allowedKeyManagement.clear();
        wifiCong.allowedPairwiseCiphers.clear();
        wifiCong.allowedProtocols.clear();

        wifiCong.SSID = "\""+result.SSID+"\"";

        if(pwd == null || pwd.equals(""))
        {
            wifiCong.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        else
        {
            wifiCong.preSharedKey = "\""+pwd+"\"";
            wifiCong.hiddenSSID = false;
            wifiCong.status = WifiConfiguration.Status.ENABLED;
        }
        wifiId = mWifiManager.addNetwork(wifiCong);
        if(wifiId != -1){
            return wifiId;
        }
        return wifiId;
    }


    public void startScan(){
        if(mWifiManager != null){
            mWifiManager.startScan();
        }
    }

    private boolean isStartScanMode = true;
    private Long sleepTime = 3000L;


    public void startScanMode(){
        if(isStartScanMode){
            isStartScanMode = false;
            new ScanThread().start();
        }
    }

   public void setScanCycle(Long time){
        sleepTime = time;
    }


    /**
     * 关闭扫描模式
     */
    public void closeScanMode(){
        isStartScanMode = true;
    }


    public boolean enableNetWork(int networkId){
        if(mWifiManager != null){
            return mWifiManager.enableNetwork( networkId,true);
        }
        return false;
    }

    /**
     * 获取已连接上的wifi名称
     * @return
     */
    public String getConnectWifiSSID(){
        if(mWifiManager != null){
            return mWifiManager.getConnectionInfo().getSSID();
        }
        return "";
    }


    public WifiInfo getConnectInfo(){
        if(mWifiManager != null){
            return mWifiManager.getConnectionInfo();
        }
        return null;
    }

    /**
     * 断开指定ID的网络
     * @param netId wifi的id
     */
    public void disconnectWifi(int netId) {
        if(mWifiManager != null){
            mWifiManager.disableNetwork(netId);
            mWifiManager.disconnect();
        }
    }

    /**
     * 取消保存
     */
    public void removeNetWork(WifiConfiguration wifiConfiguration){
        if(mWifiManager != null && wifiConfiguration != null){
            //移除网络
            mWifiManager.removeNetwork(wifiConfiguration.networkId);
            //重新保存配置
            mWifiManager.saveConfiguration();
            mWifiManager.startScan();//重新扫描
        }
    }

    /**
     * 取消保存
     */
    public void removeNetWork(int networkId){
        //移除网络
        mWifiManager.removeNetwork(networkId);
        //重新保存配置
        mWifiManager.saveConfiguration();
        mWifiManager.startScan();//重新扫描
    }


    /**
     * 获取wifi状态，返回int值，默认为已关闭
     * @return
     */
    public int getWifiState(){
        return STATE;
    }

    /**
     * 设置wifi开关
     * @param enabled false关闭,true打开
     * @return
     */
    public void setWifiEnabled(boolean enabled){
        //判断wifi是不是未找到的状态，如果不是就进行设置
        if(STATE != UNKNOWN){
            if(enabled){

                if(STATE == DISABLED){
                    mWifiManager.setWifiEnabled(enabled);
                }
            }else{

                if(STATE == ENABLE){
                    mWifiManager.setWifiEnabled(enabled);
                }
            }
        }
    }


    public void setOnScanResultsListener(OnWifiScanResultsListener listener) {
        onWifiScanResultsListener = listener;
    }


    public void setOnWifiSupplicantStateChangeListener(OnWifiSupplicantStateChangeListener listener) {
        onWifiSupplicantStateChangeListener = listener;
    }


    public void setOnWifiPWDErrorListener(OnWifiPWDErrorListener listener) {
        onWifiPWDErrorListener = listener;
    }


    public void setOnRSSIListener(OnWifiRSSIListener listener) {
        onWifiRSSIListener = listener;
    }


    public void setOnWifiConnectSuccessListener(OnWifiConnectSuccessListener listener) {
        onWifiConnectSuccessListener = listener;
    }


    public void setOnWifiConnectingListener(OnWifiConnectintListener listener) {
        onWifiConnectintListener = listener;
    }


    public void setOnWifiIDLEListener(OnWifiIDLEListener listener) {
        onWifiIDLEListener = listener;
    }


    public void setOnWifiStateChangeListener(OnWifiStateChangeListener listener) {
        onWifiStateChangeListener = listener;
    }



    public static final int DISABLED = 01;


    public static final int DISABLING = 02;


    public static final int ENABLE = 03;


    public static final int ENABLING = 04;


    public static final int UNKNOWN = 05;


    private int STATE = 01;



    public static interface OnWifiRSSIListener{


        void onWifiRSSI(List<ScanResult> scanResult, List<WifiConfiguration> configurations);
    }


    public static interface OnWifiScanResultsListener {


        void onWifiScanResults(List<ScanResult> scanResult, List<WifiConfiguration> configurations);
    }


    public static interface OnWifiSupplicantStateChangeListener {


        void OnWifiSupplicantStateChange(List<ScanResult> scanResult, List<WifiConfiguration> configurations);
    }


    public static interface OnWifiPWDErrorListener {


        void onWifiPWDError(WifiConfiguration configuration);
    }


    public static interface OnWifiConnectSuccessListener {


        void onWifiSuccess(WifiInfo wifiInfo, boolean isLock);
    }


    public static interface OnWifiConnectintListener {


        void onWifiConnecting(WifiInfo wifiInfo, boolean isLock);
    }


    public static interface OnWifiIDLEListener {


        void onWifiIDLE();
    }


    public static interface OnWifiStateChangeListener{

        void onWifiStateChange(int state);

    }

    private WifiConfiguration getWifiConfiguration(String SSID){
        WifiConfiguration wifiConfiguration = null;
        if(mWifiManager != null){
            for (WifiConfiguration wcg : mWifiManager.getConfiguredNetworks()){
                if(wcg.SSID.equals(SSID)){
                    wifiConfiguration = wcg;
                    break;
                }
            }
        }
        return  wifiConfiguration;
    }


    private ScanResult getScanResult(String SSID){
        ScanResult result = null;
        if(mWifiManager != null){
            for (ScanResult s : mWifiManager.getScanResults()){
                if(s.SSID.equals(SSID.replace("\"", ""))){
                    result = s;
                    break;
                }
            }
        }
        return  result;
    }


    public static String getCapability(String capability , Context context){
        String capabilityInfo = "";
        if(capability.contains("WPA2")){
            if(capability.contains("WPA")){
                capabilityInfo = "通过WPA/WPA2进行保护";
            }
            else if (capability.contains("WPS")){
                capabilityInfo = "通过WPA2进行保护(可使用WPS)";
            }
            else{
                capabilityInfo = "通过WPA2进行保护";
            }
        }
        else if (capability.contains("WPS")){
            capabilityInfo = "可使用WPS";
        }

        return capabilityInfo;
    }


    public static int getCapabilityType(String capability){
        int type = 0;
        if(capability.contains("WPA2")){
            if(capability.contains("WPA")){
                type = 3;
            }
            else if (capability.contains("WPS")){
                type = 3;
            }
            else{
                type = 3;
            }
        }
        else if (capability.contains("WPS")){
            type = 1;
        }

        return type;
    }


    class ScanThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (!isStartScanMode){
                try {

                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!isStartScanMode){
                    if(mWifiManager != null){

                        if(getWifiState() == ENABLE){
                            startScan();
                        }
                    }
                }
            }
        }
    }



    public class WifiBroadcast extends BroadcastReceiver{

        @SuppressWarnings("static-access")
        @Override
        public void onReceive(Context context, Intent intent) {

            if(WifiManager.WIFI_STATE_CHANGED_ACTION .equals(intent.getAction())){
                if(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED){

                    STATE = DISABLED;
                    if(onWifiStateChangeListener != null) {
                        onWifiStateChangeListener.onWifiStateChange(DISABLED);
                    }
                }else if(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING){

                    STATE = DISABLING;
                    if(onWifiStateChangeListener != null) {
                        onWifiStateChangeListener.onWifiStateChange(DISABLING);
                    }
                }else if(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING){

                    STATE = ENABLING;
                    if(onWifiStateChangeListener != null) {

                        mWifiManager.startScan();
                        onWifiStateChangeListener.onWifiStateChange(ENABLING);
                    }
                }else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){

                    STATE = ENABLE;
                    if(onWifiStateChangeListener != null) {

                        mWifiManager.startScan();
                        onWifiStateChangeListener.onWifiStateChange(ENABLE);
                    }
                }else{

                    STATE = UNKNOWN;
                    if(onWifiStateChangeListener != null) {
                        onWifiStateChangeListener.onWifiStateChange(UNKNOWN);
                    }
                }
            }

            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION .equals(intent.getAction())){
                if(onWifiScanResultsListener != null){
                    onWifiScanResultsListener.onWifiScanResults(mWifiManager.getScanResults() , mWifiManager.getConfiguredNetworks());
                }
            }

            if(WifiManager.RSSI_CHANGED_ACTION.equals(intent.getAction())){
                if(onWifiRSSIListener != null){
                    onWifiRSSIListener.onWifiRSSI(mWifiManager.getScanResults() , mWifiManager.getConfiguredNetworks());
                }
            }

            if(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction())){
                SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if(onWifiIDLEListener != null){

                    if(mWifiManager.getConnectionInfo().getDetailedStateOf(supplicantState).equals(DetailedState.IDLE)){
                        onWifiIDLEListener.onWifiIDLE();
                    }
                }
                if(onWifiConnectintListener != null){

                    if(mWifiManager.getConnectionInfo().getDetailedStateOf(supplicantState).equals(DetailedState.CONNECTING)){
                        ScanResult result = getScanResult(mWifiManager.getConnectionInfo().getSSID());

                        boolean isLock = true;
                        if(result != null){
                            isLock = (getCapabilityType(result.capabilities) == 0 || getCapabilityType(result.capabilities) ==1) ? false : true;
                        }
                        onWifiConnectintListener.onWifiConnecting(mWifiManager.getConnectionInfo() , isLock);
                    }
                }
                if(onWifiConnectSuccessListener != null){
                    if(mWifiManager.getConnectionInfo().getDetailedStateOf(supplicantState).equals(DetailedState.OBTAINING_IPADDR)){

                        ScanResult result = getScanResult(mWifiManager.getConnectionInfo().getSSID());

                        boolean isLock = true;
                        if(result != null){
                            isLock = (getCapabilityType(result.capabilities) == 0 || getCapabilityType(result.capabilities) ==1) ? false : true;
                        }
                        onWifiConnectSuccessListener.onWifiSuccess(mWifiManager.getConnectionInfo() , isLock);
                    }
                }
                if(onWifiPWDErrorListener != null){
                    int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                    if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                        WifiConfiguration wcg = getWifiConfiguration(mWifiManager.getConnectionInfo().getSSID());

                        removeNetWork(wcg);

                        onWifiPWDErrorListener.onWifiPWDError(wcg);
                    }
                }
                if(onWifiSupplicantStateChangeListener != null){
                    onWifiSupplicantStateChangeListener.OnWifiSupplicantStateChange(mWifiManager.getScanResults() , mWifiManager.getConfiguredNetworks());
                }
            }
        }
    }




}
