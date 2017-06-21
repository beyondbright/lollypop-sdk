package bongmi.bluetooth.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.lollypop.android.thermometer.ble.model.Temperature;
import cn.lollypop.android.thermometer.network.basic.Response;
import cn.lollypop.android.thermometer.sdk.LollypopSDK;

/**
 * Copyright (c) 2016, Bongmi
 * All rights reserved
 * Author: wangjunjie@bongmi.com
 */

public class LoginActivity extends AppCompatActivity
    implements View.OnClickListener {

  private static final String appKey = "gqqmgtHBgapew6ke";
  private EditText phoneNo;
  private EditText password;
  private TextView log;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);

    phoneNo = (EditText) findViewById(R.id.phoneNo);
    password = (EditText) findViewById(R.id.password);
    log = (TextView) findViewById(R.id.log);

    Button signUp = (Button) findViewById(R.id.signUp);
    signUp.setOnClickListener(this);
    Button signIn = (Button) findViewById(R.id.signIn);
    signIn.setOnClickListener(this);

    // enable debug model to show logs
    LollypopSDK.getInstance().enableDebug();

    LollypopSDK.getInstance().registerCallback(
        new LollypopSDK.LollypopCallback() {
          @Override
          public void createUser(Response response) {
            if (response.isSuccessful()) {
              loginSuc();
            } else {
              log.setText("code: " + response.getCode() + ", msg: "
                  + response.getMessage() + ", body: " + response.getBody());
            }
          }

          @Override
          public void login(Response response) {
            if (response.isSuccessful()) {
              loginSuc();
            } else {
              log.setText("code: " + response.getCode() + ", msg: "
                  + response.getMessage() + ", body: " + response.getBody());
            }
          }

          @Override
          public void connect() {

          }

          @Override
          public void disconnect() {

          }

          @Override
          public void receiveTemperature(Temperature temperature) {

          }
        });
    if (LollypopSDK.getInstance().isLogin(this)) {
      loginSuc();
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.signIn:
        signIn();
        break;
      case R.id.signUp:
        signUp();
        break;
    }
  }

  private void signIn() {
    if (TextUtils.isEmpty(phoneNo.getText().toString().trim())) {
      log.append("Please input phone number");
      return;
    }

    if (TextUtils.isEmpty(password.getText().toString().trim())) {
      log.append("Please input password");
      return;
    }

    LollypopSDK.getInstance().signIn(this, appKey,
        Long.valueOf(phoneNo.getText().toString()),
        password.getText().toString());
  }

  private void signUp() {
    if (TextUtils.isEmpty(phoneNo.getText().toString().trim())) {
      log.append("Please input phone number");
      return;
    }

    if (TextUtils.isEmpty(password.getText().toString().trim())) {
      log.append("Please input password");
      return;
    }

    LollypopSDK.getInstance().createUser(this, appKey,
        Long.valueOf(phoneNo.getText().toString()),
        password.getText().toString());
  }

  private void loginSuc() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}
