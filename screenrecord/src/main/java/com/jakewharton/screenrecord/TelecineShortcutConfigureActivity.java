package com.jakewharton.screenrecord;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import dagger.android.AndroidInjection;
import javax.inject.Inject;

import static android.content.Intent.ShortcutIconResource;

public final class TelecineShortcutConfigureActivity extends Activity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    super.onCreate(savedInstanceState);

    Intent launchIntent = new Intent(this, TelecineShortcutLaunchActivity.class);
    ShortcutIconResource icon = ShortcutIconResource.fromContext(this, com.jakewharton.screenrecord.R.drawable.ic_launcher);

    Intent intent = new Intent();
    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(com.jakewharton.screenrecord.R.string.shortcut_name));
    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);

    setResult(RESULT_OK, intent);
    finish();
  }
}
