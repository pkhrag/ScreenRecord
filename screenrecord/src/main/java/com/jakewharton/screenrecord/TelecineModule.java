package com.jakewharton.screenrecord;

import android.app.Application;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.jakewharton.screenrecord.BuildConfig;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import java.util.Map;
import javax.inject.Singleton;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

@Module abstract class TelecineModule {
  private static final String PREFERENCES_NAME = "telecine";
  private static final boolean DEFAULT_HIDE_FROM_RECENTS = false;
  private static final boolean DEFAULT_RECORDING_NOTIFICATION = false;
  private static final int DEFAULT_VIDEO_SIZE_PERCENTAGE = 100;


  @Provides @Singleton static ContentResolver provideContentResolver(Application app) {
    return app.getContentResolver();
  }

  @Provides @Singleton static SharedPreferences provideSharedPreferences(Application app) {
    return app.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
  }


  @Provides @Singleton @RecordingNotification
  static BooleanPreference provideRecordingNotificationPreference(SharedPreferences prefs) {
    return new BooleanPreference(prefs, "recording-notification", DEFAULT_RECORDING_NOTIFICATION);
  }

  @Provides @RecordingNotification
  static Boolean provideRecordingNotification(@RecordingNotification BooleanPreference pref) {
    return pref.get();
  }

  @Provides @Singleton @HideFromRecents
  static BooleanPreference provideHideFromRecentsPreference(SharedPreferences prefs) {
    return new BooleanPreference(prefs, "hide-from-recents", DEFAULT_HIDE_FROM_RECENTS);
  }

  @Provides @Singleton @VideoSizePercentage
  static IntPreference provideVideoSizePercentagePreference(SharedPreferences prefs) {
    return new IntPreference(prefs, "video-size", DEFAULT_VIDEO_SIZE_PERCENTAGE);
  }

  @Provides @VideoSizePercentage
  static Integer provideVideoSizePercentage(@VideoSizePercentage IntPreference pref) {
    return pref.get();
  }

  @ContributesAndroidInjector abstract TelecineActivity contributeTelecineActivity();

  @ContributesAndroidInjector
  abstract TelecineShortcutConfigureActivity contributeTelecineShortcutConfigureActivity();

  @ContributesAndroidInjector
  abstract TelecineShortcutLaunchActivity contributeTelecineShortcutLaunchActivity();

  @ContributesAndroidInjector abstract TelecineService contributeTelecineService();

  @ContributesAndroidInjector abstract TelecineTileService contributeTelecineTileService();
}
