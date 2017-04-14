package com.yanling.android.view.imageselect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by yanling on 17-4-13.
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(TestActivity.this, ImageSelectActivity.class);
        startActivity(intent);
    }
}
