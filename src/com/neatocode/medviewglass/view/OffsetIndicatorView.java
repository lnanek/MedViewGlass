package com.neatocode.medviewglass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.neatocode.medviewglass.R;

public class OffsetIndicatorView extends View {
		
	private Float mOffsetPercent;
	
	private Drawable mIndicator;
	
	public OffsetIndicatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public OffsetIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public OffsetIndicatorView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(final Context aContext) {
	}
	
	public void setIndicatorOffset(final Float aOffsetPercent) {
		mOffsetPercent = aOffsetPercent;
		if ( null != mOffsetPercent && mOffsetPercent < 0f ) {
			mOffsetPercent = 0f;
		}
		if ( null != mOffsetPercent && mOffsetPercent > 1f ) {
			mOffsetPercent = 1f;
		}
		if ( null != mOffsetPercent ) {
			mOffsetPercent = 1f - mOffsetPercent;
		}
		invalidate();
	}
	
	public void setIndicatorDrawable(final Integer drawableId) {
		if ( null == drawableId || 0 == drawableId) {
			return;
		}
		mIndicator = getContext().getResources().getDrawable(drawableId);		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if ( null == mOffsetPercent ) {
			return;
		}

		// Find ratio to scale the overlay to the display width.
		final int viewWidth = getWidth();
		final int indicatorWidth = mIndicator.getIntrinsicWidth();
		final float fullScreenWidthRato = viewWidth / (float) indicatorWidth;

		final int indicatorHeight = mIndicator.getIntrinsicHeight();
		final int indicatorScaledHeight = (int) (indicatorHeight * fullScreenWidthRato);
		final int viewHeight = getHeight();
		
		final int offset = (int) (indicatorScaledHeight * mOffsetPercent);

		mIndicator.setBounds(0, 0 - offset, 
				viewWidth, indicatorScaledHeight - offset);
		mIndicator.draw(canvas);
		
	}
	
}
