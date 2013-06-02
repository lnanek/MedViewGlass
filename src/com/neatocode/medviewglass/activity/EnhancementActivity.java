package com.neatocode.medviewglass.activity;

import static com.neatocode.medviewglass.Constants.LOG_TAG;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.neatocode.medviewglass.Constants;
import com.neatocode.medviewglass.R;

// TODO only filter skin colored pixels?
// http://www.shervinemami.info/blobs.html

// TODO improve performance: start hsv? do sat. without converting
// colorspace?

// TODO just use min and max from previous frame?

public class EnhancementActivity extends Activity implements
		CvCameraViewListener2, GestureDetector.OnGestureListener {

	private static final String TAG = "OCVSample::Activity";

	static {
		if (!OpenCVLoader.initDebug()) {
			Log.i(TAG, "Failed to load OpenCV");
		}
	}

	private CameraBridgeViewBase mOpenCvCameraView;

	private Size mSizeRgba;

	private Mat mRgba;
	private Mat mGray;
	private Mat mIntermediateMat;
	private Mat mRgbaInnerWindow;
	private Mat mGrayInnerWindow;
	private Mat mZoomWindow;
	private Mat mZoomCorner;
	
	private Double maxSat = null;
	private Double minSat = null;

	public static final int VIEW_MODE_ZOOM = 0;
	public static final int VIEW_MODE_COLOR_STRETCH_CENTER = 1;
	public static final int VIEW_MODE_UNENHANCED = 2;
	public static int viewMode = VIEW_MODE_ZOOM;
	private int viewCount = 3;
	private String[] viewTitles = new String[] {
			"Zoom", "Color Stretch", "Unenhanced" };

	private GestureDetector mGestureDetector;

	public EnhancementActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");

		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_enhancement);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.image_manipulations_activity_surface_view);
		//mOpenCvCameraView.enableFpsMeter();
		mOpenCvCameraView.setCvCameraViewListener(this);

		mGestureDetector = new GestureDetector(this, this);

		viewMode = VIEW_MODE_ZOOM;
		displayViewModeToUser();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		mOpenCvCameraView.enableView();
		// OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
		// mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		mGray = new Mat();
		mRgba = new Mat();
		mIntermediateMat = new Mat();
	}

	private void CreateAuxiliaryMats() {
		if (mRgba.empty())
			return;

		mSizeRgba = mRgba.size();

		int rows = (int) mSizeRgba.height;
		int cols = (int) mSizeRgba.width;

		int left = cols / 8;
		int top = rows / 8;

		int width = cols * 3 / 4;
		int height = rows * 3 / 4;

		if (mRgbaInnerWindow == null)
			mRgbaInnerWindow = mRgba.submat(top, top + height, left, left
					+ width);

		if (mGrayInnerWindow == null && !mGray.empty())
			mGrayInnerWindow = mGray.submat(top, top + height, left, left
					+ width);

		if (mZoomCorner == null)
			mZoomCorner = mRgba.submat(0, rows / 2 - rows / 10, 0, cols / 2
					- cols / 10);

		if (mZoomWindow == null)
			mZoomWindow = mRgba.submat(rows / 2 - 9 * rows / 100, rows / 2 + 9
					* rows / 100, cols / 2 - 9 * cols / 100, cols / 2 + 9
					* cols / 100);
	}

	public void onCameraViewStopped() {
		// Explicitly deallocate Mats
		if (mZoomWindow != null)
			mZoomWindow.release();
		if (mZoomCorner != null)
			mZoomCorner.release();
		if (mGrayInnerWindow != null)
			mGrayInnerWindow.release();
		if (mRgbaInnerWindow != null)
			mRgbaInnerWindow.release();
		if (mRgba != null)
			mRgba.release();
		if (mGray != null)
			mGray.release();
		if (mIntermediateMat != null)
			mIntermediateMat.release();

		mRgba = null;
		mGray = null;
		mIntermediateMat = null;
		mRgbaInnerWindow = null;
		mGrayInnerWindow = null;
		mZoomCorner = null;
		mZoomWindow = null;
	}

	private Mat stretchSaturation(Mat fv) {

		int size = (int) fv.total() * fv.channels();
		byte[] buff = new byte[size];
		fv.get(0, 0, buff);

		Integer maxSat = null;
		Integer minSat = null;
		for (int i = 1; i < size; i += 3) {
			int saturation = (int) buff[i] & 0xff;

			if (null == maxSat) {
				maxSat = saturation;
			} else {
				maxSat = maxSat > saturation ? maxSat : saturation;
			}
			if (null == minSat) {
				minSat = saturation;
			} else {
				minSat = minSat < saturation ? minSat : saturation;
			}

		}

		final int satRange = (maxSat - minSat);
		final int satScale = (255 / satRange);

		for (int i = 1; i < size; i += 3) {
			int saturation = (int) buff[i] & 0xff;
			int newSat = (saturation - minSat) * satScale;
			buff[i] = (byte) (newSat);

		}

		fv.put(0, 0, buff);
		return fv;
	}
	
	private void flipVertical(CvCameraViewFrame inputFrame) {
		Mat temp = inputFrame.rgba();
		Core.flip(temp, mRgba, -1);
		temp.release();		
	}
	
	private void stretchCenterSaturation() {
		
		// Convert to 3 channel hue, saturation, value
		Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_RGB2HSV, 3);

		double satRange = null == minSat ? 0 : (maxSat - minSat);
		double satScale = null == minSat ? 0 : (255 / satRange);
		
		int colCount = mRgba.cols();
		int quarterColCount = colCount / 4;
		int threeQuarterColCount = colCount - quarterColCount;
		int rowCount = mRgba.rows();
		int quarterRowCount = rowCount / 4;
		int threeQuarterRowCount = rowCount - quarterRowCount;
		
		// For every column in center area.
		Double newMaxSat = null;
		Double newMinSat = null;
		for (int c = quarterColCount; c < threeQuarterColCount; c++) {			
			// For every row in center area.
			for (int r = quarterRowCount; r < threeQuarterRowCount; r++) {				
				// For every pixel.
				// TODO get all pixels at once into one big array more efficient?
				double[] pixel = mRgba.get(r, c);
				// Get the saturation of the pixel.
				double saturation = pixel[1];

				// Keep track of min and max.
				// TODO histogram functions more efficient?
				if (!Double.isNaN(saturation)
						&& !Double.isInfinite(saturation)) {
					if (null == newMaxSat) {
						newMaxSat = saturation;
					} else {
						newMaxSat = Math.max(newMaxSat, saturation);
					}
					if (null == newMinSat) {
						newMinSat = saturation;
					} else {
						newMinSat = Math.min(newMinSat, saturation);
					}
				}

				// Stretch saturation across entire range from min to max.
				// TODO matrix subtract, then matrix scale more efficient?
				if ( null != minSat ) {
					pixel[1] = (saturation - newMinSat) * satScale;
					mRgba.put(r, c, pixel);
				}
			}
		}

		maxSat = newMaxSat;
		minSat = newMinSat;

		// Convert back to 4 channel red, green, blue, alpha for display
		Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_HSV2RGB, 4);

		// Draw rectangle around filtered area.
		Core.rectangle(mRgba, 
				new Point(colCount / 4, rowCount / 4), 
				new Point((colCount - (colCount/4)), (rowCount - (rowCount/4))), 
				new Scalar(255, 0, 0, 255), 2);
		
	}
	
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

		rotateToMatchDisplay(inputFrame);

		switch (EnhancementActivity.viewMode) {
			case EnhancementActivity.VIEW_MODE_ZOOM:
				zoomCenter();
				break;
			case EnhancementActivity.VIEW_MODE_COLOR_STRETCH_CENTER:
				stretchCenterSaturation();
				break;

		}

		return mRgba;
	}

	private void rotateToMatchDisplay(CvCameraViewFrame inputFrame) {
		final Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();
		if (display.getRotation() == Surface.ROTATION_0) {
			// TODO rotate 90 deg. to fix portrait
			mRgba = inputFrame.rgba();
		} else if (display.getRotation() == Surface.ROTATION_90) {
			mRgba = inputFrame.rgba();
		} else if (display.getRotation() == Surface.ROTATION_180) {
			// TODO rotate 90 deg. to fix portrait
			mRgba = inputFrame.rgba();
			// Surface.ROTATION_270
		} else {
			// Fix upside down portrait on Kindle Fire.
			flipVertical(inputFrame);
		}
	}

	private void zoomCenter() {
		if ((mZoomCorner == null) || (mZoomWindow == null)
				|| (mRgba.cols() != mSizeRgba.width)
				|| (mRgba.height() != mSizeRgba.height))
			CreateAuxiliaryMats();
		Imgproc.resize(mZoomWindow, mZoomCorner, mZoomCorner.size());

		Size wsize = mZoomWindow.size();
		Core.rectangle(mZoomWindow, new Point(1, 1), new Point(
				wsize.width - 2, wsize.height - 2), new Scalar(255, 0, 0,
				255), 2);
	}

	private void displayViewModeToUser() {
		Toast.makeText(this, viewTitles[viewMode], Toast.LENGTH_SHORT).show();
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
				previousViewMode();
				displayViewModeToUser();
			} else {
				nextViewMode();
				displayViewModeToUser();
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			previousViewMode();
			displayViewModeToUser();
			return true;

		default:
			return super.dispatchKeyEvent(event);
		}
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// Log.d(TAG, "onFling, x: " + Float.toString(velocityX) + ", y: " +
		// Float.toString(velocityY));

		if (velocityX < 0.0f) // swipe forward
		{
			previousViewMode();
		} else if (velocityX > 0.0f) // swipe backward
		{
			nextViewMode();
		}

		displayViewModeToUser();

		return false;
	}

	private void nextViewMode() {
		viewMode += 1;
		if (viewMode > viewCount - 1)
			viewMode = 0;
	}

	private void previousViewMode() {
		viewMode -= 1;
		if (viewMode < 0)
			viewMode = viewCount - 1;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}