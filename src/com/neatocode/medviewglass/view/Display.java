package com.neatocode.medviewglass.view;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.neatocode.medviewglass.Constants;
import com.neatocode.medviewglass.R;
import com.neatocode.medviewglass.activity.OverlayActivity;
import com.neatocode.medviewglass.model.Overlay;

public class Display {
			
	private static final float SCREEN_WIDTH_DEGREES = 45f;

	private Float pitch;
	
	private Float initialPitch;

	private Overlay target;

	private TextView text;

	private TextView locationText;
	
	private OffsetIndicatorView indicator;
		
	private OverlayActivity mActivity;
	
	public Display(final OverlayActivity aActivity) {
		mActivity = aActivity;
		

		
		indicator = (OffsetIndicatorView) aActivity.findViewById(R.id.indicator);
		text = (TextView) aActivity.findViewById(R.id.text);
		locationText = (TextView) aActivity.findViewById(R.id.location);
	}

	public void showTarget(final Overlay aTarget) {
		if (null == aTarget) {
			target = null;
			updateDisplay();
			return;
		}

		target = aTarget;
		locationText.setText(aTarget.name);

		updateDisplay();
	}

	public void setOrientation(final float aAzimuth, final float aRoll,
			final float aPitch) {
		if ( null == initialPitch ) {
			initialPitch = aRoll;
		}
		
		pitch = aRoll;
		updateDisplay();
	}
	
	public float normalize(final float deg) {
		float result = deg;
		while(result > 360) {
			result -= 360;
		}
		while(result < -360) {
			result += 360;
		}
		if ( Math.abs(result - 360) < Math.abs(result) ) {
			return result - 360;
		}
		if ( Math.abs(result + 360) < Math.abs(result) ) {
			return result + 360;
		}
		return result;
	}

	public void updateDisplay() {
		if ( null == pitch ) {
			return;
		}
		
		if ( null == target ) {
			return;
		}
		
		float degreesOffStart = initialPitch - pitch;
		float offset = degreesOffStart/SCREEN_WIDTH_DEGREES + 0.5f;
		Log.i(Constants.LOG_TAG, "initialPitch, pitch, offset = "
				+ initialPitch + ", " + pitch + ", " + offset);
		indicator.setIndicatorDrawable(target.indicatorDrawableId);		
		indicator.setIndicatorOffset(offset);

	}

}
