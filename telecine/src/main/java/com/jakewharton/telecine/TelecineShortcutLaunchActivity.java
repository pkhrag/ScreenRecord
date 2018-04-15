package com.jakewharton.telecine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.analytics.HitBuilders;
import dagger.android.AndroidInjection;
import javax.inject.Inject;

public final class TelecineShortcutLaunchActivity extends Activity {
  private static final String KEY_ACTION = "launch-action";

  static Intent createQuickTileIntent(Context context) {
    Intent intent = new Intent(context, TelecineShortcutLaunchActivity.class);
    intent.putExtra(KEY_ACTION, Analytics.ACTION_QUICK_TILE_LAUNCHED);
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
