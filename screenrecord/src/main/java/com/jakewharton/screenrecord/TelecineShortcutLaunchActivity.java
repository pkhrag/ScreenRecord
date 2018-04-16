package com.jakewharton.screenrecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import dagger.android.AndroidInjection;

public final class TelecineShortcutLaunchActivity extends Activity {
  private static final String KEY_ACTION = "launch-action";

  static Intent createQuickTileIntent(Context context) {
    Intent intent = new Intent(context, TelecineShortcutLaunchActivity.class);
    return intent;

  }


  @Override protected void onCreate(Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    super.onCreate(savedInstanceState);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (!CaptureHelper.handleActivityResult(this, requestCode, resultCode, data)) {
      super.onActivityResult(requestCode, resultCode, data);
    }
    finish();
  }

  @Override protected void onStop() {
    if (!isFinishing()) {
      finish();
    }
    super.onStop();
  }
}
