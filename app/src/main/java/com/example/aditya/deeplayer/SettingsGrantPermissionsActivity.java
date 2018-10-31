package com.example.aditya.deeplayer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsGrantPermissionsActivity extends AppCompatActivity{

    private final static int PERMISSION_TO_READ_EXTERNAL_STORAGE = 1001;
    private final static int PERMISSION_TO_WRITE_EXTERNAL_STORAGE = 1002;
    private final static int PERMISSION_TO_READ_CONTACTS = 1003;

    private TextView mReadStorageTextView;
    private TextView mWriteStorageTextView;
    private TextView mReadContactsTextView;
    private TextView mWriteSettingsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        setTheme(PlayerFiles.mTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_grant_permissions);

        mReadStorageTextView = (TextView) findViewById(R.id.settings_grant_permissions_read_storage_textView);
        mWriteStorageTextView = (TextView) findViewById(R.id.settings_grant_permissions_write_storage_textView);
        mReadContactsTextView = (TextView) findViewById(R.id.settings_grant_permissions_read_contacts_textView);
        mWriteSettingsTextView = (TextView) findViewById(R.id.settings_grant_permissions_write_settings_textView);

        mReadStorageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SettingsGrantPermissionsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SettingsGrantPermissionsActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(SettingsGrantPermissionsActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_TO_READ_EXTERNAL_STORAGE);
                }
            }
        });

        mWriteStorageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SettingsGrantPermissionsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SettingsGrantPermissionsActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(SettingsGrantPermissionsActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_TO_WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        mReadContactsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SettingsGrantPermissionsActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SettingsGrantPermissionsActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(SettingsGrantPermissionsActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_TO_READ_CONTACTS);
                }
            }
        });

        mWriteSettingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean settingsCanWrite = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    settingsCanWrite = Settings.System.canWrite(getApplicationContext());
                    if(!settingsCanWrite) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }else {
                        Toast.makeText(SettingsGrantPermissionsActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //User allowed permission when downloaded the app
                    Toast.makeText(SettingsGrantPermissionsActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_TO_READ_EXTERNAL_STORAGE:
            case PERMISSION_TO_WRITE_EXTERNAL_STORAGE:
            case PERMISSION_TO_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SettingsGrantPermissionsActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsGrantPermissionsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
