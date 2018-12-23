package yu.com.Wifi.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import yu.com.Wifi.util.WifiAdmin;
import yu.com.Wifi.R;

import java.util.List;


public class WifiListAdapter extends BaseAdapter{
    private Context context;
    private List<ScanResult> scanResults;
    private List<WifiConfiguration> wifiConfigurationList;
    private WifiInfo wifiInfo = null;
    private OnWifiItemClickListener onItemClickListener;

    public WifiListAdapter(Context context ){
        this.context = context;
    }


    public void setData(List<ScanResult> scanResults ,  WifiInfo wifiInfo , List<WifiConfiguration> wifiConfigurationList){

        if(wifiConfigurationList != null){
            for (WifiConfiguration wcg: wifiConfigurationList) {
                Log.i("wifi" , "wifi配置大小" + wifiConfigurationList.size() + "---名称" + wcg.SSID);
            }
            this.scanResults = scanResults;
        }
        this.wifiInfo = wifiInfo;
        this.wifiConfigurationList = wifiConfigurationList;
        notifyDataSetChanged();
    }


    public void clearData(){
        scanResults.clear();
        wifiConfigurationList.clear();
        notifyDataSetChanged();
    }



    public static final int CONNECTED = 03;


    public static final int CONNECTING = 02;


    public static final int SAVED = 01;


    public static final int UNSAVED = 00;


    @Override
    public int getCount() {
        return scanResults == null ? 0 : scanResults.size();
    }

    @Override
    public Object getItem(int position) {
        return scanResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        boolean isSaved = false;
        boolean isConnected = false;
        boolean isConnecting = false;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.wifi_list_item , null);
            viewHolder.ssid = (TextView) convertView.findViewById(R.id.item_wifi_ssid);
            viewHolder.state = (TextView) convertView.findViewById(R.id.item_wifi_state);
            viewHolder.capability = (TextView) convertView.findViewById(R.id.item_wifi_capability);
            viewHolder.level = (TextView) convertView.findViewById(R.id.item_wifi_level);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.level.setText(scanResults.get(position).level + "");
        viewHolder.ssid.setText(scanResults.get(position).SSID);
        viewHolder.capability.setText(WifiAdmin.getCapability(scanResults.get(position).capabilities , null));
        if(wifiInfo != null){

            if(wifiInfo.getSSID() .equals( "\""+scanResults.get(position).SSID+"\"") && wifiInfo.getSupplicantState().equals(SupplicantState.ASSOCIATING) ){
                viewHolder.capability.setText("正在连接...");
                isConnecting = true;
                isSaved = true;
            }

            else if(wifiInfo.getSSID() .equals( "\""+scanResults.get(position).SSID+"\"") && wifiInfo.getSupplicantState().equals(SupplicantState.COMPLETED) ){
                isConnected = true;
                isSaved = true;
                viewHolder.capability.setText("已连接");
            }

            else if(wifiInfo.getSSID() .equals( "\""+scanResults.get(position).SSID+"\"") && wifiInfo.getSupplicantState().equals(SupplicantState.SCANNING) ){
                viewHolder.capability.setText("正在进行验证身份...");
                isConnecting = true;
                isSaved = true;
            }else{
                if(wifiInfo.getSSID() .equals( "\""+scanResults.get(position).SSID+"\"")){
                    isSaved = true;
                }
            }
        }
        for(int i = 0; i < wifiConfigurationList.size() ; i ++){
            if(wifiConfigurationList.get(i).SSID .equals("\""+scanResults.get(position).SSID+"\"" )&& !wifiConfigurationList.get(i).SSID.equals(wifiInfo.getSSID())){
                viewHolder.capability.setText(WifiAdmin.getCapability(scanResults.get(position).capabilities , null) == "" ? "已保存" : "已保存，" + WifiAdmin.getCapability(scanResults.get(position).capabilities , null));
                isSaved = true;
            }
        }


        final boolean finalIsSaved = isSaved;
        final boolean finalIsConnected = isConnected;
        final boolean finalIsConnecting = isConnecting;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    int state = 0;
                    WifiConfiguration wifiConfiguration = null;

                    if(finalIsSaved)
                    {

                        if(wifiConfigurationList.size() > 0){
                            for(int i = 0; i < wifiConfigurationList.size() ; i ++){

                                if(wifiConfigurationList.get(i).SSID .equals("\""+scanResults.get(position).SSID+"\"" )){

                                    wifiConfiguration = wifiConfigurationList.get(i);
                                }
                            }
                        }
                        if(finalIsConnected){
                            state = CONNECTED;
                        }
                        else if(finalIsConnecting){
                            state = CONNECTING;
                        }else{
                            state = SAVED;
                        }
                    }
                    else
                    {

                        state = UNSAVED;
                    }

                    onItemClickListener.onWifiItemClick(scanResults ,scanResults.get(position) ,wifiConfiguration ,position ,WifiAdmin.getCapabilityType(scanResults.get(position).capabilities) ,state);
                }
            }
        });
        return convertView;
    }

    public class ViewHolder{
        public TextView ssid;
        public TextView capability;
        public TextView state;
        public TextView level;
    }



    public void setOnItemClickListener(OnWifiItemClickListener listener) {
        onItemClickListener = listener;
    }


    public static interface OnWifiItemClickListener {


        void onWifiItemClick(List<ScanResult> resultList, ScanResult result, WifiConfiguration wifiConfiguration, int capabilityType, int position, int connectionStatus);
    }
}
