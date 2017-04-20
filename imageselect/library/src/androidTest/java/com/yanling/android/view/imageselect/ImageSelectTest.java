package com.yanling.android.view.imageselect;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * 图片选择界面单元测试
 * @author yanling
 * @date 2017-04-13
 */
@RunWith(AndroidJUnit4.class)
public class ImageSelectTest extends ActivityInstrumentationTestCase2<ImageSelectActivity>{

    private ImageSelectActivity activity;

    public ImageSelectTest(){
        super(ImageSelectActivity.class);
    }

    @Before
    public void setUp() throws Exception{
        super.setUp();
        //初始化Activity
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }

    @Test
    public void typeOperandsAndPerformAddOperation(){
        //执行测试操作
    }

    @Test
    public void testResolverImages(){
        //执行测试
        List<String> photos = activity.resolverImages();
        String haha = "";
    }

    @Test
    public void testShowPreview(){
        activity.showPreview();
        String haha = "";
    }

    @After
    public void tearDown() throws Exception{
        super.tearDown();
    }



}
