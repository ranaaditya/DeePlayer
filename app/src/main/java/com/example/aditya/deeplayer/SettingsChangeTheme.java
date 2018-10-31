package com.example.aditya.deeplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsChangeTheme extends AppCompatActivity {

    private TextView mVioletThemeTextView;
    private TextView mCrimsonBlueThemeTextView;
    private TextView mBluishGreenThemeTextView;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(PlayerFiles.mTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settitngs_change_theme);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsChangeTheme.this);
        mEditor = mPreferences.edit();

        mVioletThemeTextView = (TextView) findViewById(R.id.settings_change_themes_violet_textView);
        mCrimsonBlueThemeTextView = (TextView) findViewById(R.id.settings_change_themes_crimson_blue_textView);
        mBluishGreenThemeTextView = (TextView) findViewById(R.id.settings_change_themes_bluish_green_textView);

        mVioletThemeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerFiles.mTheme = R.style.VioletAppTheme;
                mEditor.putInt("theme", R.style.VioletAppTheme);
                mEditor.commit();
                Toast.makeText(SettingsChangeTheme.this, "Violet theme applied", Toast.LENGTH_SHORT).show();
            }
        });

        mCrimsonBlueThemeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerFiles.mTheme = R.style.CrimsonBlueAppTheme;
                mEditor.putInt("theme", R.style.CrimsonBlueAppTheme);
                mEditor.commit();
                Toast.makeText(SettingsChangeTheme.this, "Crimson Blue theme applied", Toast.LENGTH_SHORT).show();
            }
        });

        mBluishGreenThemeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerFiles.mTheme = R.style.BluishGreenAppTheme;
                mEditor.putInt("theme", R.style.BluishGreenAppTheme);
                mEditor.commit();
                Toast.makeText(SettingsChangeTheme.this, "Bluish Green theme applied", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
