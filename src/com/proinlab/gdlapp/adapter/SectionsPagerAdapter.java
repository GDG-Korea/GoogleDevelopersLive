package com.proinlab.gdlapp.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.proinlab.gdlapp.fragment.ListFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

	private String[] catelist;

	public SectionsPagerAdapter(FragmentManager fm, String[] catelist) {
		super(fm);
		this.catelist = catelist;
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
