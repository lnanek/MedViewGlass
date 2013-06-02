package com.neatocode.medviewglass.activity;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class CameraSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String LOG_TAG = CameraSurfaceView.class.getSimpleName();

	private SurfaceHolder holder;

	private Camera camera;

	private Integer currentCameraId;

	boolean initilized;
	
	boolean adjustedSize;

	public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CameraSurfaceView(Context context) {
		super(context);
		init(context);
	}
	
	public int getCurrentCameraId() {
		return currentCameraId;
	}

	public void init(Context context) {
		// Initiate the Surface Holder properly
		this.holder = this.getHolder();
		this.holder.addCallback(this);
		this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	public void setCameraId(final Integer newCameraId) {
		if ( null == newCameraId ) {
			return;
		}
		
		currentCameraId = newCameraId;
		adjustedSize = false;

		if (initilized) {
			openCamera();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
		
		logSizeAndRatio(width, height, "surfaceChanged ");
		
		initilized = true;
		openCamera();
	}
	
	private void logSizeAndRatio(int w, int h, String prefix) {
		float ratio = (float) w / h;
		Log.d("CameraSurfaceView", prefix + " width = " + w + ", height = " + h + ", ratio = " + ratio);
	}
	
	public void takePicture(PictureCallback raw) {
		if ( null != camera ) {
			camera.takePicture(null, null, raw);
		}
	}

	public void openCamera() {
		try {

			closeCamera();
			
			camera = Camera.open(currentCameraId);

			camera.setPreviewDisplay(this.holder);
			
			final Camera.Parameters parameters = camera.getParameters();
			final List<Size> sizes = camera.getParameters().getSupportedPreviewSizes();
			int selectedSizeIndex = 0;
			int selectedMp = 0;
			int sizeIndex = 0;
			for ( final Size size : sizes ) {
				Log.d("CameraSurfaceView", "supported size: " + size.width + ", " + size.height);

				logSizeAndRatio(size.width, size.height, "supported size ");
				
				final int thisMp = size.width * size.height;
				if ( thisMp > selectedMp ) {
					selectedSizeIndex = sizeIndex;
					selectedMp = thisMp;
				}
				sizeIndex++;
			}
			final Size size = sizes.get(selectedSizeIndex);

			logSizeAndRatio(size.width, size.height, "chosen size ");
	        int usedPreviewWidth = size.width;
	        int usedPreviewHeight = size.height;
			parameters.setPreviewSize(usedPreviewWidth, usedPreviewHeight);

			// Fix sideways preview on some phones
	        final Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	        if ( display.getRotation() == Surface.ROTATION_0 ) {
				camera.setDisplayOrientation(90);
			} else if ( display.getRotation() == Surface.ROTATION_90 ) {
				// Do nothing.
			} else if ( display.getRotation() == Surface.ROTATION_180 ) {
			// Surface.ROTATION_270
			} else {
				camera.setDisplayOrientation(180);
			}			        	
			
			final float dataRatio = ((float) usedPreviewWidth) / usedPreviewHeight;
			final float viewSizeRatio =  ((float) getWidth()) / getHeight();
			
			logSizeAndRatio(getWidth(), getHeight(), "view size ");
	
			if ( !adjustedSize ) {
				
				// Data is too wide
				if ( viewSizeRatio > dataRatio ) {
					final float heightRatio = ((float) getHeight()) / usedPreviewHeight;
					final float newViewWidth = heightRatio * usedPreviewWidth;
					final float totalPadding = Math.abs(getWidth() - newViewWidth);
					final int paddingLeft = (int) (totalPadding/2);
					final int paddingRight = (int) (totalPadding - paddingLeft);
					Log.d(LOG_TAG, "view is wider than data, setting padding: " + paddingLeft + ", " + "0" + ", " + paddingRight + ", " + "0");
					((View) getParent()).setPadding(paddingLeft, 0, paddingRight, 0);
					((View) getParent()).requestLayout();
				
				// Data is too thin
				} else if ( viewSizeRatio < dataRatio ) {
					final float widthRatio = ((float) getWidth()) / usedPreviewWidth;
					final float newViewHeight = widthRatio * usedPreviewHeight;
					final float totalPadding = Math.abs(getHeight() - newViewHeight);
					final int paddingTop = (int) (totalPadding/2);
					final int paddingBottom = (int) (totalPadding - paddingTop);
					Log.d(LOG_TAG, "view is narrower than data, setting padding: " + "0" + ", " + paddingTop + ", " + "0" + ", " + paddingBottom);
					
					((View) getParent()).setPadding(0, paddingTop, 0, paddingBottom);	
					((View) getParent()).requestLayout();
					
				// Equal ratio
				} else {
					((View) getParent()).setPadding(0,0,0,0);
					((View) getParent()).requestLayout();
	
				}
				
				logSizeAndRatio(getWidth(), getHeight(), "view size ");
				
				adjustedSize = true;
			}
			
			camera.setParameters(parameters);			
			camera.startPreview();

		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		closeCamera();
	}

	public void closeCamera() {
		if ( null != camera ) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

}