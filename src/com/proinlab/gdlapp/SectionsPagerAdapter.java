package com.proinlab.gdlapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

	private Context mContext;
	
	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = new ListFragment();
		Bundle args = new Bundle();
		args.putInt(ListFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return mContext.getString(R.string.title_section1);
		case 1:
			return mContext.getString(R.string.title_section2);
		case 2:
			return mContext.getString(R.string.title_section3);
		}
		return null;
	}
}
