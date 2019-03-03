package com.bignerdranch.android.draganddraw;

import android.graphics.*;
import java.io.*;

public class Box implements Serializable{
	private PointF mOrigin;
	private PointF mCurrent;
	
	public float left;
	public float right;
	public float top;
	public float bottom;
	
	public Box(Box box){
		mOrigin = box.getOrigin();
		mCurrent = box.getCurrent();
		left = Math.min( box.getOrigin().x, box.getCurrent().x );
		right = Math.max( box.getOrigin().x, box.getCurrent().x );
		top = Math.min( box.getOrigin().y, box.getCurrent().y );
		bottom = Math.max( box.getOrigin().y, box.getCurrent().y );
	}
	
	public Box(PointF origin) {
		mOrigin = origin;
		mCurrent = origin;
		left = 0;
		right = 0;
		top = 0;
		bottom = 0;
	}

	public void setCurrent( PointF current ) {
		mCurrent = current;
		left = Math.min( mOrigin.x, mCurrent.x );
		right = Math.max( mOrigin.x, mCurrent.x );
		top = Math.min( mOrigin.y, mCurrent.y );
		bottom = Math.max( mOrigin.y, mCurrent.y );
	}

	public PointF getCurrent() {
		return mCurrent;
	}

	public void setOrigin( PointF origin ) {
		mOrigin = origin;
		left = Math.min( mOrigin.x, mCurrent.x );
		right = Math.max( mOrigin.x, mCurrent.x );
		top = Math.min( mOrigin.y, mCurrent.y );
		bottom = Math.max( mOrigin.y, mCurrent.y );
	}

	public PointF getOrigin() {
		return mOrigin;
	}	
}
