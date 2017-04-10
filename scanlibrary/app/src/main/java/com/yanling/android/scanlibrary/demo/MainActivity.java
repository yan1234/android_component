package com.yanling.android.scanlibrary.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.yanling.android.scanlibrary.ScanCameraActivity;

/**
 * Created by yanling on 17-4-10.
 */
public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(MainActivity.this, ScanCameraActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            if (requestCode == ScanCameraActivity.SCAN_OK){
                String result = data.getStringExtra(ScanCameraActivity.SCAN_CODE);
                Log.d("MainActivity", ">>>"+result);
            }
        }
    }
}
