package com.example.administrator.baidumaps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient locationClient;
    LatLng mMyLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.mapview);
        mBaiduMap = mMapView.getMap();
        MapStatus mapStatus = new MapStatus.Builder()
                .overlook(0)// 地图俯仰的角度  -45--0
                .zoom(15)//缩放的级别  3-21
                .build();

        BaiduMapOptions options = new BaiduMapOptions()
                .zoomControlsEnabled(false)// 不显示缩放的控件
                .zoomGesturesEnabled(true)// 是否允许缩放的手势
                // 具体查看API
                .mapStatus(mapStatus);


        // 目前来说，设置只能通过MapView的构造方法来添加,所以Demo里面是在布局中添加MapView
        // 后面项目实施会动态创建
//        MapView mapView = new MapView(this,options);

        // 为地图设置状态监听
        mBaiduMap.setOnMapStatusChangeListener(mapStatusListener);
        mBaiduMap.setOnMarkerClickListener(MakerListener);
        initView();
    }

    private BaiduMap.OnMapStatusChangeListener mapStatusListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {

            Toast.makeText(MainActivity.this, "状态变化：纬度：" + mapStatus.target.latitude + "经度：" + mapStatus.target.longitude, Toast.LENGTH_SHORT).show();

        }
    };

    private void initView() {
        Button btn_sate = (Button) findViewById(R.id.btn_sate);
        Button btn_location = (Button) findViewById(R.id.btn_location);
        btn_sate.setOnClickListener(this);
        btn_location.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sate: {
                //切换卫星和普通的视图

                // 判断一下是什么视图
                if (mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_SATELLITE) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    return;
                }
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            }
            break;
            case R.id.btn_location: {
                // 定位相关

                /**
                 * 1. 开启定位图层
                 * 2. 初始化LocationClient
                 * 3. 配置一些定位相关的参数LocationClientOption
                 * 4. 设置监听，定位的监听
                 * 5. 开启定位
                 */

                mBaiduMap.setMyLocationEnabled(true);// 打开定位
                locationClient = new LocationClient(getApplicationContext());
                LocationClientOption option = new LocationClientOption();
                option.setOpenGps(true);
                option.setCoorType("bd09ll");
                option.setIsNeedAddress(true);
               // option.setScanSpan(5000);
                locationClient.setLocOption(option);
                locationClient.registerLocationListener(locationListener);
                locationClient.start();
                locationClient.requestLocation();
            }
            break;
        }
    }

    private BDLocationListener locationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            /**
             * 在定位监听里面，可以根据我们的结果来处理，显示定位的数据。。。。
             */

            if (bdLocation == null) {
                // 没有定位信息，重新定位，重新请求定位信息
                locationClient.requestLocation();// 请求定位
                return;
            }

            double lng = bdLocation.getLongitude();// 获取经度
            double lat = bdLocation.getLatitude();// 获取纬度

            //  Toast.makeText(MainActivity.this, "经度："+lng+"纬度："+lat, Toast.LENGTH_SHORT).show();
            MyLocationData myLocationData = new MyLocationData.Builder()
                    .latitude(lat)
                    .longitude(lng)
                    .accuracy(100f)
                    .build();
            mBaiduMap.setMyLocationData(myLocationData);
            mMyLocation = new LatLng(lat, lng);
            moveToMyLocation();
            addOverLay(new LatLng(lat + 0.01, lng + 0.01));
        }

        private void moveToMyLocation() {
            MapStatus mapStatus = new MapStatus.Builder()
                    .rotate(0)
                    .zoom(20)
                    .target(mMyLocation)
                    .build();

            MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
            mBaiduMap.animateMapStatus(update);
        }
    };
    private BitmapDescriptor dot = BitmapDescriptorFactory.fromResource(R.drawable.pic3);
    private BitmapDescriptor dot_click = BitmapDescriptorFactory.fromResource(R.drawable.treasure_expanded);

    private void addOverLay(LatLng latLng) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.icon(dot);
        mBaiduMap.addOverlay(options);
    }

    private BaiduMap.OnMarkerClickListener MakerListener = new BaiduMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            InfoWindow inforWindow = new InfoWindow(dot_click, marker.getPosition(), 0, new
                    InfoWindow.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick() {

                }
            });
            mBaiduMap.showInfoWindow(inforWindow);
            return false;
        }
    };
}