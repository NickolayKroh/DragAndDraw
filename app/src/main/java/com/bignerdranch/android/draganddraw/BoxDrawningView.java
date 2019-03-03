package com.bignerdranch.android.draganddraw;

import android.view.*;
import android.content.*;
import android.util.*;
import android.graphics.*;
import java.util.*;
import android.os.*;

public class BoxDrawningView extends View {
	private Box mCurrentBox;
	private Paint mBoxPaint;
	private Paint mBackgroundPaint;
	private DrawPath mPath;
	private Matrix mMatrix;
	private PointF vectorO;
	private PointF rotationCenter = new PointF(0,0);
	private PointF mOffset = new PointF(0,0);
	private float mAngle = 0;
	private float mScale = 1;
	
	public BoxDrawningView( Context context ) {
		this( context, null );
	}
	
	public BoxDrawningView( Context context, AttributeSet attrs ) {
		super( context, attrs );
		
		mBoxPaint = new Paint();
		mBoxPaint.setStrokeWidth(3);
		mBoxPaint.setStyle(Paint.Style.STROKE);
		mBoxPaint.setAntiAlias(true);
		
		mBackgroundPaint = new Paint();
		mBackgroundPaint.setColor(0xfff8efe0);
		
		mPath = new DrawPath();
		mMatrix = new Matrix();
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		PointF current = new PointF( event.getX(0), event.getY(0) );
		
		switch( event.getActionMasked() ) {
			case MotionEvent.ACTION_DOWN:
				addNewBox(current);
				break;
			case MotionEvent.ACTION_MOVE:
				if( event.getPointerCount() == 2 )
					transformPath(event);
				else if( mCurrentBox != null )
					invalidateCurrentBox(current);
				break;
			case MotionEvent.ACTION_UP:
				stopTransformPath();
				break;
			case MotionEvent.ACTION_CANCEL:
				removeCurrentBox();
				stopTransformPath();
				Log.i("","cancel");
				return true;
			case MotionEvent.ACTION_POINTER_DOWN:
				removeCurrentBox();
				vectorO = new PointF(event.getX(0) - event.getX(1),
									 event.getY(0) - event.getY(1));
				rotationCenter = new PointF((event.getX(0) + event.getX(1)) / 2,
											(event.getY(0) + event.getY(1)) / 2);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				stopTransformPath();
				break;
		}
		
		return true;
	}

	private void transformPath(MotionEvent event) {
		PointF vectorC = new PointF(event.getX(0) - event.getX(1),
									event.getY(0) - event.getY(1));
		PointF center = new PointF((event.getX(0) + event.getX(1)) / 2,
								   (event.getY(0) + event.getY(1)) / 2);
		mOffset = new PointF(center.x - rotationCenter.x,
							 center.y - rotationCenter.y);
		
		float scalar = vectorO.x * vectorC.x + vectorO.y * vectorC.y;
		float modO = vectorO.x * vectorO.x + vectorO.y * vectorO.y;
		float modC = vectorC.x * vectorC.x + vectorC.y * vectorC.y;
		
		mScale = (float) Math.sqrt( modC / modO );
		
		float coef = vectorO.x * vectorC.y - vectorO.y * vectorC.x;
		coef = coef / Math.abs(coef);
		mAngle = coef * (float) Math.toDegrees(Math.acos(scalar / Math.sqrt(modO * modC)));
		
		if( Float.isNaN(mAngle) )
			mAngle = 0;
		
		vectorO = vectorC;
		rotationCenter = center;
		
		invalidate();
	}
	
	@Override
	protected void onDraw( Canvas canvas ) {
		canvas.drawPaint(mBackgroundPaint);

		mMatrix.setTranslate(mOffset.x, mOffset.y);
		mPath.transform(mMatrix);
		mMatrix.setScale(mScale, mScale, rotationCenter.x, rotationCenter.y);
		mPath.transform(mMatrix);
		mMatrix.setRotate(mAngle, rotationCenter.x, rotationCenter.y);
		mPath.transform(mMatrix);
		
		canvas.drawPath(mPath, mBoxPaint);
		
		if( mCurrentBox != null ) {
			canvas.drawRect(mCurrentBox.left, mCurrentBox.top, mCurrentBox.right, mCurrentBox.bottom, mBoxPaint);
		}
	}

	private void stopTransformPath() {
		mAngle = 0;
		mOffset = new PointF(0, 0);
		mScale = 1;
		addCurrentBox();
	}

	private void addCurrentBox() {
		if (mCurrentBox != null) {
			mPath.addRect(mCurrentBox.left, mCurrentBox.top, mCurrentBox.right, mCurrentBox.bottom, Path.Direction.CW);
			mCurrentBox = null;
		}
	}
	
	private void addNewBox(PointF origin) {
		mCurrentBox = new Box(origin);
		invalidate();
	}
	
	private void invalidateCurrentBox(PointF current) {
		mCurrentBox.setCurrent(current);
		invalidate();
	}
	
	private void removeCurrentBox() {
		mCurrentBox = null;
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable( "instance", super.onSaveInstanceState() );
		
		addCurrentBox();
		bundle.putSerializable("path", mPath);
		
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle) state;
		Parcelable p = bundle.getParcelable("instance");
		
		mPath = (DrawPath) bundle.getSerializable("path");
				
		super.onRestoreInstanceState(p);
	}
}
