package com.jakewharton.screenrecord;

import android.annotation.TargetApi;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import dagger.android.AndroidInjection;
import javax.inject.Inject;
import timber.log.Timber;

import static android.os.Build.VERSION_CODES.N;

@TargetApi(N) // Only created on N+
public final class TelecineTileService extends TileService {

  @Override public void onCreate() {
    AndroidInjection.inject(this);
    super.onCreate();
  }

  @Override public void onClick() {
    startActivity(TelecineShortcutLaunchActivity.createQuickTileIntent(this));
  }

  @Override public void onStartListening() {
    Timber.i("Quick tile started listening");
    Tile tile = getQsTile();
    tile.setState(Tile.STATE_ACTIVE);
    tile.updateTile();
  }

  @Override public void onStopListening() {
    Timber.i("Quick tile stopped listening");
  }

  @Override public void onTileAdded() {
    Timber.i("Quick tile added");
  }

  @Override public void onTileRemoved() {
    Timber.i("Quick tile removed");
  }
}
