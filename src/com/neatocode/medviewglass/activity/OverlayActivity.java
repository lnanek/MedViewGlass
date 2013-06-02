package com.neatocode.medviewglass.activity;

import static com.neatocode.medviewglass.Constants.LOG_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.neatocode.medviewglass.R;
import com.neatocode.medviewglass.model.Overlay;
import com.neatocode.medviewglass.view.Display;

// TODO for Android, show the camera under the overlay

// for glass, keep black

public class OverlayActivity extends Activity implements
		SensorEventListener {

	// TODO use wake lock to turn on screen when run

	private SensorManager mSensorManager;

	private Sensor mOrientation;

	private Display mDisplay;

	private List<Overlay> mOverlays;

	private int mTargetIndex;
	
	protected void setContentView() {
		setContentView(R.layout.activity_overlay);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// TODO supposed to be more accurate to compose compass and
		// accelerometer yourself
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView();
		mDisplay = new Display(this);
		
		mOverlays = Arrays.asList(Overlay.OVERLAYS);
		mDisplay.showTarget(mOverlays.get(mTargetIndex));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(LOG_TAG, "onTouchEvent, event = " + event);
		return super.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.i(LOG_TAG, "dispatchTouchEvent, event = " + ev);

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			nextTarget();
			return true;
		}

		return super.dispatchTouchEvent(ev);
	}

	private void nextTarget() {
		Log.i(LOG_TAG, "nextTarget");

		mTargetIndex++;
		if (mTargetIndex >= mOverlays.size()) {
			mTargetIndex = 0;
		}
		mDisplay.showTarget(mOverlays.get(mTargetIndex));
	}

	private void previousTarget() {
		Log.i(LOG_TAG, "previousTarget");

		mTargetIndex--;
		if (mTargetIndex < 0) {
			mTargetIndex = mOverlays.size() - 1;
		}
		mDisplay.showTarget(mOverlays.get(mTargetIndex));
	}

	@Override
	public boolean dispatchKeyEvent(final KeyEvent event) {
		Log.i(LOG_TAG, "dispatchKeyEvent, event = " + event);

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
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (event.isShiftPressed()) {
				previousTarget();
			} else {
				nextTarget();
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			previousTarget();
			return true;

		default:
			return super.dispatchKeyEvent(event);
		}
	}

	@Override
	public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
		// Do nothing.
	}

	@Override
	protected void onResume() {
		// Log.i(LOG_TAG, "onResume");

		super.onResume();
		mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		// Log.i(LOG_TAG, "onPause");
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(final SensorEvent event) {
		// Log.i(LOG_TAG, "onSensorChanged");

		float azimuth_angle = event.values[0];
		float pitch_angle = event.values[1];
		float roll_angle = event.values[2];
		mDisplay.setOrientation(azimuth_angle, pitch_angle, roll_angle);
	}

	public static double microDegreesToDegrees(int microDegrees) {
		return microDegrees / 1E6;
	}

}
