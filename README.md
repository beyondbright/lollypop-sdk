# 说明
## 这是一个完整的Demo，直接用AS打开就可以跑起来。
## 相关的蓝牙的SDK已经封装到Service，类名叫`BleAutoConnectService`，其主要接口说明如下：
- `scanAndConnect`

该方法扫描Femometer设备,并选择信号最强的设备进行连接，连接成功后会回调`BleStatus.CONNECTED`，带的参数值是对应的设备地址。该方法在绑定体温计时使用，获取到设备地址后缓存下来，以后的连接直接用`connect`方法就可以。
- `disconnect`

在连接状态下，调用该方法断开连接。
- `connect`

在已经缓存设备地址的情况下，可以直接调用该方法进行设备连接。
- `reconnect`

一般调用connect后40秒还未连接成功,可能原因有两种:

1、体温计进入休眠状态,需要插拔盖子唤醒。体温计唤醒后调用该方法进行重连。

2、手机蓝牙死机,重启蓝牙或者重启手机恢复。

## 在AS新项目中如何使用`BleAutoConnectService`类
- 添加dependencies
```
compile 'cn.lollypop.android:bm-base:0.0.2'
compile 'cn.lollypop.android:bm-bluetooth:0.0.3'
compile 'com.orhanobut:logger:1.15'
compile 'com.google.code.gson:gson:2.5'
```
- 在AndroidManifest.xml中添加权限蓝牙相关权限及service
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
- 在自定义Activity或者Application的onCreate方法中绑定BleAutoConnectService，别忘了在onDestroy方法中解绑。
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
- 在ServiceConnection中注册BleCallback回调方法
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
- 设备回调信息都在bleCallback中
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
