package com.jakewharton.telecine;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import com.google.android.gms.analytics.HitBuilders;
import timber.log.Timber;

import static android.content.Context.MEDIA_PROJECTION_SERVICE;

final class CaptureHelper {
  private static final int CREATE_SCREEN_CAPTURE = 4242;

  private CaptureHelper() {
    throw new AssertionError("No instances.");
  }

  static void fireScreenCaptureIntent(Activity activity) {
    MediaProjectionManager manager =
        (MediaProjectionManager) activity.getSystemService(MEDIA_PROJECTION_SERVICE);
    Intent intent = manager.createScreenCaptureIntent();
    activity.startActivityForResult(intent, CREATE_SCREEN_CAPTURE);
  }

  static boolean handleActivityResult(Activity activity, int requestCode, int resultCode,
      Intent data) {
    if (requestCode != CREATE_SCREEN_CAPTURE) {
      return false;
    }

    if (resultCode == Activity.RESULT_OK) {
      Timber.d("Acquired permission to screen capture. Starting service.");
      activity.startService(TelecineService.newIntent(activity, resultCode, data));
    } else {
      Timber.d("Failed to acquire permission to screen capture.");
    }

    return true;
  }
}
