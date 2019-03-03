package com.bignerdranch.android.draganddraw;

import android.os.Bundle;
import android.app.*;
import android.util.*;
import android.view.*;

public abstract class SingleFragmentActivity extends Activity
{
    protected abstract Fragment createFragment();
	
	protected int getLayoutResId() {
		return R.layout.activity_fragment;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView( getLayoutResId() );

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById( R.id.fragment_container );

        if(fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment ).commit();
        }
    }
}
