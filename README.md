# LollypopSDK说明
LollypopSDK提供了连接棒米体温计(Femometer)的相关接口。
## 使用流程
1. 向棒米官方申请appKey
2. 使用gradle添加依赖，详见下面的相关配置
3. 初始化SDK，调用`LollypopSDK.getInstance().init()`，建议在application中调用
4. 调用`LollypopSDK.getInstance().registerCallback`注册回调
5. 调用`LollypopSDK.getInstance().createUser()`方法创建用户或者`LollypopSDK.getInstance().signIn()`方法登录，已经登录过就不需要重新登录，是否已经登录可用方法`LollypopSDK.getInstance().isLogin()`来判断
6. 调用`LollypopSDK.getInstance().connect()`方法连接体温计，同时插拔一下体温计以唤醒体温计
7. 连接成功后可以调用`LollypopSDK.getInstance().getDeviceInfo()`方法获取设备信息，测温成功会执行在第三步注册的`receiveTemperature()`回调方法

## 相关配置
- 添加dependencies
```
compile 'cn.lollypop.android:LollypopSDK:1.4.3'
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
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

<application>
  <service android:name="cn.lollypop.android.thermometer.ble.BleAutoConnectService"/>
</application>
```
## 相关接口
- 初始化SDK。建议在Application中调用
```
public void init(Context context)
```
- 创建用户。向官方申请appKey之后需要创建用户才能使用该SDK

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

- 登录，在连接蓝牙之前必须先登录

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

- 是否已经登录，已经登录过了就不需要重新登录了

```
  /**
   * 是否已经登录
   * @param context context
   * @return 已经登录则返回true
   */
  public boolean isLogin(Context context)
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

- 断开连接。如果在Activity中调用connect方法，那在Activity销毁的时候需要调用disconnect方法。因为在connect方法中有绑定蓝牙的service，需要在disconnect方法中解绑。

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
    void createUser(Response response);

    // 登录回调
    void login(Response response);

    // 连接成功回调
    void connect();

    // 断开连接回调
    void disconnect();

    // 收到体温数据 Temperature {temperatureInt: 温度Int型（比如收到3655，就是36.55摄氏度），measureTimestamp：测温的时间戳，calculate：是否是预测值，deviceUserId：预留字段}
    void receiveTemperature(Temperature temperature);
  }
```

- 是否需要GPS授权

```
  /**
   * 是否需要开启GPS定位, Android 6.0之后扫描蓝牙需要开启GPS定位！
   * @param context context
   * @return true 需要开启GPS定位，false 不需要开启GPS定位
   */
  public boolean needGPSPermission(Context context)
```
