# 说明
- 这是一个完整的Demo，直接用AS打开就可以跑起来
- 在AS新项目中如何使用
-- 添加dependencies
```
compile 'cn.lollypop.android:bm-base:0.0.2'
compile 'cn.lollypop.android:bm-bluetooth:0.0.3'
compile 'com.orhanobut:logger:1.15'
compile 'com.google.code.gson:gson:2.5'
```
-- 在AndroidManifest.xml中添加权限蓝牙相关权限及service
```
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-feature
    android:name="android.hardware.bluetooth_le"
    android:required="false"/>
<application>
  <service android:name="cn.lollypop.android.thermometer.ble.BleAutoConnectService"/>
</application>
```
-- 在自定义Activity或者Application的onCreate方法中绑定BleAutoConnectService，别忘了在onDestroy方法中解绑。
```
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  Intent gattServiceIntent = new Intent(this, BleAutoConnectService.class);
  bindService(gattServiceIntent, serviceConnection,
      Activity.BIND_AUTO_CREATE);
}

@Override
protected void onDestroy() {
  super.onDestroy();
  unbindService(serviceConnection);
}
```
-- 在ServiceConnection中注册BleCallback回调方法
```
private final ServiceConnection serviceConnection = new ServiceConnection() {
  @Override
  public void onServiceConnected(ComponentName componentName, IBinder service) {
    bleService = ((BleAutoConnectService.LocalBinder) service).getService();
    registerBleCallback(bleCallback);
  }

  @Override
  public void onServiceDisconnected(ComponentName componentName) {
    bleService.stopScan();
    unregisterBleCallback(bleCallback);
    bleService = null;
  }
};

private void registerBleCallback(BleCallback bleCallback) {
  if (bleService != null) {
    bleService.registerBleCallback(bleCallback);
  }
}

private void unregisterBleCallback(BleCallback bleCallback) {
  if (bleService != null) {
    bleService.unregisterBleCallback(bleCallback);
  }
}
```
-- 设备回调信息都在bleCallback中
```
private final BleCallback bleCallback = new BleCallback() {
  @Override
  public void callback(BleStatus bleStatus, Object o) {
    switch (bleStatus) {
      case CONNECTED: //连接设备成功，回调的Object是设备地址
        break;
      case MEASURE_START: //与设备通讯完成（设备初始化结束），并开启体温数据的监听
        DeviceInformationServiceUtil.getFirmwareVersion(MainActivity.this); //获取设备版本号
        break;
      case DISCONNECTED: //断开设备
        break;
      case GET_BATTERY: //获取到电量，回调的Objcet是设备电量值
        break;
      case SET_ALARM: //获取当前闹钟，回调的是AlarmTimeModel类
        break;
      case MEASURE_GET: //获取到温度值，回调的Object是Temperature类
        break;
      default:
        break;
    }
  }
};
```
- 相关接口说明
-- 
