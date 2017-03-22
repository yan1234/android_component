package com.yanling.android.dialog.popdialog;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.yanling.android.dialog.popdialog.bottommenu.BottomMenuDialog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;

/**
 * 仿IOS底部弹出选项框单元测试
 * @author yanling
 * @date 2017-03-20
 */
@RunWith(AndroidJUnit4.class)
public class BottomMenuDialogInstrumentationTest
        extends ActivityInstrumentationTestCase2<TestActivity> {

    private TestActivity mActivity;

    public BottomMenuDialogInstrumentationTest(){
        super(TestActivity.class);
    }


    @Before
    public void setUp() throws Exception {
        super.setUp();

        // Injecting the Instrumentation instance is required
        // for your test to run with AndroidJUnitRunner.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }

    @Test
    public void typeOperandsAndPerformAddOperation() {
        //执行测试函数
        //mActivity.onShowBottomMenu();
        //onView(withId(R.id.bottom_menu_lv)).perform(ViewActions.click());

        onView(withId(R.id.activity_test_btn)).perform(ViewActions.click());

        Log.d("haha", "test");





    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}

