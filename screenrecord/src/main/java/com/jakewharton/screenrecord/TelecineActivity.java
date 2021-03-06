package com.jakewharton.screenrecord;

import android.app.ActivityManager;
import android.app.ActivityManager.TaskDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import dagger.android.AndroidInjection;
import javax.inject.Inject;
import timber.log.Timber;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public final class TelecineActivity extends AppCompatActivity {
  @BindView(com.jakewharton.screenrecord.R.id.spinner_video_size_percentage) Spinner videoSizePercentageView;
  @BindView(com.jakewharton.screenrecord.R.id.switch_hide_from_recents) Switch hideFromRecentsView;
  @BindView(com.jakewharton.screenrecord.R.id.switch_recording_notification) Switch recordingNotificationView;
  @BindView(com.jakewharton.screenrecord.R.id.launch) View launchView;

  @BindString(com.jakewharton.screenrecord.R.string.app_name) String appName;
  @BindColor(com.jakewharton.screenrecord.R.color.primary_normal) int primaryNormal;

  @Inject @VideoSizePercentage IntPreference videoSizePreference;
  @Inject @HideFromRecents BooleanPreference hideFromRecentsPreference;
  @Inject @RecordingNotification BooleanPreference recordingNotificationPreference;


  private VideoSizePercentageAdapter videoSizePercentageAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    super.onCreate(savedInstanceState);

    if ("true".equals(getIntent().getStringExtra("crash"))) {
      throw new RuntimeException("Crash! Bang! Pow! This is only a test...");
    }

    setContentView(com.jakewharton.screenrecord.R.layout.activity_main);
    ButterKnife.bind(this);

    CheatSheet.setup(launchView);

    setTaskDescription(new TaskDescription(appName, rasterizeTaskIcon(), primaryNormal));

    videoSizePercentageAdapter = new VideoSizePercentageAdapter(this);

    videoSizePercentageView.setAdapter(videoSizePercentageAdapter);
    videoSizePercentageView.setSelection(
        VideoSizePercentageAdapter.getSelectedPosition(videoSizePreference.get()));

    hideFromRecentsView.setChecked(hideFromRecentsPreference.get());
    recordingNotificationView.setChecked(recordingNotificationPreference.get());
  }

  @NonNull private Bitmap rasterizeTaskIcon() {
    Drawable drawable = getResources().getDrawable(com.jakewharton.screenrecord.R.drawable.ic_videocam_white_24dp, getTheme());

    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    int size = am.getLauncherLargeIconSize();
    Bitmap icon = Bitmap.createBitmap(size, size, ARGB_8888);



    Canvas canvas = new Canvas(icon);
    drawable.setBounds(0, 0, size, size);
    drawable.draw(canvas);

    return icon;
  }

  @OnClick(com.jakewharton.screenrecord.R.id.launch) void onLaunchClicked() {
    Timber.d("Attempting to acquire permission to screen capture.");
    CaptureHelper.fireScreenCaptureIntent(this);
  }

  @OnItemSelected(com.jakewharton.screenrecord.R.id.spinner_video_size_percentage) void onVideoSizePercentageSelected(
      int position) {
    int newValue = videoSizePercentageAdapter.getItem(position);
    int oldValue = videoSizePreference.get();
    if (newValue != oldValue) {
      Timber.d("Video size percentage changing to %s%%", newValue);
      videoSizePreference.set(newValue);
    }
  }

  @OnCheckedChanged(com.jakewharton.screenrecord.R.id.switch_hide_from_recents) void onHideFromRecentsChanged() {
    boolean newValue = hideFromRecentsView.isChecked();
    boolean oldValue = hideFromRecentsPreference.get();
    if (newValue != oldValue) {
      Timber.d("Hide from recents preference changing to %s", newValue);
      hideFromRecentsPreference.set(newValue);
    }
  }

  @OnCheckedChanged(com.jakewharton.screenrecord.R.id.switch_recording_notification) void onRecordingNotificationChanged() {
    boolean newValue = recordingNotificationView.isChecked();
    boolean oldValue = recordingNotificationPreference.get();
    if (newValue != oldValue) {
      Timber.d("Recording notification preference changing to %s", newValue);
      recordingNotificationPreference.set(newValue);
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (!CaptureHelper.handleActivityResult(this, requestCode, resultCode, data)) {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override protected void onStop() {
    super.onStop();
    if (hideFromRecentsPreference.get() && !isChangingConfigurations()) {
      Timber.d("Removing task because hide from recents preference was enabled.");
      finishAndRemoveTask();
    }
  }
}
