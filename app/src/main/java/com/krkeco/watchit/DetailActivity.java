package com.krkeco.watchit;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class DetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

				setContentView(R.layout.detail_container);

/*
				Fragment fg = new DetailMovie();
				int container = R.id.det_container;
				String tag = null;

				getSupportFragmentManager()
						.beginTransaction()
						.replace(container,fg,tag)
						.addToBackStack("tag")
						.commit();
				*/
    }
		
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }
		public void toast(String string) {
				Context context = getApplicationContext();
				CharSequence text = string;
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.show();
		}

}
