package com.bignerdranch.android.draganddraw;

import android.view.*;
import android.content.*;
import android.util.*;
import android.graphics.*;
import java.util.*;
import android.os.*;

public class BoxDrawningView extends View {
	private Box mCurrentBox;
	private List<Box> mBoxen = new ArrayList<>();
	private Paint mBoxPaint;
	private Paint mBackgroundPaint;
	private Path mPath;
	private Matrix mMatrix;
	private PointF vectorO;
	private PointF rotationCenter = new PointF(0,0);
	private PointF scaleCenter = new PointF(0,0);
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
		
		mMatrix = new Matrix();
		mPath = new Path();
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		PointF current = new PointF( event.getX(0), event.getY(0) );
		
		switch( event.getAction() ) {
			case MotionEvent.ACTION_DOWN:
				addCurrentBox(current);
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
				Log.i("","cancel");
				return true;
			case MotionEvent.ACTION_POINTER_2_DOWN:
				removeCurrentBox();
				vectorO = new PointF( event.getX(0) - event.getX(1),
									event.getY(0) - event.getY(1) );
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
		mPath.reset();

		for( Box box : mBoxen ) {
			box.setAngle(box.getAngle() + mAngle);
			box.scale = box.scale * mScale;
			box.Offset = new PointF(box.Offset.x + mOffset.x,
									box.Offset.y + mOffset.y);
			box.rotationC = rotationCenter;
			
			mMatrix.setRotate(-box.getAngle(), box.rotationC.x, box.rotationC.y);
			mPath.transform(mMatrix);
			mMatrix.setScale(1/box.scale, 1/box.scale, box.rotationC.x, box.rotationC.y);
			mPath.transform(mMatrix);
			mMatrix.setTranslate(-box.Offset.x, -box.Offset.y);
			mPath.transform(mMatrix);
			
			mPath.addRect( box.left, box.top, box.right, box.bottom, Path.Direction.CW );
			
			mMatrix.setTranslate(box.Offset.x, box.Offset.y);
			mPath.transform(mMatrix);
			mMatrix.setScale(box.scale, box.scale, box.rotationC.x, box.rotationC.y);
			mPath.transform(mMatrix);
			mMatrix.setRotate(box.getAngle(), box.rotationC.x, box.rotationC.y);
			mPath.transform(mMatrix);
		}
		
		canvas.drawPath(mPath, mBoxPaint);
	}

	private void stopTransformPath() {
		mAngle = 0;
		mOffset = new PointF(0, 0);
		mScale = 1;
	}
	
	private void addCurrentBox(PointF origin) {
		mCurrentBox = new Box(origin);
		mBoxen.add(mCurrentBox);
		invalidate();
	}
	
	private void invalidateCurrentBox(PointF current) {
		mCurrentBox.setCurrent(current);
		invalidate();
	}
	
	private void removeCurrentBox() {
		mBoxen.remove(mCurrentBox);
		mCurrentBox = null;
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable( "instance", super.onSaveInstanceState() );
		
		bundle.putInt( "size", mBoxen.size() );
		int i = 0;
		for( Box box : mBoxen ) {
			bundle.putSerializable( "item" + i, box);
			++i;
		}
		
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle) state;
		Parcelable p = bundle.getParcelable("instance");
		
		int arraySize = bundle.getInt("size", 0);
		mBoxen = new ArrayList<>();
		
		for(int i = 0; i < arraySize; i++){
			Box box = (Box) bundle.getSerializable( "item" + i );
			mBoxen.add(box);
		}
				
		super.onRestoreInstanceState(p);
	}
}
