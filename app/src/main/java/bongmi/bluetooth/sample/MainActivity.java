package bongmi.bluetooth.sample;

import java.util.ArrayList;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.lollypop.android.thermometer.ble.model.Temperature;
import cn.lollypop.android.thermometer.device.storage.DeviceInfo;
import cn.lollypop.android.thermometer.network.basic.Response;
import cn.lollypop.android.thermometer.sdk.LollypopSDK;
import cn.lollypop.be.exception.LollypopException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private TextView log;
  private Button disconnect;
  private Button connect;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    log = (TextView) findViewById(R.id.log);
    disconnect = (Button) findViewById(R.id.disconnect);
    disconnect.setOnClickListener(this);
    disconnect.setEnabled(false);

    connect = (Button) findViewById(R.id.connect);
    connect.setOnClickListener(this);

    Button getDeviceInfoBtn = (Button) findViewById(R.id.getDeviceInfo);
    getDeviceInfoBtn.setOnClickListener(this);
    Button signOut = (Button) findViewById(R.id.signOut);
    signOut.setOnClickListener(this);

    LollypopSDK.getInstance().registerCallback(
        new LollypopSDK.LollypopCallback() {
          @Override
          public void createUser(Response response) {}

          @Override
          public void login(Response response) {}

          @Override
          public void connect() {
            log.append("connected\n");
            disconnect.setEnabled(true);
          }

          @Override
          public void disconnect() {
            log.append("disconnect\n");
          }

          @Override
          public void receiveTemperature(Temperature temperature) {
            log.append(temperature.toString() + "\n");
          }
        });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    try {
      LollypopSDK.getInstance().disconnect(this);
    } catch (LollypopException e) {
      log.append(e.getMessage() + "\n");
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.disconnect:
        try {
          LollypopSDK.getInstance().disconnect(this);
        } catch (LollypopException e) {
          log.append(e.getMessage() + "\n");
        }
        connect.setEnabled(true);
        disconnect.setEnabled(false);
        break;

      case R.id.connect:
        doConnect();
        break;

      case R.id.getDeviceInfo:
        try {
          DeviceInfo deviceInfo = LollypopSDK.getInstance().getDeviceInfo(this);
          log.append(deviceInfo.toString() + "\n");
        } catch (LollypopException e) {
          log.append(e.getMessage() + "\n");
        }
        break;

      case R.id.signOut:
        LollypopSDK.getInstance().signOut(this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        break;
      default:
        break;
    }
  }

  private void doConnect() {
    if (LollypopSDK.getInstance().needGPSPermission(this)) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        ArrayList<String> permissions = new ArrayList<>();

        if (!(ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)) {
          permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!(ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)) {
          permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissions.size() > 0) {
          requestPermissions(permissions.toArray(
              new String[permissions.size()]), 100);
        }
      }
      log.append("请开启GPS并授权\n");
      return;
    }

    try {
      LollypopSDK.getInstance().connect(this);
    } catch (LollypopException e) {
      log.append(e.getMessage() + "\n");
    }
    connect.setEnabled(false);
  }
}
