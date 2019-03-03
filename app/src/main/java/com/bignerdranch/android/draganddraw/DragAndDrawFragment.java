package com.bignerdranch.android.draganddraw;

import android.view.*;
import android.os.*;
import android.app.*;
import android.util.*;

public class DragAndDrawFragment extends Fragment {
	BoxDrawningView mBoxView;
	
	public static DragAndDrawFragment newInstance() {
		return new DragAndDrawFragment();
	}
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View v = inflater.inflate( R.layout.fragment_drag_and_draw, container, false );
		mBoxView = v.findViewById(R.id.box_view);
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable( "box", mBoxView.onSaveInstanceState() );
		super.onSaveInstanceState(outState);
	}
}
