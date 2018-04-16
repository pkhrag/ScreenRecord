package com.jakewharton.screenrecord;

import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import com.nightlynexus.demomode.BarsBuilder;
import com.nightlynexus.demomode.BatteryBuilder;
import com.nightlynexus.demomode.ClockBuilder;
import com.nightlynexus.demomode.DemoMode;
import com.nightlynexus.demomode.NetworkBuilder;
import com.nightlynexus.demomode.NotificationsBuilder;
import com.nightlynexus.demomode.SystemIconsBuilder;
import com.nightlynexus.demomode.WifiBuilder;
import dagger.android.AndroidInjection;
import javax.inject.Inject;
import javax.inject.Provider;
import timber.log.Timber;

import static android.app.Notification.PRIORITY_MIN;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public final class TelecineService extends Service {
  private static final String EXTRA_RESULT_CODE = "result-code";
  private static final String EXTRA_DATA = "data";
  private static final int NOTIFICATION_ID = 99118822;

  static Intent newIntent(Context context, int resultCode, Intent data) {
    Intent intent = new Intent(context, TelecineService.class);
    intent.putExtra(EXTRA_RESULT_CODE, resultCode);
    intent.putExtra(EXTRA_DATA, data);
    return intent;
  }

  @Inject @VideoSizePercentage Provider<Integer> videoSizePercentageProvider;
  @Inject @RecordingNotification Provider<Boolean> recordingNotificationProvider;
  @Inject ContentResolver contentResolver;

  private boolean running;
  private RecordingSession recordingSession;

  private final RecordingSession.Listener listener = new RecordingSession.Listener() {

    @Override public void onPrepare() {

    }

    @Override public void onStart() {

      if (!recordingNotificationProvider.get()) {
        return; // No running notification was requested.
      }

      Context context = getApplicationContext();
      String title = context.getString(com.jakewharton.screenrecord.R.string.notification_recording_title);
      String subtitle = context.getString(com.jakewharton.screenrecord.R.string.notification_recording_subtitle);
      Notification notification = new Notification.Builder(context) //
          .setContentTitle(title)
          .setContentText(subtitle)
          .setSmallIcon(com.jakewharton.screenrecord.R.drawable.ic_videocam_white_24dp)
          .setColor(ContextCompat.getColor(context, com.jakewharton.screenrecord.R.color.primary_normal))
          .setAutoCancel(true)
          .setPriority(PRIORITY_MIN)
          .build();

      Timber.d("Moving service into the foreground with recording notification.");
      startForeground(NOTIFICATION_ID, notification);
    }

    @Override public void onStop() {

    }

    @Override public void onEnd() {
      Timber.d("Shutting down.");
      stopSelf();
    }
  };

  @Override public void onCreate() {
    AndroidInjection.inject(this);
    super.onCreate();
  }

  @Override public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
    if (running) {
      Timber.d("Already running! Ignoring...");
      return START_NOT_STICKY;
    }
    Timber.d("Starting up!");
    running = true;

    int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
    Intent data = intent.getParcelableExtra(EXTRA_DATA);
    if (resultCode == 0 || data == null) {
      throw new IllegalStateException("Result code or data missing.");
    }

    recordingSession =
        new RecordingSession(this, listener, resultCode, data,
            videoSizePercentageProvider);
    recordingSession.showOverlay();

    return START_NOT_STICKY;
  }

  @Override public void onDestroy() {
    recordingSession.destroy();
    super.onDestroy();
  }

  @Override public IBinder onBind(@NonNull Intent intent) {
    throw new AssertionError("Not supported.");
  }
}
