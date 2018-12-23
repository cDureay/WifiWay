//package yu.com.Wifi;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.baidu.location.BDAbstractLocationListener;
//import com.baidu.location.BDLocation;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.SDKInitializer;
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.MapStatusUpdate;
//import com.baidu.mapapi.map.MapStatusUpdateFactory;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.MyLocationData;
//import com.baidu.mapapi.model.LatLng;
//
//import yu.com.Wifi.wifiway.Login;
//
//public class MapActivity extends AppCompatActivity {
//    public LocationClient mLocationClient;
//    private TextView positionText;
//    private MapView mapView;
//    private BaiduMap baiduMap;
//    private boolean isFirstLocate = true;
//    public static String loc;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        mLocationClient = new LocationClient(getApplicationContext());
//        mLocationClient.registerLocationListener(new MyLocationListener());
//        SDKInitializer.initialize(getApplicationContext());
//        setContentView(R.layout.map_layout);
//        mapView = findViewById(R.id.bmapView);
//        baiduMap = mapView.getMap();
//        baiduMap.setMyLocationEnabled(true);
//        positionText = findViewById(R.id.position_text_view);
//
//        requestLocation();
//    }
//
//    private void nevigateTo(BDLocation location) {
//        if (isFirstLocate) {
//            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
//            baiduMap.animateMapStatus(update);
//            update = MapStatusUpdateFactory.zoomTo(16f);
//            baiduMap.animateMapStatus(update);
//            isFirstLocate = false;
//        }
//        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
//        locationBuilder.latitude(location.getLatitude());
//        locationBuilder.longitude(location.getLongitude());
//        MyLocationData locationData = locationBuilder.build();
//        baiduMap.setMyLocationData(locationData);
//    }
//
//    private void requestLocation() {
//        initLocation();
//        mLocationClient.start();
//    }
//
//    private void initLocation() {
//        LocationClientOption option = new LocationClientOption();
//        option.setScanSpan(5000);
//        option.setIsNeedAddress(true);
//        option.setIsNeedLocationDescribe(true);
//        //option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//        option.setCoorType("bd09ll");
//        option.setOpenGps(true);
//        //option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
//        mLocationClient.setLocOption(option);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mLocationClient.stop();
//        mapView.onDestroy();
//        baiduMap.setMyLocationEnabled(false);
//    }
//
//    public class MyLocationListener extends BDAbstractLocationListener {
//
//        //public StringBuilder location = new StringBuilder();
//
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//
//            if (location.getLocType() == BDLocation.TypeGpsLocation
//                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                nevigateTo(location);
//            }
//            StringBuilder currentPosition =new StringBuilder();
//            currentPosition.append("纬度:").append(location.getLatitude());
//            currentPosition.append("经线:").append(location.getLongitude());
//            currentPosition.append(location.getCountry());
//            currentPosition.append(location.getProvince());
//            currentPosition.append(location.getCity());
//            currentPosition.append(location.getDistrict());
//            currentPosition.append(location.getStreet());
//            currentPosition.append("定位方式:");
//            if(location.getLocType()==BDLocation.TypeGpsLocation){
//                currentPosition.append("GPS");
//            }else if(location.getLocType()==BDLocation.TypeNetWorkLocation){
//                currentPosition.append("网络");
//            }
//            loc = currentPosition.toString();
//        }
//    }
//}
