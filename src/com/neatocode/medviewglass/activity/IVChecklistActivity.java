
package com.neatocode.medviewglass.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.neatocode.medviewglass.Constants;
import com.neatocode.medviewglass.R;

public class IVChecklistActivity extends FragmentActivity {
	
	public static final String[] TARGET_NAMES = new String[] {
			"Check order from doctor", 
			"Identify patient" ,
			"Explain procedure to patient" 	,
			"Wash Hands before procedure" 	,
			"Bring all materials to patient bedside" ,
			"Perform procedure" 			
	};
	
    private static final int NUM_PAGES = TARGET_NAMES.length;

    private ViewPager mPager;

    private ScreenSlidePagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		

        setContentView(R.layout.activity_screen_slide);
        
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });
        
        mPager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				select();
			}
		});
        
    }

    public void next() {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }
    
    public void prev() {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }
    
    public void select() {
		Log.i(Constants.LOG_TAG, "select");
    	((IVChecklistPageFragment)mPagerAdapter.getItem(mPager.getCurrentItem())).setCheckbox();
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(Constants.LOG_TAG, "onTouchEvent, event = " + event);
		return super.onTouchEvent(event);
	}

	@Override
	public boolean dispatchKeyEvent(final KeyEvent event) {
		Log.i(Constants.LOG_TAG, "dispatchKeyEvent, event = " + event);

		final int action = event.getAction();
		if (action != KeyEvent.ACTION_DOWN) {
			return false;
		}

		final int keyCode = event.getKeyCode();
		switch (keyCode) {
		// Back button on standard Android, swipe down on Google Glass
		case KeyEvent.KEYCODE_BACK:
			finish();
			return true;

			// Left and right swipe through the cameras on Google Glass.
			// On phone, volume keys move through cameras
		case KeyEvent.KEYCODE_TAB:
		case KeyEvent.KEYCODE_VOLUME_UP:
			if ( event.isShiftPressed() ) {
				prev();
			} else {
				next();
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			prev();
			return true;

		case KeyEvent.KEYCODE_DPAD_CENTER:
			select();
			return true;

		default:
			return super.dispatchKeyEvent(event);
		}
	}

    /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return IVChecklistPageFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
