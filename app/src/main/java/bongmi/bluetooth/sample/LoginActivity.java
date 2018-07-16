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

import com.bm.android.thermometer.ble.model.Growp;
import com.bm.android.thermometer.ble.model.Temperature;
import com.bm.android.thermometer.network.basic.Response;
import com.bm.android.thermometer.sdk.LollypopSDK;

/**
 * Copyright (c) 2016, Bongmi
 * All rights reserved
 * Author: wangjunjie@bongmi.com
 */

public class LoginActivity extends AppCompatActivity
    implements View.OnClickListener {

  private static final String appKey = "gqqmgtHBgapew6ke";
  private EditText phoneNo;
  private EditText userId;
  private EditText password;
  private TextView log;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);

    phoneNo = findViewById(R.id.phoneNo);
    userId = findViewById(R.id.userId);
    password = findViewById(R.id.password);
    log = findViewById(R.id.log);

    Button signUp = findViewById(R.id.signUp);
    signUp.setOnClickListener(this);
    Button signIn = findViewById(R.id.signIn);
    signIn.setOnClickListener(this);
    Button signUpByUserId = findViewById(R.id.signUpByUserId);
    signUpByUserId.setOnClickListener(this);
    Button signInByUserId = findViewById(R.id.signInByUserId);
    signInByUserId.setOnClickListener(this);

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
          public void connect(boolean suc) {

          }

          @Override
          public void disconnect() {

          }

          @Override
          public void receiveTemperature(Temperature temperature) {

          }

          @Override
          public void receiveGrowp(Growp growp) {

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
      case R.id.signInByUserId:
        signInByUserId();
        break;
      case R.id.signUpByUserId:
        signUpByUserId();
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

  private void signInByUserId() {
    if (TextUtils.isEmpty(userId.getText().toString().trim())) {
      log.append("Please input userId");
      return;
    }

    LollypopSDK.getInstance().signIn(this, appKey,
        userId.getText().toString());
  }

  private void signUpByUserId() {
    if (TextUtils.isEmpty(userId.getText().toString().trim())) {
      log.append("Please input userId");
      return;
    }

    LollypopSDK.getInstance().createUser(this, appKey,
        userId.getText().toString());
  }

  private void loginSuc() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}
