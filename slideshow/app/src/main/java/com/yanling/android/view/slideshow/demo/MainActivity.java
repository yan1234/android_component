package com.yanling.android.view.slideshow.demo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.yanling.android.view.slideshow.SlideShowFragment;

public class MainActivity extends Activity {

    private RelativeLayout layout;

    private SlideShowFragment fragment;

    private int[] resources = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (RelativeLayout)findViewById(R.id.layout);

        fragment = new SlideShowFragment();
        fragment.setTipDrawable(getResources().getDrawable(R.drawable.selected), getResources().getDrawable(R.drawable.unselected));
        fragment.setImageSources(resources);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.layout, fragment);
        transaction.commit();
    }
}
