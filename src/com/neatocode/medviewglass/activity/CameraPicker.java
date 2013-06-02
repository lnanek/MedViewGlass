package com.neatocode.medviewglass.activity;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

public class CameraPicker {
	
	private Integer pickedCameraId;
	
	private boolean isFrontFacingCamera;
	
	private int numberOfCameras;

	public CameraPicker() {
		
		// Find the total number of cameras available
		numberOfCameras = Camera.getNumberOfCameras();
		
		if ( 0 == numberOfCameras ) {
			return;
		}

		// Find the ID of the default camera
		for (int i = 0; i < numberOfCameras; i++) {	
			
			final CameraInfo cameraInfo = new CameraInfo();			
			Camera.getCameraInfo(i, cameraInfo);

			// Take the first back facing camera found.
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
				pickCamera(i, cameraInfo);
				break;
			}
			
			// Or the first camera if no back facing camera found.
			if ( 0 == i ) {
				pickCamera(i, cameraInfo);
			}
		}

	}
	
	private void pickCamera(final int id, final CameraInfo info) {
		isFrontFacingCamera = CameraInfo.CAMERA_FACING_FRONT == info.facing;
		pickedCameraId = id;
	}
	
	private void pickCamera(final int id) {
		final CameraInfo cameraInfo = new CameraInfo();				
		Camera.getCameraInfo(id, cameraInfo);
		pickCamera(id, cameraInfo);
	}
	
	public boolean isFrontFacingCamera() {
		return isFrontFacingCamera;
	}
	
	public boolean pickedCamera() {
		return null != pickedCameraId;
	}
	
	public Integer getPickedCameraId() {
		return pickedCameraId;
	}

	public int getNumberOfCameras() {
		return numberOfCameras;
	}

	public Integer pickNextCamera() {
		
		if ( null == pickedCameraId ) {
			return null;
		}
		
		// Find the ID of the next camera				
		int nextCameraId = pickedCameraId + 1;
		if ( nextCameraId >= numberOfCameras ) {
			nextCameraId = 0;
		}
		pickCamera(nextCameraId);
		return nextCameraId;
	}

}
