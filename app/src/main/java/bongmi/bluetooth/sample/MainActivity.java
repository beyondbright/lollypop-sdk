package bongmi.bluetooth.sample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.lollypop.android.thermometer.ble.BleAutoConnectService;
import cn.lollypop.android.thermometer.ble.BleCallback;
import cn.lollypop.android.thermometer.ble.exceptions.BleException;
import cn.lollypop.android.thermometer.ble.utils.DeviceInformationServiceUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private BleAutoConnectService bleService;
  private TextView log;
  private String address;
  private Button reconnect;
  private Button connect;
  private Button scanAndConnect;
  private Button disconnect;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    log = (TextView) findViewById(R.id.log);
    reconnect = (Button) findViewById(R.id.reconnect);
    reconnect.setOnClickListener(this);
    reconnect.setEnabled(false);

    connect = (Button) findViewById(R.id.connect);
    connect.setOnClickListener(this);
    connect.setEnabled(false);

    scanAndConnect = (Button) findViewById(R.id.scanAndConnect);
    scanAndConnect.setOnClickListener(this);

    disconnect = (Button) findViewById(R.id.disconnect);
    disconnect.setOnClickListener(this);
    disconnect.setEnabled(false);

    Intent gattServiceIntent = new Intent(this, BleAutoConnectService.class);
    bindService(gattServiceIntent, serviceConnection,
        Activity.BIND_AUTO_CREATE);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unbindService(serviceConnection);
  }

  private final ServiceConnection serviceConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName componentName,
                                   IBinder service) {
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

  private final BleCallback bleCallback = new BleCallback() {
    @Override
    public void callback(BleStatus bleStatus, Object o) {
      log.append(bleStatus.name() + ": " + o + "\n");
      switch (bleStatus) {
        case CONNECTED: //连接设备成功
          address = (String) o;
          scanAndConnect.setEnabled(false);
          disconnect.setEnabled(true);
          log.append("connect device: " + o + "\n");
          break;
        case MEASURE_START: //与设备通讯完成,开启体温数据的监听
          log.append("firmware version: " + DeviceInformationServiceUtil.getFirmwareVersion(MainActivity.this) + "\n");
          break;
        case DISCONNECTED: //断开设备
          scanAndConnect.setEnabled(true);
          connect.setEnabled(true);
          reconnect.setEnabled(true);
          log.append("disconnect \n");
          break;
        case GET_BATTERY: //获取到电量
          log.append("get battery : " + o + "\n");
          break;
        case SET_ALARM: //获取当前闹钟
          log.append("set alarm : " + o + "\n");
          break;
        case MEASURE_GET: //获取到温度值
          log.append("get temperature : " + o + "\n");
          break;
        default:
          break;
      }
    }
  };

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.reconnect:
        // 一般调用connect后40秒还未连接成功,可能原因有两种:
        // 1、体温计进入休眠状态,需要插拔盖子唤醒。体温计唤醒后调用该方法进行重连。
        // 2、手机蓝牙死机,重启蓝牙或者重启手机恢复。
        bleService.reconnect();
        break;
      case R.id.connect:
        // 在已经缓存设备地址的情况下,可以直接调用该方法进行设备连接。
        try {
          bleService.connect(address);
        } catch (BleException e) {
          e.printStackTrace();
        }
        connect.setEnabled(false);
        break;
      case R.id.disconnect:
        // 在连接状态下,调用该方法断开连接
        bleService.destroy();
        disconnect.setEnabled(false);
        break;
      case R.id.scanAndConnect:
        // 该方法扫描Femometer设备,并选择信号最强的设备进行连接,
        // 连接成功后会回调BleStatus.CONNECTED,带的参数值是对应的设备地址。
        // 该方法在绑定体温计时使用,获取到设备地址后缓存下来,以后的连接直接用connect就可以。
        bleService.startScan();
        scanAndConnect.setEnabled(false);
        break;
      default:
        break;
    }
  }
}
