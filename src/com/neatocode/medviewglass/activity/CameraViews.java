package com.neatocode.medviewglass.activity;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.neatocode.medviewglass.R;

public class CameraViews {

	private static final String LOG_TAG = CameraViews.class.getSimpleName();

	public View mContent;

	public CameraSurfaceView surface;

	public CameraViews(final Activity activity, final int numberOfCameras) {

		mContent = activity.findViewById(R.id.content);
		surface = (CameraSurfaceView) activity.findViewById(R.id.surface);

	}

	public void enterCameraMode() {

		mContent.setPadding(0, 0, 0, 0);
		surface.setCameraId(surface.getCurrentCameraId());

		surface.setVisibility(View.VISIBLE);
	}
	
}
