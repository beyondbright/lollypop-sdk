# 说明
LollypopSDK提供了连接棒米体温计(Femometer)并与之交互的接口。
## 相关接口
- 创建用户，所有体温都对应一个用户，没有用户必须先创建用户。

```
  /**
   * 创建用户
   *
   * @param context  context
   * @param appKey 申请的key
   * @param phone    手机号
   * @param password 密码
   */
  public void createUser(Context context, String appKey, long phone, String password)
```

- 登录，在连接蓝牙之前必须先登录。

```
  /**
   * 登录
   *
   * @param context  context
   * @param phone    手机号
   * @param password 密码
   */
  public void signIn(Context context, String appKey, long phone, String password)
```

- 登出，需要切换用户的时候先调用登出方法，再重新登录

```
  /**
   * 登出
   *
   * @param context context
   */
  public void signOut(Context context)
```

- 连接体温计

```
  /**
   * 连接体温计
   *
   * @param context context
   * @throws LollypopException
   */
  public void connect(Context context) throws LollypopException
```

一般调用connect后40秒还未连接成功,可能原因有两种:

1、体温计进入休眠状态,需要插拔盖子唤醒。体温计唤醒后调用该方法进行重连。

2、手机蓝牙死机,重启蓝牙或者重启手机恢复。

- 断开连接。在Activity中调用connect方法，那在Activity销毁的时候需要调用disconnect方法。因为在connect方法中有绑定蓝牙的service，需要在disconnect方法中解绑。

```
  /**
   * 断开连接
   *
   * @param context context
   * @throws LollypopException
   */
  public void disconnect(Context context) throws LollypopException
```

- 获取设备信息，连接成功后可以调用该接口获取设备信息

```
  /**
   * 获取设备信息
   *
   * @param context context
   * @return DeviceInfo 设备信息{address: 蓝牙地址，battery: 电量，version：固件版本}
   * @throws LollypopException
   */
  public DeviceInfo getDeviceInfo(Context context) throws LollypopException
```

- 设置回调方法

```
  /**
   * 注册回调方法
   *
   * @param lollypopCallback LollypopCallback
   */
  public void registerCallback(LollypopCallback lollypopCallback)

  public interface LollypopCallback {
    // 创建用户回调
    void createUser(boolean result, String errorMsg);

    // 登录回调
    void login(boolean result, String errorMsg);

    // 连接成功回调
    void connect();

    // 断开连接回调
    void disconnect();

    // 收到体温数据 Temperature {temperatureInt: 温度Int型（比如收到3655，就是36.55摄氏度），measureTimestamp：测温的时间戳，calculate：是否是预测值，deviceUserId：预留字段}
    void receiveTemperature(Temperature temperature);
  }
```

## 在AS新项目中如何使用
- 添加dependencies
```
compile 'cn.lollypop.android:bm-base:0.0.2'
compile 'cn.lollypop.android:bm-bluetooth:0.0.3'
compile 'com.orhanobut:logger:1.15'
compile 'com.google.code.gson:gson:2.5'
```
- 在AndroidManifest.xml中添加权限蓝牙相关权限及Service
```
<!-- 蓝牙 -->
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-feature
    android:name="android.hardware.bluetooth_le"
    android:required="false"/>
<!-- 网络 -->
<uses-permission android:name="android.permission.INTERNET"/>

<application>
  <service android:name="cn.lollypop.android.thermometer.ble.BleAutoConnectService"/>
</application>
```
