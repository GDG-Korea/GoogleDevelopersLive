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
	private String[] catelist;

	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mContext = context;
		catelist = mContext.getResources().getStringArray(R.array.category);

	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = new ListFragment();
		Bundle args = new Bundle();
		args.putString(ListFragment.ARG_SECTION_CATE, catelist[position]);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return catelist.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if (position < catelist.length)
			return catelist[position];
		else
			return null;
	}
}
