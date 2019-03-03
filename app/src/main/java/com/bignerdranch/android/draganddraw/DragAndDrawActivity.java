package com.bignerdranch.android.draganddraw;

import android.app.*;
import android.os.*;
import android.util.*;

public class DragAndDrawActivity extends SingleFragmentActivity {
	@Override
	protected Fragment createFragment() {
		return DragAndDrawFragment.newInstance();
	}
}
