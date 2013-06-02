package com.neatocode.medviewglass.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.neatocode.medviewglass.R;

public class OverlayCameraActivity extends OverlayActivity {
	
	private CameraViews mViews;

	private CameraPicker mCameraPicker;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_overlay_with_camera);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCameraPicker = new CameraPicker();

		mViews = new CameraViews(this, mCameraPicker.getNumberOfCameras());

		mViews.surface.setCameraId(mCameraPicker.getPickedCameraId());			

	}

	public void onSwitchCameraButtonClicked() {
		
		mViews.surface.setCameraId(mCameraPicker.pickNextCamera());
	}
	
}
