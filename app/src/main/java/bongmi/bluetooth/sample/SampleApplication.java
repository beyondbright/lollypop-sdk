package bongmi.bluetooth.sample;

import android.app.Application;
import com.bm.android.thermometer.sdk.LollypopSDK;

/**
 * Copyright (c) 2016, Bongmi
 * All rights reserved
 * Author: wangjunjie@bongmi.com
 */

public class SampleApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    LollypopSDK.getInstance().init(this);
  }
}
