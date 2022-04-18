# LollypopSDK说明
LollypopSDK提供了连接棒米体温计和棒米耳温枪的相关接口。
## 使用流程
1. 向棒米官方申请appKey
2. 使用gradle添加依赖，详见下面的相关配置
3. 初始化SDK，调用`LollypopSDK.getInstance().init()`，建议在application中调用
4. 调用`LollypopSDK.getInstance().registerCallback()`注册回调
5. 调用`LollypopSDK.getInstance().createUser()`方法创建用户或者`LollypopSDK.getInstance().signIn()`方法登录，已经登录过就不需要重新登录，是否已经登录可用方法`LollypopSDK.getInstance().isLogin()`来判断
6. 调用`LollypopSDK.getInstance().connect()`方法连接已唤醒的设备（基础体温计需要插拔盖子唤醒，耳温枪需要按一下开关唤醒）
7. 连接成功后可以调用`LollypopSDK.getInstance().getDeviceInfo()`方法获取设备信息，测温成功会执行在第三步注册的`receiveTemperature()`回调方法

## 相关配置
- 添加dependencies
```
compile 'cn.lollypop.android:LollypopSDK:2.1.4'
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

```
## 相关接口
- 初始化SDK。建议在Application中调用
```
public void init(Context context)
```
- 创建用户。向官方申请appKey之后需要创建用户才能使用该SDK

```
  /**
   * 通过手机号创建用户
   *
   * @param context  context
   * @param appKey 申请的key
   * @param phone    手机号
   * @param password 密码
   */
  public void createUser(Context context, String appKey, long phone, String password)
```
```
/**
   * 通过userId创建用户
   *
   * @param context  context
   * @param appKey 申请的key
   * @param userId  userId
   */
public void createUser(Context context, String appKey, String userId)
```

- 登录，在连接蓝牙之前必须先登录

```
  /**
   * 通过手机号登录
   *
   * @param context  context
   * @param appKey 申请的key
   * @param phone    手机号
   * @param password 密码
   */
  public void signIn(Context context, String appKey, long phone, String password)
```

```
  /**
   * 通过userId登录
   *
   * @param context  context
   * @param appKey 申请的key
   * @param userId   userId
   */
  public void signIn(Context context, String appKey, String userId)
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
   * 登出，同时清除设备地址
   *
   * @param context context
   */
  public void signOut(Context context)
```
- Android 6.0之后需要定位权限，如何使用参加demo
```
 /**
   * Android 6.0之后扫描蓝牙需要开启GPS定位
   */
  public void requestLocationPermissions(Activity activity, Callback callback)
```
- 连接体温计

```
  /**
   * 连接棒米设备，如果之前已经连接上，会直接进行连接，不再扫描
   *
   * @param deviceType 棒米基础体温计：BASAL_THERMOMETER，棒米耳温枪：SMARTTHERMO。不填默认是连接棒米基础体温计。
   *
   * @throws LollypopException      其他异常
   * @throws NoPermissionException  没有定位权限，出现这个异常可以调用 requestLocationPermissions() 方法请求权限
   * @throws NotSupportBleException 该手机不支持BLE
   * @throws NotEnableBleException  蓝牙没打开
   * @throws NoDeviceExistException 硬件设备类型异常，直接调用connect方法不会出现这个异常
   */
  public void connect(DeviceType deviceType) throws LollypopException, NoPermissionException,
      NotSupportBleException, NotEnableBleException, NoDeviceExistException
```

一般调用connect后40秒还未连接成功,可能原因有两种:

1、设备进入休眠状态,需要唤醒。设备唤醒后调用connect方法进行重连。

2、手机蓝牙死机，重启蓝牙或者重启手机恢复。

- 断开连接。如果在Activity中调用connect方法，那在Activity销毁的时候需要调用disconnect方法。

```
  /**
   * 断开连接
   *
   * @throws LollypopException
   */
  public void disconnect() throws LollypopException
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

- 设置单位
```
  /**
   * 设置耳温枪的单位
   *
   * @param isCentigrade 是否设置成摄氏度
   */
  public void setUnit(boolean isCentigrade) throws LollypopException
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
    void connect(boolean suc);

    // 断开连接回调
    void disconnect();

    // 收到体温数据 Temperature {temperatureInt: 温度Int型（比如收到3655，就是36.55摄氏度），measureTimestamp：测温的时间戳，calculate：是否是预测值，deviceUserId：预留字段}
    void receiveTemperature(Temperature temperature);
  }
```
