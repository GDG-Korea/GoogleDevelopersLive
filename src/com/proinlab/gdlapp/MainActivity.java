package com.proinlab.gdlapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity implements
		ActionBar.TabListener {

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private int CurrentItem = 0;

	private ViewPager mViewPager;
	public static AlertDialog alert = null;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			mViewPager.setCurrentItem(CurrentItem + 1);
			CurrentItem++;
			if (CurrentItem < mSectionsPagerAdapter.getCount()) {
				mViewPager.setCurrentItem(CurrentItem + 1);
				CurrentItem++;
			} 
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (CurrentItem != 0) {
				mViewPager.setCurrentItem(CurrentItem - 1);
				CurrentItem--;
			}
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowCustomEnabled(true);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), this);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		// YouTube Dialog
		LinearLayout linear = (LinearLayout) View.inflate(this,
				R.layout.loading_dialog, null);
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setView(linear);
		alert = alt_bld.create();
		alert.setCancelable(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_developer:
			intent = new Intent(this, DevInfo.class);
			startActivity(intent);
			return true;
		case R.id.menu_schedule:
			intent = new Intent(this, Schedule.class);
			startActivity(intent);
			return true;
		}
		return false;
	}

	@Override
	public void onTabSelected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
		CurrentItem = tab.getPosition();
	}

	@Override
	public void onTabUnselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
	}
}
